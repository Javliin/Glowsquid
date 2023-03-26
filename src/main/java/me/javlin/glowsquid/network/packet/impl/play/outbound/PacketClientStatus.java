package me.javlin.glowsquid.network.packet.impl.play.outbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketClientStatus extends Packet {
    public static final int ID = 0x16;

    private int actionId;

    public PacketClientStatus(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        actionId = readVarInt();
    }

    @Override
    public void write() {
        writeVarInt(actionId);
    }
}
