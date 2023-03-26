package me.javlin.glowsquid.network.packet.builder;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.util.UtilCompression;
import me.javlin.glowsquid.network.util.UtilDataType;

import java.io.ByteArrayOutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class PacketEncoder {
    private final List<Map.Entry<byte[], Integer>> headers = new ArrayList<>();
    private final PacketBuilder builder;
    private final Packet data;
    private final byte[] packet;

    private int packetId;

    PacketEncoder(PacketBuilder builder, Packet data) {
        this.builder = builder;
        this.data = data;
        this.packet = encode();
    }

    private byte[] encode() {
        if (data == null) {
            return new byte[0];
        }

        packetId = PacketBuilder.getPacketIds().get(data.getClass());
        int compressionThreshold = builder.getCompressionThreshold();

        byte[] lengthHeader;
        byte[] decompressedLength = new byte[]{0};
        byte[] packetIdData = UtilDataType.writeVarInt(packetId);
        byte[] writeData = data.getWriteData();

        ByteArrayOutputStream packetData = new ByteArrayOutputStream();
        ByteArrayOutputStream content = new ByteArrayOutputStream();

        content.write(packetIdData, 0, packetIdData.length);
        content.write(writeData, 0, writeData.length);

        writeData = content.toByteArray();

        if (compressionThreshold > -1) { // Compression enabled
            int length = writeData.length + 1;
            int decompressed = writeData.length;

            lengthHeader = UtilDataType.writeVarInt(length);

            if (lengthHeader.length + length >= compressionThreshold) { // Compression threshold reached
                decompressedLength = UtilDataType.writeVarInt(decompressed);
                writeData = UtilCompression.compress(content.toByteArray());
                lengthHeader = UtilDataType.writeVarInt(writeData.length + decompressedLength.length);
            }

            packetData.write(lengthHeader, 0, lengthHeader.length);
            packetData.write(decompressedLength, 0, decompressedLength.length);
            headers.add(new AbstractMap.SimpleEntry<>(lengthHeader, length));
            headers.add(new AbstractMap.SimpleEntry<>(decompressedLength, decompressed));
        } else { // Compression disabled
            lengthHeader = UtilDataType.writeVarInt(writeData.length);
            packetData.write(lengthHeader, 0, lengthHeader.length);
            headers.add(new AbstractMap.SimpleEntry<>(lengthHeader, writeData.length));
        }

        packetData.write(writeData, 0, writeData.length);
        headers.add(new AbstractMap.SimpleEntry<>(packetIdData, packetId));

        return packetData.toByteArray();
    }
}
