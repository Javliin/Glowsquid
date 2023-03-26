package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketSetExperience extends Packet {
    public static final int ID = 0x1F;

    private int level;
    private int totalExperience;

    private float experiencebar;

    public PacketSetExperience(PacketDecoder packet) {
        super(packet);
    }

    public PacketSetExperience(int level, int totalExperience, float experiencebar) {
        this.level = level;
        this.totalExperience = totalExperience;
        this.experiencebar = experiencebar;
    }

    @Override
    public void read() {
        level = readVarInt();
        totalExperience = readVarInt();
        experiencebar = readFloat();
    }

    @Override
    public void write() {
        writeVarInt(level);
        writeVarInt(totalExperience);
        writeFloat(experiencebar);
    }
}