package me.javlin.glowsquid.network.packet.impl.play.outbound;

import lombok.Getter;

import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketClientSettings extends Packet {
    public static final int ID = 0x15;

    private String locale;

    private byte viewDistance;
    private byte displayedSkinParts;

    private boolean chatColors;

    private byte chatMode;

    public PacketClientSettings(PacketDecoder packet) {
        super(packet);
    }

    public PacketClientSettings(String locale, byte viewDistance, byte displayedSkinParts, boolean chatColors, byte chatMode) {
        this.locale = locale;
        this.viewDistance = viewDistance;
        this.displayedSkinParts = displayedSkinParts;
        this.chatColors = chatColors;
        this.chatMode = chatMode;
    }

    @Override
    public void read() {
        locale = readString();

        viewDistance = readByte();
        displayedSkinParts = readByte();
        chatColors = readBoolean();
        chatMode = readByte();
    }

    @Override
    public void write() {
        writeString(locale);

        writeByte(viewDistance);
        writeByte(displayedSkinParts);

        writeBoolean(chatColors);

        writeByte(chatMode);
    }
}