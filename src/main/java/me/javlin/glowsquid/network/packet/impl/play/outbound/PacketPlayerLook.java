package me.javlin.glowsquid.network.packet.impl.play.outbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketPlayerLook extends Packet {
    public static final int ID = 0x05;

    private float yaw;
    private float pitch;

    private boolean onGround;

    public PacketPlayerLook(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        yaw = readFloat();
        pitch = readFloat();
        onGround = readBoolean();
    }

    @Override
    public void write() {
        writeFloat(yaw);
        writeFloat(pitch);
        writeBoolean(onGround);
    }
}
