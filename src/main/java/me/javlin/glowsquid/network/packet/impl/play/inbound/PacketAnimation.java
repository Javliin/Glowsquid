package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketAnimation extends Packet {
    public static final int ID = 0x0B;

    private int entityId;
    private byte animation;

    public PacketAnimation(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        entityId = readVarInt();
        animation = readByte();
    }

    @Override
    public void write() {
        writeVarInt(entityId);
        writeByte(animation);
    }
}
