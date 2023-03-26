package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;
import me.javlin.glowsquid.mc.Metadata;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

import java.util.UUID;

@Getter
public class PacketSpawnPlayer extends Packet {
    public static final int ID = 0x0C;

    private int entityId;

    private UUID uuid;

    private int x;
    private int y;
    private int z;

    private byte yaw;
    private byte pitch;

    private short item;

    private Metadata metadata;

    public PacketSpawnPlayer(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        entityId = readVarInt();
        uuid = is18 ? readUUID() : UUID.fromString(readString());

        // Ignore player name and properties fields for 1.7 clients
        if(!is18) {
            readString();

            int propertiesCount = readVarInt();

            for(int index = 0; index < propertiesCount; index++) {
                readString();
                readString();
                readString();
            }
        }

        x = readInt();
        y = readInt();
        z = readInt();

        yaw = readByte();
        pitch = readByte();
        item = readShort();

        metadata = readMetadata();
    }

    @Override
    public void write() {
        writeVarInt(entityId);

        if (is18) {
            writeUUID(uuid);
        } else {
            writeString(uuid.toString());
        }

        writeInt(x);
        writeInt(y);
        writeInt(z);
        writeByte(yaw);
        writeByte(pitch);
        writeShort(item);
        writeMetadata(metadata);
    }
}
