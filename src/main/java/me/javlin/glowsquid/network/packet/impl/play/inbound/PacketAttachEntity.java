package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketAttachEntity extends Packet {
    public static final int ID = 0x1B;

    private int entityID;
    private int vehicleID;

    private boolean leash;

    public PacketAttachEntity(PacketDecoder packet) {
        super(packet);
    }

    public PacketAttachEntity(int entityID, int vehicleID, boolean leash) {
        this.entityID = entityID;
        this.vehicleID = vehicleID;
        this.leash = leash;
    }

    @Override
    public void read() {
        entityID = readInt();
        vehicleID = readInt();
        leash = readBoolean();
    }

    @Override
    public void write() {
        writeInt(entityID);
        writeInt(vehicleID);
        writeBoolean(leash);
    }
}