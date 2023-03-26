package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketEntityTeleport extends Packet {
    public static final int ID = 0x18;

    protected int entityId;

    protected int x;
    protected int y;
    protected int z;
    protected byte yaw;
    protected byte pitch;

    protected boolean onGround;


    public PacketEntityTeleport(int entityId, int x, int y, int z, byte yaw, byte pitch, boolean onGround, boolean is18) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.is18 = is18;
    }

    public PacketEntityTeleport(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void write() {
        if(is18)
            writeVarInt(entityId);
        else
            writeInt(entityId);

        writeInt(x);
        writeInt(y);
        writeInt(z);

        writeByte(yaw);
        writeByte(pitch);

        if(is18)
            writeBoolean(onGround);
    }

    @Override
    public void read() {
        if(is18) {
            entityId = readVarInt();
        } else {
            entityId = readInt();
        }

        x = readInt();
        y = readInt();
        z = readInt();
        yaw = readByte();
        pitch = readByte();

        if(is18) {
            onGround = readBoolean();
        }
    }
}
