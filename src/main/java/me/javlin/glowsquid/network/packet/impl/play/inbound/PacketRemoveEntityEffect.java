package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketRemoveEntityEffect extends Packet {
    public static final int ID = 0x1E;

    private int entityID;

    private byte effectID;

    public PacketRemoveEntityEffect(PacketDecoder packet) {
        super(packet);
    }

    public PacketRemoveEntityEffect(int entityID, byte effectID) {
        this.entityID = entityID;
        this.effectID = effectID;
    }

    @Override
    public void read() {
        entityID = readVarInt();
        effectID = readByte();
    }

    @Override
    public void write() {
        writeVarInt(entityID);
        writeByte(effectID);
    }
}