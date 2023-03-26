package me.javlin.glowsquid.network.packet.impl.handshaking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@AllArgsConstructor
@Getter
public class PacketHandshake extends Packet {
    public static final int ID = 0x00;

    private String address;

    private int protocol;
    private int state;
    private short port;

    public PacketHandshake(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        protocol = readVarInt();
        address = readString();
        port = readShort();
        state = readVarInt();
    }

    @Override
    public void write() {
        writeVarInt(protocol);
        writeString(address);
        writeShort(port);
        writeVarInt(state);
    }
}
