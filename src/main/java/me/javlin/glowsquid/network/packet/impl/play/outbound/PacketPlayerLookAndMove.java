package me.javlin.glowsquid.network.packet.impl.play.outbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketPlayerLookAndMove extends PacketPlayerMove {
    public static final int ID = 0x06;

    protected double x;
    protected double y;
    protected double z;

    private float yaw;
    private float pitch;

    protected boolean onGround;

    public PacketPlayerLookAndMove(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        x = readDouble();
        y = readDouble();

        if(!is18)
            readDouble();

        z = readDouble();

        yaw = readFloat();
        pitch = readFloat();

        onGround = readBoolean();
    }

    @Override
    public void write() {
        writeDouble(x);
        writeDouble(y);

        if(!is18)
            writeDouble(y + 1.62);

        writeDouble(z);

        writeFloat(yaw);
        writeFloat(pitch);

        writeBoolean(onGround);
    }
}
