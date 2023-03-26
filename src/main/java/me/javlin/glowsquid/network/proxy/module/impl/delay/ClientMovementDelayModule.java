package me.javlin.glowsquid.network.proxy.module.impl.delay;

import me.javlin.glowsquid.mc.Player;
import me.javlin.glowsquid.network.DelayedPacket;
import me.javlin.glowsquid.network.DelayedType;
import me.javlin.glowsquid.network.proxy.module.Module;
import me.javlin.glowsquid.network.proxy.module.ModuleManager;
import me.javlin.glowsquid.network.packet.event.PacketEvent;
import me.javlin.glowsquid.network.packet.event.PacketSendEvent;
import me.javlin.glowsquid.network.packet.impl.play.outbound.PacketPlayerMove;

import java.util.List;

public class ClientMovementDelayModule extends Module {
    private static final float RANGE = 3.9F;
    private static final float MAX_DIFF = 0.7F;

    public ClientMovementDelayModule(ModuleManager manager) {
        super(manager);
    }

    @PacketEvent
    public void onPlayerMove(PacketSendEvent<PacketPlayerMove> event) {
        processMove(event);
    }

    private void processMove(PacketSendEvent<PacketPlayerMove> event) {
        PacketPlayerMove playerMove = event.getPacket();

        processMove(new PacketPlayerMove(
                playerMove.getX(),
                playerMove.getY(),
                playerMove.getZ(),
                playerMove.isOnGround(),
                false
        ), event);
    }

    private void processMove(PacketPlayerMove playerMove, PacketSendEvent<PacketPlayerMove> event) {
        Player potentialPlayer = new Player(playerMove.getX(), playerMove.getY(), playerMove.getZ());

        synchronized (delayedPacketQueues) {
            List<DelayedPacket> firstDelayed = delayedPacketQueues.get(DelayedType.PLAYER_RANGE);

            if (!firstDelayed.isEmpty() || checkPlayerDelayed(potentialPlayer)) {
                DelayedPacket.delayQueue(session, DelayedType.PLAYER_RANGE, player.getEntityId(), playerMove);

                event.setCancelled(true);
            }
        }

        player.setPos(potentialPlayer.getX(), potentialPlayer.getY(), potentialPlayer.getZ());
    }

    private boolean checkPlayerDelayed(Player potentialPlayer) {
        double distance, potentialDistance;

        for (Player player : playerList.values()) {
            distance = this.player.distanceTo(player);
            potentialDistance = potentialPlayer.distanceTo(player);

            if (DelayedPacket.shouldBeDelayed(RANGE, MAX_DIFF, potentialDistance, distance)) {
                return true;
            }
        }

        return false;
    }
}
