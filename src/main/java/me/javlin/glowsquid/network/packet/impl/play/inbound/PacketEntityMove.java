package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketEntityMove extends Packet {
    public static final int ID = 0x15;

    protected int entityId;

    protected byte dx;
    protected byte dy;
    protected byte dz;

    protected boolean onGround;

    public PacketEntityMove(int entityId, byte dx, byte dy, byte dz, boolean onGround, boolean is18) {
        this.entityId = entityId;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.onGround = onGround;
        this.is18 = is18;
    }

    public PacketEntityMove(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void write() {
        if (is18) {
            writeVarInt(entityId);
        } else {
            writeInt(entityId);
        }

        writeByte(dx);
        writeByte(dy);
        writeByte(dz);

        if (is18) {
            writeBoolean(onGround);
        }
    }

    @Override
    public void read() {
        if(is18)
            entityId = readVarInt();
        else
            entityId = readInt();

        dx = readByte();
        dy = readByte();
        dz = readByte();

        if(is18)
            onGround = readBoolean();
    }
}
