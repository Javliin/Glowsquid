package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketTimeUpdate extends Packet {
    public static final int ID = 0x03;

    private long worldAge;
    private long timeOfDay;

    public PacketTimeUpdate(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
       worldAge = readLong();
       timeOfDay = readLong();
    }

    @Override
    public void write() {
       writeLong(worldAge);
       writeLong(timeOfDay);
    }
}
