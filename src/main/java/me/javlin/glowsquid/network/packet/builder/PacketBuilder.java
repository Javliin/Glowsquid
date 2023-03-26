package me.javlin.glowsquid.network.packet.builder;

import lombok.Getter;
import me.javlin.glowsquid.Console;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.PacketInfo;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PacketBuilder {
    private static final Map<PacketInfo.PacketState, PacketEntry> PACKET_CONSTRUCTORS = new HashMap<>();
    private static final Map<Class<? extends Packet>, Integer> PACKET_IDS = new HashMap<>();

    private final boolean is18;

    private Map<Integer, Constructor<? extends Packet>> constructorCache;

    private PacketInfo.PacketState state;
    private PacketInfo.PacketDirection direction;

    private int compressionThreshold;

    public PacketBuilder(PacketInfo.PacketState state, PacketInfo.PacketDirection direction, int compressionThreshold, boolean is18) {
        this.state = state;
        this.direction = direction;
        this.constructorCache = PACKET_CONSTRUCTORS.get(state).get(direction);
        this.compressionThreshold = compressionThreshold;
        this.is18 = is18;
    }

    public PacketBuilder state(PacketInfo.PacketState state) {
        this.state = state;
        this.constructorCache = PACKET_CONSTRUCTORS.get(state).get(direction);
        return this;
    }

    public PacketBuilder direction(PacketInfo.PacketDirection direction) {
        this.direction = direction;
        this.constructorCache = PACKET_CONSTRUCTORS.get(state).get(direction);
        return this;
    }

    public PacketBuilder compression(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
        return this;
    }

    public PacketDecoder read(byte[] data) {
        return new PacketDecoder(this, data);
    }

    public PacketEncoder write(Packet packet) {
        return new PacketEncoder(this, packet);
    }

    public static Map<Class<? extends Packet>, Integer> getPacketIds() {
        return PACKET_IDS;
    }

    public static void registerPacketClasses() {
        for (PacketInfo.PacketState state : PacketInfo.PacketState.values()) {
            PacketEntry entry = new PacketEntry();

            for (PacketInfo.PacketDirection direction : PacketInfo.PacketDirection.values()) {
                Map<Integer, Constructor<? extends Packet>> map;
                Map<Integer, Class<? extends Packet>> packetClasses;

                if (direction == PacketInfo.PacketDirection.OUTBOUND) {
                    map = entry.getClient();
                    packetClasses = state.getClientClasses();
                } else {
                    map = entry.getServer();
                    packetClasses = state.getServerClasses();
                }

                for (Map.Entry<Integer, Class<? extends Packet>> packetEntry : packetClasses.entrySet()) {
                    Class<? extends Packet> packetClass = packetEntry.getValue();
                    int id = packetEntry.getKey();

                    try {
                        map.put(id, packetClass.getConstructor(PacketDecoder.class));
                        PACKET_IDS.put(packetClass, id);
                    } catch (NoSuchMethodException exception) {
                        Console.error("FAIL_GRAB_HEADER", packetClass.getName());
                        exception.printStackTrace();
                    }
                }

                PACKET_CONSTRUCTORS.put(state, entry);
            }
        }
    }

    @Getter
    private static class PacketEntry {
        private final Map<Integer, Constructor<? extends Packet>> client = new ConcurrentHashMap<>();
        private final Map<Integer, Constructor<? extends Packet>> server = new ConcurrentHashMap<>();

        public Map<Integer, Constructor<? extends Packet>> get(PacketInfo.PacketDirection direction) {
            return direction == PacketInfo.PacketDirection.OUTBOUND ? client : server;
        }
    }
}
