package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketDisconnect extends Packet {
    public static final int ID = 0x40;

    private String reason;

    public PacketDisconnect(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        reason = readString();
    }

    @Override
    public void write() {
        writeString(reason);
    }
}
