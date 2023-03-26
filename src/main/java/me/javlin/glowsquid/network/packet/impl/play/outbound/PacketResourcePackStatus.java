package me.javlin.glowsquid.network.packet.impl.play.outbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketResourcePackStatus extends Packet {
    public static final int ID = 0x19;

    private String hash;

    private int result;

    public PacketResourcePackStatus(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        hash = readString();
        result = readVarInt();
    }

    @Override
    public void write() {
        writeString(hash);
        writeVarInt(result);
    }
}
