package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketCollectItem extends Packet {
    public static final int ID = 0x0D;

    private int collectedEntityID;
    private int collectorEntityID;

    public PacketCollectItem(PacketDecoder packet) {
        super(packet);
    }

    public PacketCollectItem(int collectedEntityID, int collectorEntityID) {
        this.collectedEntityID = collectedEntityID;
        this.collectorEntityID = collectorEntityID;
    }

    @Override
    public void read() {
        collectedEntityID = readVarInt();
        collectorEntityID = readVarInt();
    }

    @Override
    public void write() {
        writeVarInt(collectedEntityID);
        writeVarInt(collectorEntityID);
    }
}