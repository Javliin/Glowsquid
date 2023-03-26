package me.javlin.glowsquid.network.packet.impl.login.inbound;

import lombok.Getter;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketLoginSuccess extends Packet {
    public static final int ID = 0x02;

    private String uuid;
    private String username;

    public PacketLoginSuccess(PacketDecoder packet) {
        super(packet);
    }

    public PacketLoginSuccess(String uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    @Override
    public void read() {
        uuid = readString();
        username = readString();
    }

    @Override
    public void write() {
        writeString(uuid);
        writeString(username);
    }
}