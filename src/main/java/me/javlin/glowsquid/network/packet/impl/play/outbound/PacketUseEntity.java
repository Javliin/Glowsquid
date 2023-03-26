package me.javlin.glowsquid.network.packet.impl.play.outbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketUseEntity extends Packet {
    public static final int ID = 0x02;

    private int entityId;
    private Action action;

    private float x;
    private float y;
    private float z;

    public PacketUseEntity(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        if( !is18) {
            entityId = readInt();
            action = Action.getById(readByte());

            return;
        }

        entityId = readVarInt();
        action = Action.getById(readVarInt());

        if (action == Action.INTERACT_AT) {
            x = readFloat();
            y = readFloat();
            z = readFloat();
        }
    }

    @Override
    public void write() {
        if (!is18) {
            writeInt(entityId);
            writeByte((byte) action.ordinal());
            return;
        }

        writeVarInt(entityId);
        writeVarInt(action.ordinal());

        if (action == Action.INTERACT_AT) {
            writeFloat(x);
            writeFloat(y);
            writeFloat(z);
        }
    }

    public enum Action {
        INTERACT, ATTACK, INTERACT_AT;

        public static Action getById(int id) {
            if(id > values().length - 1 || id < 0) {
                return null;
            }

            return values()[id];
        }
    }
}
