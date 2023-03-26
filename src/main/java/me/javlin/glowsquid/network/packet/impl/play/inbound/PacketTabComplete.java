package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketTabComplete extends Packet {
    public static final int ID = 0x3A;

    private int count;

    private String[] matches;

    public PacketTabComplete(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        count = readVarInt();

        String[] matches = new String[count];

        for (int index = 0; index < count; index++) {
            matches[index] = readString();
        }

        this.matches = matches;
    }

    @Override
    public void write() {
        writeVarInt(count);

        for (String match : matches) {
            writeString(match);
        }
    }
}
