package me.javlin.glowsquid.network.packet.impl.status.outbound;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

public class PacketStatusRequest extends Packet {
    public static final int ID = 0x00;

    public PacketStatusRequest(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
       // No fields
    }

    @Override
    public void write() {
        // No fields
    }
}
