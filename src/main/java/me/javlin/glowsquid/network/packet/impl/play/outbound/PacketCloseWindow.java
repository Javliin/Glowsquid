package me.javlin.glowsquid.network.packet.impl.play.outbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketCloseWindow extends Packet {
    public static final int ID = 0x0D;

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
