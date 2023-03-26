package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketCamera extends Packet {
    public static final int ID = 0x43;

    private int cameraID;

    public PacketCamera(PacketDecoder packet) {
        super(packet);
    }

    public PacketCamera(int cameraID) {
        this.cameraID = cameraID;
    }

    @Override
    public void read() {
        cameraID = readVarInt();
    }

    @Override
    public void write() {
        writeVarInt(cameraID);
    }
}