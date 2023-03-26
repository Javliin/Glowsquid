package me.javlin.glowsquid.network.packet.impl.play.outbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketSteerVehicle extends Packet {
    public static final int ID = 0x0C;

    private float sideways;
    private float forward;

    private byte flags;

    public PacketSteerVehicle(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        sideways = readFloat();
        forward = readFloat();
        flags = readByte();
    }

    @Override
    public void write() {
        writeFloat(sideways);
        writeFloat(forward);
        writeByte(flags);
    }
}
