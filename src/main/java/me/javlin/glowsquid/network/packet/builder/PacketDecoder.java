package me.javlin.glowsquid.network.packet.builder;

import lombok.Getter;
import me.javlin.glowsquid.Console;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.util.UtilCompression;
import me.javlin.glowsquid.network.util.UtilDataType;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

@Getter
public class PacketDecoder {
    private final PacketBuilder builder;
    private final Packet packet;
    private final byte[] data;

    private ByteBuffer buffer;

    PacketDecoder(PacketBuilder builder, byte[] data) {
        this.builder = builder;
        this.data = data;
        this.packet = decode();
    }

    private Packet decode() {
        if (data == null || data.length == 0) {
            return null;
        }

        buffer = ByteBuffer.wrap(data);

        int providedLength = UtilDataType.readVarInt(buffer);
        int length = buffer.remaining();
        int decompressedLength = 0;

        if (builder.getCompressionThreshold() > -1) {
            decompressedLength = UtilDataType.readVarInt(buffer);

            if (decompressedLength != 0) {
                ByteArrayOutputStream newBuffer = new ByteArrayOutputStream();
                byte[] data = UtilCompression.decompress(buffer);
                int position = buffer.position();

                if (data == null) {
                    Console.error("PACKET_FAIL_DECOMPRESS");
                    return null;
                }

                newBuffer.write(buffer.array(), 0, position);
                newBuffer.write(data, 0, data.length);

                buffer = ByteBuffer.wrap(newBuffer.toByteArray());
                buffer.position(position);
                length = buffer.remaining();
            }
        }

        if (!verifyPacket(length, providedLength, decompressedLength)) {
            Console.error("PACKET_FAIL_VERIFY");
            return null;
        }

        Constructor<? extends Packet> constructor = builder.getConstructorCache().get(UtilDataType.readVarInt(buffer));

        if (constructor != null) {
            try {
                return constructor.newInstance(this);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException exception) {
                Console.error("FAIL_CREATE_INSTANCE", constructor.getDeclaringClass().getSimpleName());
                exception.printStackTrace();
                return null;
            }
        }

        return null;
    }

    private boolean verifyPacket(int length, int providedLength, int decompressedLength) {
        if (builder.getCompressionThreshold() == -1 || decompressedLength == 0) { // Compression disabled
            if (providedLength != length) {
                Console.error("INVALID_PACKET_LENGTH", providedLength, length);
                return false;
            }
        } else if (decompressedLength != length) { // Compression enabled, packet is compressed
            Console.error("INVALID_PACKET_LENGTH", decompressedLength, length);
            return false;
        }

        return true;
    }
}
