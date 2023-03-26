package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketPlayerAbilities extends Packet {
    public static final int ID = 0x39;

    private byte flags;

    private float flyingSpeed;
    private float fovModifier;

    public PacketPlayerAbilities(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        flags = readByte();
        flyingSpeed = readFloat();
        fovModifier = readFloat();
    }

    @Override
    public void write() {
        writeByte(flags);
        writeFloat(flyingSpeed);
        writeFloat(fovModifier);
    }
}
