package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketEntity extends Packet {
    public static final int ID = 0x14;

    private int entityId;

    public PacketEntity(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        entityId = readVarInt();
    }

    @Override
    public void write() {
        writeVarInt(entityId);
    }
}
