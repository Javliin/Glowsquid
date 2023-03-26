package me.javlin.glowsquid.network.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

public class UtilDataType {
    public static UUID toUUID(String uuid) {
        return UUID.fromString(uuid.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                "$1-$2-$3-$4-$5"
        ));
    }

    public static String readString(ByteBuffer buffer) {
        int length = readVarInt(buffer);
        byte[] data = new byte[length];
        buffer.get(data);

        return new String(data);
    }

    public static int readVarInt(ByteBuffer varInt) {
        if (!varInt.hasRemaining()) {
            return -1;
        }

        int result = 0;
        int count = 0;
        int bite;

        do {
            bite = varInt.get();
            result |= ((bite & 127) << (7 * count));
            count++;
        } while (varInt.hasRemaining()
                && count < 5
                && (bite & 128) != 0);

        return result;
    }

    public static int readVarInt(InputStream inputStream) throws IOException {
        int input;
        int count = -1;
        int result = 0;

        do {
            input = inputStream.read();

            count++;

            if (input == -1 || count >= 5) {
                return -1;
            }

            result |= ((input & 0b01111111) << (7 * count));
        } while (!isByteLast(input));

        return result;
    }

    public static byte[] writeVarInt(int value) {
        byte varIntByte;

        ByteArrayOutputStream varint = new ByteArrayOutputStream();

        do {
            varIntByte = (byte) (value & 0b01111111);
            value >>>= 7;

            if (value != 0) {
                varIntByte |= 0b10000000;
            }

            varint.write(varIntByte);
        } while (value != 0);

        return varint.toByteArray();
    }

    public static boolean isByteLast(int varint) {
        return (varint & 0b10000000) == 0;
    }

    public static boolean isNumeric(String str) {
        return str.matches("\\d+(\\.\\d+)?");
    }
}
