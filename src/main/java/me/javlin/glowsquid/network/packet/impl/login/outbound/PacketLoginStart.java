package me.javlin.glowsquid.network.packet.impl.login.outbound;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@AllArgsConstructor
@Getter
public class PacketLoginStart extends Packet {
    public static final int ID = 0x00;

    private String name;

    public PacketLoginStart(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        name = readString();
    }

    @Override
    public void write() {
        writeString(name);
    }
}
