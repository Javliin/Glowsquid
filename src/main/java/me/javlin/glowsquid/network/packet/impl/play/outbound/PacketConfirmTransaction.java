package me.javlin.glowsquid.network.packet.impl.play.outbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketConfirmTransaction extends Packet {
    public static final int ID = 0x0F;

    private byte windowID;
    private short actionNumber;
    private boolean accepted;

    public PacketConfirmTransaction(PacketDecoder packet) {
        super(packet);
    }

    public PacketConfirmTransaction(byte windowID, short actionNumber, boolean accepted) {
        this.windowID = windowID;
        this.actionNumber = actionNumber;
        this.accepted = accepted;
    }

    @Override
    public void read() {
        windowID = readByte();
        actionNumber = readShort();
        accepted = readBoolean();
    }

    @Override
    public void write() {
        writeByte(windowID);
        writeShort(actionNumber);
        writeBoolean(accepted);
    }
}