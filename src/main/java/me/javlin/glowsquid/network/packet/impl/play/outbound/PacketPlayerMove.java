package me.javlin.glowsquid.network.packet.impl.play.outbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketPlayerMove extends Packet {
    public static final int ID = 0x04;

    protected double x;
    protected double y;
    protected double z;

    protected boolean onGround;

    public PacketPlayerMove(PacketDecoder packet) {
        super(packet);
    }

    public PacketPlayerMove(double x, double y, double z, boolean onGround, boolean is18) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.onGround = onGround;
        this.is18 = is18;
    }

    @Override
    public void read() {
        x = readDouble();
        y = readDouble();

        if (!is18) {
            readDouble();
        }

        z = readDouble();

        onGround = readBoolean();
    }

    @Override
    public void write() {
        writeDouble(x);
        writeDouble(y);

        if(!is18)
            writeDouble(y + 1.62);

        writeDouble(z);

        writeBoolean(onGround);
    }
}
