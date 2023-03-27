package me.javlin.glowsquid.network.packet.impl.play.outbound;

import lombok.Getter;
import lombok.Setter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
@Setter
public class PacketPluginMessage extends Packet {
    public static final int ID = 0x17;

    private String channel;
    private byte[] message;

    public PacketPluginMessage(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        channel = readString();
        message = readRemaining();
    }

    @Override
    public void write() {
        writeString(channel);
        writeByteArray(message);
    }
}
