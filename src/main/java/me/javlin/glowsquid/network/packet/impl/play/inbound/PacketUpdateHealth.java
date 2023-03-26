package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketUpdateHealth extends Packet {
    public static final int ID = 0x06;

    private float health;
    private int food;
    private float foodSaturation;

    public PacketUpdateHealth(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        health = readFloat();
        food = readVarInt();
        foodSaturation = readFloat();
    }

    @Override
    public void write() {
        writeFloat(health);
        writeVarInt(food);
        writeFloat(foodSaturation);
    }
}

