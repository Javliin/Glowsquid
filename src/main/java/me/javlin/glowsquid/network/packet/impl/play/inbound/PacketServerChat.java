package me.javlin.glowsquid.network.packet.impl.play.inbound;

import lombok.Getter;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketServerChat extends Packet {
    public static final int ID = 0x02;

    private String message;
    private Position position;

    public PacketServerChat(PacketDecoder packet) {
        super(packet);
    }

    public PacketServerChat(String message, Position position, boolean is18) {
        this.message = message;
        this.position = position;
        this.is18 = is18;
    }

    public PacketServerChat(String message, boolean is18) {
        this.message = message;
        this.position = Position.CHAT_BOX;
        this.is18 = is18;
    }

    @Override
    public void read() {
        message = readString();

        if(is18)
            position = Position.getById(readByte());
    }

    @Override
    public void write() {
       writeString(message);

        if (is18) {
            writeByte((byte) position.ordinal());
        }
    }

    public enum Position {
        CHAT_BOX,
        SYSTEM_MESSAGE,
        ABOVE_HOTBAR;

        public static Position getById(int id) {
            if(id > values().length - 1 || id < 0)
                return null;

            return values()[id];
        }
    }
}
