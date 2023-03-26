package me.javlin.glowsquid.dummy;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.event.PacketEvent;
import me.javlin.glowsquid.network.packet.event.PacketSendEvent;
import me.javlin.glowsquid.network.packet.impl.play.inbound.PacketServerChat;
import me.javlin.glowsquid.network.packet.impl.play.outbound.PacketClientChat;

public class DummyListener {
    public int called;
    public int events;

    @PacketEvent
    public void onClientChat(PacketSendEvent<PacketClientChat> event) {
        called++;
    }

    @PacketEvent
    public void onServerChat(PacketSendEvent<PacketServerChat> event) {
        called++;
    }

    @PacketEvent
    public void onPacket(PacketSendEvent<Packet> event) {
        events++;
    }
}
