package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketOpenWindow extends Packet {
    public static final int ID = 0x2D;

    private byte windowId;

    private String windowType;
    private String windowTitle;

    private byte slots;

    public PacketOpenWindow(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        windowId = readByte();
        windowType = readString();
        windowTitle = readString();
        slots = readByte();
    }

    @Override
    public void write() {
        writeByte(windowId);
        writeString(windowType);
        writeString(windowTitle);
        writeByte(slots);
    }
}
