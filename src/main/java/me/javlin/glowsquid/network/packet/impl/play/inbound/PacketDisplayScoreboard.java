package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketDisplayScoreboard extends Packet {
    public static final int ID = 0x3D;

    private byte position;

    private String scoreName;

    public PacketDisplayScoreboard(PacketDecoder packet) {
        super(packet);
    }

    public PacketDisplayScoreboard(byte position, String scoreName) {
        this.position = position;
        this.scoreName = scoreName;
    }

    @Override
    public void read() {
        position = readByte();
        scoreName = readString();
    }

    @Override
    public void write() {
        writeByte(position);
        writeString(scoreName);
    }
}