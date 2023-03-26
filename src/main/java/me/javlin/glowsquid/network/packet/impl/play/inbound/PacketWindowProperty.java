package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketWindowProperty extends Packet {
    public static final int ID = 0x31;

    private byte windowId;

    private short property;
    private short value;

    public PacketWindowProperty(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        windowId = readByte();
        property = readShort();
        value = readShort();
    }

    @Override
    public void write() {
        writeByte(windowId);
        writeShort(property);
        writeShort(value);
    }
}
