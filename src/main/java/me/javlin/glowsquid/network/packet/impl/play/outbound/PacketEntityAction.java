package me.javlin.glowsquid.network.packet.impl.play.outbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketEntityAction extends Packet {
    public static final int ID = 0x0B;

    private int entityId;
    private int actionId;
    private int actionParameter;

    public PacketEntityAction(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        entityId = readVarInt();
        actionId = readVarInt();
        actionParameter = readVarInt();
    }

    @Override
    public void write() {
        writeVarInt(entityId);
        writeVarInt(actionId);
        writeVarInt(actionParameter);
    }
}
