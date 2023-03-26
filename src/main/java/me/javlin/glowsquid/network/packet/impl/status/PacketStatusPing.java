package me.javlin.glowsquid.network.packet.impl.status;

import lombok.AllArgsConstructor;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@AllArgsConstructor
public class PacketStatusPing extends Packet {
    public static final int ID = 0x01;

    private long payload;

    public PacketStatusPing(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        payload = readLong();
    }

    @Override
    public void write() {
        writeLong(payload);
    }
}
