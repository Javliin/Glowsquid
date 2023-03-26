package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PacketDestroyEntities extends Packet {
    public static final int ID = 0x13;

    private List<Integer> destroyedEntities;

    public PacketDestroyEntities(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        int count = is18 ? readVarInt() : readByte();

        List<Integer> entities = new ArrayList<>(count);

        for(int index = 0; index < count; index++) {
            entities.add(is18 ? readVarInt() : readInt());
        }

        this.destroyedEntities = entities;
    }

    @Override
    public void write() {
        if (is18) {
            writeVarInt(destroyedEntities.size());
        } else {
            writeByte((byte) destroyedEntities.size());
        }

        for (int id : destroyedEntities) {
            if (is18) {
                writeVarInt(id);
            } else {
                writeInt(id);
            }
        }
    }
}
