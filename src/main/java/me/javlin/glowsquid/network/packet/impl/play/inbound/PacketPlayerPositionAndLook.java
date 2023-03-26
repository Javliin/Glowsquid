package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketPlayerPositionAndLook extends Packet {
    public static final int ID = 0x08;

    private double x;
    private double y;
    private double z;

    private float yaw;
    private float pitch;

    private byte flags;

    public PacketPlayerPositionAndLook(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        x = readDouble();
        y = readDouble();
        z = readDouble();
        yaw = readFloat();
        pitch = readFloat();
        flags = readByte();
    }

    @Override
    public void write() {
        writeDouble(x);
        writeDouble(y);
        writeDouble(z);

        writeFloat(yaw);
        writeFloat(pitch);

        writeByte(flags);
    }
}
