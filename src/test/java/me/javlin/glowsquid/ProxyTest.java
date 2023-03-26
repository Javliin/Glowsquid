package me.javlin.glowsquid;

import me.javlin.glowsquid.dummy.DummyProxy;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.PacketInfo;
import me.javlin.glowsquid.network.packet.builder.PacketBuilder;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.reflections.Reflections;

import java.io.*;
import java.util.Map;

public class ProxyTest {
    private static final String PACKET_PATH = PacketInfo.class.getPackage().getName() + ".impl";

    @Test
    public void simulateTraffic() {
        registerPackets();

        for (PacketInfo.PacketDirection direction : PacketInfo.PacketDirection.values()) {
            for (PacketInfo.PacketState state : PacketInfo.PacketState.values()) {
                try (InputStream handshakingClient = getClass().getResourceAsStream(String.format("packetdata/%s/%s", direction.name().toLowerCase(), state.name().toLowerCase()))) {
                    if (handshakingClient == null) {
                        continue;
                    }

                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    ByteArrayOutputStream raw = new ByteArrayOutputStream();
                    DummyProxy proxy = new DummyProxy(
                            handshakingClient,
                            output,
                            raw,
                            new PacketBuilder(
                                    state,
                                    direction,
                                    state == PacketInfo.PacketState.PLAY ? 256 : -1,
                                    true
                            )
                    );

                    proxy.start();
                    Assertions.assertArrayEquals(raw.toByteArray(), output.toByteArray());
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            }
        }
    }

    private void registerPackets() {
        PacketBuilder.registerPacketClasses();
        Map<Class<? extends Packet>, Integer> packetIds = PacketBuilder.getPacketIds();
        Reflections packets = new Reflections(PACKET_PATH);
        packets.getSubTypesOf(Packet.class).forEach(packet -> Assertions.assertTrue(packetIds.containsKey(packet)));
    }
}
