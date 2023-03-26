package me.javlin.glowsquid.network.packet.impl.play;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketKeepAlive extends Packet {
    public static final int ID = 0x00;

    private int keepAliveId;

    public PacketKeepAlive(PacketDecoder packet) {
        super(packet);
    }

    public PacketKeepAlive(int id) {
        this.keepAliveId = id;
    }

    @Override
    public void read() {
        keepAliveId = readVarInt();
    }

    @Override
    public void write() {
       writeVarInt(keepAliveId);
    }
}
