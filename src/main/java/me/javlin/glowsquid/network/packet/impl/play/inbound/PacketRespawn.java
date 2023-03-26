package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketRespawn extends Packet {
    public static final int ID = 0x07;

    private int dimension;
    private byte difficulty;
    private byte gamemode;

    private String levelType;

    public PacketRespawn(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        dimension = readInt();
        difficulty = readByte();
        gamemode = readByte();
        levelType = readString();
    }

    @Override
    public void write() {
        writeInt(dimension);
        writeByte(difficulty);
        writeByte(gamemode);
        writeString(levelType);
    }
}

