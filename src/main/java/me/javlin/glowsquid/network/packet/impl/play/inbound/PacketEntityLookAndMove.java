package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketEntityLookAndMove extends PacketEntityMove {
    public static final int ID = 0x17;

    private byte yaw;
    private byte pitch;

    public PacketEntityLookAndMove(int entityId, byte dx, byte dy, byte dz, byte yaw, byte pitch, boolean onGround, boolean is18) {
        super(entityId, dx, dy, dz, onGround, is18);

        this.yaw = yaw;
        this.pitch = pitch;
    }

    public PacketEntityLookAndMove(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void write() {
        if(is18)
            writeVarInt(entityId);
        else
            writeInt(entityId);
        
        writeByte(dx);
        writeByte(dy);
        writeByte(dz);
        writeByte(yaw);
        writeByte(pitch);

        if(is18)
            writeBoolean(onGround);
    }

    @Override
    public void read() {
        if (is18) {
            entityId = readVarInt();
        } else {
            entityId = readInt();
        }

        dx = readByte();
        dy = readByte();
        dz = readByte();
        yaw = readByte();
        pitch = readByte();

        if (is18) {
            onGround = readBoolean();
        }
    }
}
