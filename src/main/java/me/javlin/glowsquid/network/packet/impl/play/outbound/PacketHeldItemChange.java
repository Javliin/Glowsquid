package me.javlin.glowsquid.network.packet.impl.play.outbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketHeldItemChange extends Packet {
    public static final int ID = 0x09;

    private short item;

    public PacketHeldItemChange(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        item = readShort();
    }

    @Override
    public void write() {
        writeShort(item);
    }
}
