package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketSpawnExperienceOrb extends Packet {
    public static final int ID = 0x11;

    private int entityId;
    private int x;
    private int y;
    private int z;

    private short count;

    public PacketSpawnExperienceOrb(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        entityId = readVarInt();
        x = readInt();
        y = readInt();
        z = readInt();
        count = readShort();
    }

    @Override
    public void write() {
        writeVarInt(entityId);
        writeInt(x);
        writeInt(y);
        writeInt(z);
        writeShort(count);
    }
}
