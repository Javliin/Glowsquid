package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketEntityVelocity extends Packet {
    public static final int ID = 0x12;

    private int entityId;

    private short velocityX;
    private short velocityY;
    private short velocityZ;

    public PacketEntityVelocity(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        entityId = readVarInt();
        velocityX = readShort();
        velocityY = readShort();
        velocityZ = readShort();
    }

    @Override
    public void write() {
        writeVarInt(entityId);
        writeShort(velocityX);
        writeShort(velocityY);
        writeShort(velocityZ);
    }
}
