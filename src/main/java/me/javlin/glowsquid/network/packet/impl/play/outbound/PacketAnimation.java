package me.javlin.glowsquid.network.packet.impl.play.outbound;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

public class PacketAnimation extends Packet {
    public static final int ID = 0x0A;

    public PacketAnimation(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        // No fields
    }

    @Override
    public void write()  {
        // No fields
    }
}
