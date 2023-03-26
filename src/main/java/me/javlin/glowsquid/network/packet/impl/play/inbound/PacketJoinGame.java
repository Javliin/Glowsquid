package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketJoinGame extends Packet {
    public static final int ID = 0x01;

    private int entityId;

    private byte gamemode;
    private byte dimension;
    private byte difficulty;
    private byte maxPlayers;

    private String levelType;

    private boolean reducedDebugInfo;

    public PacketJoinGame(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        entityId = readInt();
        gamemode = readByte();
        dimension = readByte();
        difficulty = readByte();
        maxPlayers = readByte();
        levelType = readString();
        reducedDebugInfo = readBoolean();
    }

    @Override
    public void write() {
        writeInt(entityId);

        writeByte(gamemode);
        writeByte(dimension);
        writeByte(difficulty);
        writeByte(maxPlayers);

        writeString(levelType);

        writeBoolean(reducedDebugInfo);
    }
}
