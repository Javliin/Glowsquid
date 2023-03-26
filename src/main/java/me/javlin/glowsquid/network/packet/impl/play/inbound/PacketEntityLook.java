package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketEntityLook extends Packet {
    public static final int ID = 0x16;

    private int entityId;

    private byte yaw;
    private byte pitch;

    private boolean onGround;

    public PacketEntityLook(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        entityId = readVarInt();
        yaw = readByte();
        pitch = readByte();
        onGround = readBoolean();
    }

    @Override
    public void write() {
        writeVarInt(entityId);
        writeByte(yaw);
        writeByte(pitch);
        writeBoolean(onGround);
    }
}
