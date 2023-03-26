package me.javlin.glowsquid.network.packet.impl.login.inbound;

import lombok.Getter;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketSetCompression extends Packet {
    public static final int ID = 0x03;

    private int threshold;

    public PacketSetCompression(PacketDecoder packet) {
        super(packet);
    }

    public PacketSetCompression(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public void read() {
        threshold = readVarInt();
    }

    @Override
    public void write() {
        writeVarInt(threshold);
    }
}