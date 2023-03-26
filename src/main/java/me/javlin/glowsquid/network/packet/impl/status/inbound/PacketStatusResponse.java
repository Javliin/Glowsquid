package me.javlin.glowsquid.network.packet.impl.status.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketStatusResponse extends Packet {
    public static final int ID = 0x00;

    private String json;

    public PacketStatusResponse(String json) {
        this.json = json;
    }

    public PacketStatusResponse(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        json = readString();
    }

    @Override
    public void write() {
        writeString(json);
    }
}
