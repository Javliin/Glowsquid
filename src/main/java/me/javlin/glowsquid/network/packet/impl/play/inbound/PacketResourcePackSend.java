package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketResourcePackSend extends Packet {
    public static final int ID = 0x48;

    private String hash;
    private String url;

    public PacketResourcePackSend(PacketDecoder packet) {
        super(packet);
    }

    public PacketResourcePackSend(String hash, String url) {
        this.hash = hash;
        this.url = url;
    }

    @Override
    public void read() {
        hash = readString();
        url = readString();
    }

    @Override
    public void write() {
        writeString(hash);
        writeString(url);
    }
}