package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketEntityHeadLook extends Packet {
    public static final int ID = 0x19;

    private int entityId;

    private byte headYaw;

    public PacketEntityHeadLook(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        entityId = readVarInt();
        headYaw = readByte();
    }

    @Override
    public void write() {
        writeVarInt(entityId);
        writeByte(headYaw);
    }
}
