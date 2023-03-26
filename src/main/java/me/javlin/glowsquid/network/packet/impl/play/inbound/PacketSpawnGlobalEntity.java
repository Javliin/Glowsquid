package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketSpawnGlobalEntity extends Packet {
    public static final int ID = 0x2C;

    private int entityID;
    private int x;
    private int y;

    private byte type;

    private int z;

    public PacketSpawnGlobalEntity(PacketDecoder packet) {
        super(packet);
    }

    public PacketSpawnGlobalEntity(int entityID, int x, int y, byte type, int z) {
        this.entityID = entityID;
        this.x = x;
        this.y = y;
        this.type = type;
        this.z = z;
    }

    @Override
    public void read() {
        entityID = readVarInt();

        x = readInt();
        y = readInt();

        type = readByte();

        z = readInt();
    }

    @Override
    public void write() {
        writeVarInt(entityID);
        writeInt(x);
        writeInt(y);

        writeByte(type);

        writeInt(z);
    }
}