package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketEntityEffect extends Packet {
    public static final int ID = 0x1D;

    private int entityID;

    private boolean hideParticles;

    private int duration;

    private byte amplifier;
    private byte effectID;

    public PacketEntityEffect(PacketDecoder packet) {
        super(packet);
    }

    public PacketEntityEffect(int entityID, boolean hideParticles, int duration, byte amplifier, byte effectID) {
        this.entityID = entityID;
        this.hideParticles = hideParticles;
        this.duration = duration;
        this.amplifier = amplifier;
        this.effectID = effectID;
    }

    @Override
    public void read() {
        entityID = readVarInt();
        effectID = readByte();
        amplifier = readByte();
        duration = readVarInt();
        hideParticles = readBoolean();
    }

    @Override
    public void write() {
        writeVarInt(entityID);
        writeByte(effectID);
        writeByte(amplifier);
        writeVarInt(duration);
        writeBoolean(hideParticles);
    }
}