package me.javlin.glowsquid.network.packet.impl.play.outbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

import java.util.UUID;

@Getter
public class PacketSpectate extends Packet {
    public static final int ID = 0x18;

    private UUID targetPlayer;

    public PacketSpectate(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        targetPlayer = readUUID();
    }

    @Override
    public void write() {
        writeUUID(targetPlayer);
    }
}
