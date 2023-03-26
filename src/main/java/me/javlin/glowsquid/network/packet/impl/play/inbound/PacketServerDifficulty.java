package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketServerDifficulty extends Packet {
    public static final int ID = 0x41;

    private byte difficulty;

    public PacketServerDifficulty(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        difficulty = readByte();
    }

    @Override
    public void write() {
        writeByte(difficulty);
    }
}