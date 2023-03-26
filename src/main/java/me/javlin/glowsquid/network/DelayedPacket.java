package me.javlin.glowsquid.network;

import lombok.Getter;
import lombok.Setter;
import me.javlin.glowsquid.network.proxy.ProxySession;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.impl.play.inbound.PacketEntityLookAndMove;
import me.javlin.glowsquid.network.packet.impl.play.inbound.PacketEntityMove;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelayedPacket {
    private static final HashMap<Integer, DelayedData> DELAYED_PLAYERS = new HashMap<>();

    private final long sendTime;
    private final Packet packet;

    public DelayedPacket(long sendTime, Packet packet) {
        this.sendTime = sendTime;
        this.packet = packet;
    }

    public long getSendTime() {
        return sendTime;
    }

    public Packet getPacket() {
        return packet;
    }

    public static void splitDelayQueue(ProxySession listener, DelayedType type, PacketEntityMove packetEntityMove) {
        splitDelayQueue(listener, type, 0, packetEntityMove);
    }

    /**
     * Divides a provided entity movement packet into multiple entity movement packets. These packets are
     * then sent to the client with a delay interval between them, in order to produce a smooth and delayed movement.
     * This provides combat advantages when activated under specific conditions. The delay interval between the packets,
     * along with the amount of times a packet is divided are changed as a delay stacks (more movement packets
     * are received during the delay). The longer a delay is continued, the less the packets are divided and the smaller the
     * delay becomes.
     *
     * @param listener Parent ProxyListener of the delayed packet
     * @param type Type of delay to use (there are different delayed packet queues for each type of delay)
     * @param delayOffset Time in milliseconds to begin sending the delayed packets; 0 to use the time of the last entry
     *                    in the delayed packet queue, or if this does not exist, the current time
     * @param packetEntityMove Packet to divide and delay
     */
    public static void splitDelayQueue(ProxySession listener, DelayedType type, long delayOffset, PacketEntityMove packetEntityMove) {
        synchronized (listener.getDelayedPacketQueue()) {
            int entityId = packetEntityMove.getEntityId();

            byte dx = packetEntityMove.getDx();
            byte dy = packetEntityMove.getDy();
            byte dz = packetEntityMove.getDz();

            boolean isLookAndMove = packetEntityMove instanceof PacketEntityLookAndMove;

            DelayedData data = DELAYED_PLAYERS.getOrDefault(entityId, new DelayedData());
            PacketEntityMove splitPacket;

            float interval = Math.max(1, (type.getDelay() / data.getDelayedCount().getOrDefault(type, 1)) / 50); // Amount of times to divide the packet

            if (isLookAndMove) {
                PacketEntityLookAndMove lookAndMove = (PacketEntityLookAndMove) packetEntityMove;

                splitPacket = new PacketEntityLookAndMove(
                        entityId,
                        (byte) Math.round(dx / interval),
                        (byte) Math.round(dy / interval),
                        (byte) Math.round(dz / interval),
                        lookAndMove.getYaw(),
                        lookAndMove.getPitch(),
                        lookAndMove.isOnGround(),
                        listener.is18()
                );
            } else {
                splitPacket = new PacketEntityMove(
                        entityId,
                        (byte) Math.round(dx / interval),
                        (byte) Math.round(dy / interval),
                        (byte) Math.round(dz / interval),
                        packetEntityMove.isOnGround(),
                        listener.is18()
                );
            }

            long sendTime;

            if (dx % interval != 0 || dy % interval != 0 || dz % interval != 0) {
                // As long as one look packet is sent, the remaining split packets only need to include the movement
                PacketEntityMove remainder = new PacketEntityMove(
                        entityId,
                        (byte) (dx % interval),
                        (byte) (dy % interval),
                        (byte) (dz % interval),
                        packetEntityMove.isOnGround(),
                        listener.is18()
                );

                sendTime = delayQueue(listener, type, entityId, delayOffset, splitPacket, remainder);
            } else {
                sendTime = delayQueue(listener, type, entityId, delayOffset, splitPacket);
            }

            data.setLastDelayed(sendTime);

            DELAYED_PLAYERS.put(entityId, data);
        }
    }

    public static void delayQueue(ProxySession listener, DelayedType type, int entityId, Packet packet) {
        delayQueue(listener, type, entityId, 0, packet);
    }

    /**
     * Gradually delays packets a decreasing amount by considering the length the delay has continued
     * @param listener Parent ProxyListener of the delayed packet
     * @param type Type of delay to use (there are different delayed packet queues for each type of delay)
     * @param delayOffset Time in milliseconds to begin sending the delayed packets; 0 to use the time of the last entry
     *                    in the delayed packet queue, or if this does not exist, the current time
     * @param packets Should only be two packets at most, with the delayed packet at the first index, and a unique
     *                packet sent as the 
     */
    public static long delayQueue(ProxySession listener, DelayedType type, int entityId, long delayOffset, Packet... packets) {
        Map<DelayedType, Integer> delayedCount = DELAYED_PLAYERS.get(entityId).getDelayedCount();
        List<DelayedPacket> queue = listener.getDelayedPacketQueue().get(type);

        boolean isEmpty = queue.isEmpty();

        if (isEmpty) {
            // This only gets reset / decreased once the delayed packets have outpaced incoming movement packets
            // This guarantees a decrease in delay times,
            delayedCount.put(type, 1);
        }

        int delay = type.getDelay(); // Delay in milliseconds for the current delay type
        int currentDelayInterval = delayedCount.get(type); // Amount of delays that have been stacked
        int interval = Math.max(1, Math.round((float) (delay / currentDelayInterval) / 50F));  // Amount of times to divide the packet
        int sendDelay = delay / currentDelayInterval / interval; // Delay between each packet (unused if interval is 1)

        Packet packet = packets[0];

        long lastSendTime; // Current time if no packets are delayed, or the time the last delayed packet in the queue will be sent

        if (delayOffset != 0) {
            lastSendTime = delayOffset;
        } else if (!isEmpty) {
            lastSendTime = queue.get(queue.size() - 1).getSendTime();
        } else {
            lastSendTime = System.currentTimeMillis();
        }

        long sendTime = lastSendTime + (delay / currentDelayInterval); // Time to send the final packet

        // Send all packets in intervals between the last send time and the final send time
        for (int index = 1; index < interval; index++) {
            queue.add(new DelayedPacket(lastSendTime + ((long) sendDelay * index), packet));
        }

        if (packets.length > 1) {
            packet = packets[1];
        }

        queue.add(new DelayedPacket(sendTime, packet));
        delayedCount.put(type, delayedCount.get(type) + 1);

        return sendTime;
    }

    public static Long getPlayer(int entityId) {
        Long player = DELAYED_PLAYERS.get(entityId).getLastDelayed();

        if(player != null && player < System.currentTimeMillis()) {
            DELAYED_PLAYERS.remove(entityId);
            return null;
        }

        return player;
    }

    public static void removePlayer(int entityId) {
        DELAYED_PLAYERS.remove(entityId);
    }

    public static boolean shouldBeDelayed(float range, float maxDiff, double originalDistance, double newDistance) {
        return newDistance >= range && originalDistance < range && Math.abs(newDistance - originalDistance) <= maxDiff;
    }

    @Getter
    @Setter
    private static class DelayedData {
        private final HashMap<DelayedType, Integer> delayedCount = new HashMap<>();
        private Long lastDelayed;
    }
}

