package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketEntityStatus extends Packet {
    public static final int ID = 0x1A;

    private int entityID;

    private byte entityStatus;

    public PacketEntityStatus(PacketDecoder packet) {
        super(packet);
    }

    public PacketEntityStatus(int entityID, byte entityStatus) {
        this.entityID = entityID;
        this.entityStatus = entityStatus;
    }

    @Override
    public void read() {
        entityID = readInt();
        entityStatus = readByte();
    }

    @Override
    public void write() {
        writeInt(entityID);
        writeByte(entityStatus);
    }
}