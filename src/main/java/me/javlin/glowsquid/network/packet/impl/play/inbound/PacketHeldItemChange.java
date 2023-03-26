package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketHeldItemChange extends Packet {
    public static final int ID = 0x09;

    private byte slot;

    public PacketHeldItemChange(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        slot = readByte();
    }

    @Override
    public void write() {
        writeByte(slot);
    }
}
