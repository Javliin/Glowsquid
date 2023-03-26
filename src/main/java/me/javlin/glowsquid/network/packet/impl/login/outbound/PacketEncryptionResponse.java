package me.javlin.glowsquid.network.packet.impl.login.outbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketEncryptionResponse extends Packet {
    public static final int ID = 0x01;

    private byte[] sharedSecret;
    private byte[] verifyToken;

    public PacketEncryptionResponse(PacketDecoder packet) {
        super(packet);
    }

    public PacketEncryptionResponse(byte[] sharedSecret, byte[] verifyToken, boolean is18) {
        this.sharedSecret = sharedSecret;
        this.verifyToken = verifyToken;
        this.is18 = is18;
    }

    @Override
    public void read() {
        int length = is18 ? readVarInt() : (int) readShort();
        sharedSecret = readByteArray(new byte[length]);
        length = is18 ? readVarInt() : (int) readShort();
        verifyToken = readByteArray(new byte[length]);
    }

    @Override
    public void write() {
        if(is18) {
            writeVarInt(sharedSecret.length);
        } else {
            writeShort((short) sharedSecret.length);
        }

        writeByteArray(sharedSecret);

        if(is18) {
            writeVarInt(verifyToken.length);
        } else {
            writeShort((short) verifyToken.length);
        }

        writeByteArray(verifyToken);
    }
}
