package me.javlin.glowsquid.network.packet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.javlin.glowsquid.Console;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class PacketInfo {
    private final PacketState state;
    private final PacketDirection direction;
    private final int ID;

    @Getter
    public enum PacketState {
        HANDSHAKING, LOGIN, PLAY, STATUS;

        private final Map<Integer, Class<? extends Packet>> clientClasses;
        private final Map<Integer, Class<? extends Packet>> serverClasses;

        PacketState() {
            String packetPath = PacketInfo.class.getPackage().getName() + ".impl";
            String currentPackage = packetPath + "." + name().toLowerCase();

            Reflections clientReflections = new Reflections(currentPackage + "." + PacketDirection.OUTBOUND.name().toLowerCase());
            Reflections serverReflections = new Reflections(currentPackage + "." + PacketDirection.INBOUND.name().toLowerCase());

            Map<Integer, Class<? extends Packet>> classes = new HashMap<>();

            Set<Class<? extends Packet>> multiClasses = new Reflections(currentPackage).getSubTypesOf(Packet.class);
            Set<Class<? extends Packet>> client = clientReflections.getSubTypesOf(Packet.class);
            Set<Class<? extends Packet>> server = serverReflections.getSubTypesOf(Packet.class);

            multiClasses.removeAll(client);
            multiClasses.removeAll(server);
            
            client.addAll(multiClasses);
            server.addAll(multiClasses);

            clientClasses = loadPackets(client);
            serverClasses = loadPackets(server);
        }

        private Map<Integer, Class<? extends Packet>> loadPackets(Set<Class<? extends Packet>> packets) {
            Map<Integer, Class<? extends Packet>> packetMap = new HashMap<>();

            for (Class<? extends Packet> clazz : packets) {
                try {
                    packetMap.put((Integer) clazz.getField("ID").get(null), clazz);
                } catch (ClassCastException | IllegalAccessException | NoSuchFieldException exception) {
                    Console.error("PACKET_FAIL_LOAD", clazz.getSimpleName());
                    exception.printStackTrace();
                }
            }

            return packetMap;
        }
    }

    public enum PacketDirection {
        OUTBOUND, INBOUND
    }
}
