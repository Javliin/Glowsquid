package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.AllArgsConstructor;
import lombok.Getter;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@AllArgsConstructor
@Getter
public class PacketSoundEffect extends Packet {
    public static final int ID = 0x29;

    private String soundname;

    private int effectpositionX;
    private int effectpositionY;
    private int effectpositionZ;

    private float volume;

    private byte pitch;

    public PacketSoundEffect(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        soundname = readString();
        effectpositionX = readInt();
        effectpositionY = readInt();
        effectpositionZ = readInt();
        volume = readFloat();
        pitch = readByte();
    }

    @Override
    public void write() {
        writeString(soundname);
        writeInt(effectpositionX);
        writeInt(effectpositionY);
        writeInt(effectpositionZ);
        writeFloat(volume);
        writeByte(pitch);
    }
}