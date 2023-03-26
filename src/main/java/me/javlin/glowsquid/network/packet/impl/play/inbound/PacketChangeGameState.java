package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketChangeGameState extends Packet {
    public static final int ID = 0x2B;

    private float value;

    private byte reason;

    public PacketChangeGameState(PacketDecoder packet) {
        super(packet);
    }

    public PacketChangeGameState(float value, byte reason) {
        this.value = value;
        this.reason = reason;
    }

    @Override
    public void read() {
        value = readFloat();
        reason = readByte();
    }

    @Override
    public void write() {
        writeFloat(value);
        writeByte(reason);
    }
}