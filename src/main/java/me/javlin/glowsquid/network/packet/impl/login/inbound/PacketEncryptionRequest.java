package me.javlin.glowsquid.network.packet.impl.login.inbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketEncryptionRequest extends Packet {
    public static final int ID = 0x01;

    private String serverId;

    private byte[] publicKey;
    private byte[] verifyToken;

    public PacketEncryptionRequest(PacketDecoder packet) {
        super(packet);
    }

    public PacketEncryptionRequest(String serverId, byte[] publicKey, byte[] verifyToken, boolean is18) {
        this.serverId = serverId;
        this.publicKey = publicKey;
        this.verifyToken = verifyToken;
        this.is18 = is18;
    }

    @Override
    public void read() {
        serverId = readString();

        int length = is18 ? readVarInt() : (int) readShort();

        publicKey = readByteArray(new byte[length]);
        length = is18 ? readVarInt() : (int) readShort();
        verifyToken = readByteArray(new byte[length]);
    }

    @Override
    public void write() {
        writeString(serverId);

        writeVarInt(publicKey.length);
        writeByteArray(publicKey);

        writeVarInt(verifyToken.length);
        writeByteArray(verifyToken);
    }
}
