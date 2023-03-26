package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketPlayerListHeaderAndFooter extends Packet {
    public static final int ID = 0x47;

    private String header;
    private String footer;

    public PacketPlayerListHeaderAndFooter(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        header = readString();
        footer = readString();
    }

    @Override
    public void write() {
        writeString(header);
        writeString(footer);
    }
}
