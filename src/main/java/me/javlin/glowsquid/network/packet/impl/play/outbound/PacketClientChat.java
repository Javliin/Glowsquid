package me.javlin.glowsquid.network.packet.impl.play.outbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

public class PacketClientChat extends Packet {
    public static final int ID = 0x01;

    @Getter
    private String message;

    public PacketClientChat(String message) {
        this.message = message;
    }

    public PacketClientChat(PacketDecoder packet) {
        super(packet);
    }

    public void read() {
        message = readString();
    }

    public void write() {
        writeString(message);
    }
}
