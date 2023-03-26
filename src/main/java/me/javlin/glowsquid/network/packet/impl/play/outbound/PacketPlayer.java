package me.javlin.glowsquid.network.packet.impl.play.outbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketPlayer extends Packet {
    public static final int ID = 0x03;

    private boolean onGround;

    public PacketPlayer(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        onGround = readBoolean();
    }

    @Override
    public void write() {
        writeBoolean(onGround);
    }
}
