package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketCloseWindow extends Packet {
    public static final int ID = 0x2E;

    private byte windowId;

    public PacketCloseWindow(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        windowId = readByte();
    }

    @Override
    public void write() {
        writeByte(windowId);
    }
}
