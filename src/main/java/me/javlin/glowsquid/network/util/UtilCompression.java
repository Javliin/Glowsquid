package me.javlin.glowsquid.network.util;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class UtilCompression {
    private static final Inflater inflater = new Inflater();
    private static final Deflater deflater = new Deflater();

    public static byte[] compress(byte[] data) {
        ByteArrayOutputStream compressedData = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        deflater.setInput(data);
        deflater.finish();

        while (!deflater.finished()) {
            compressedData.write(buffer, 0, deflater.deflate(buffer));
        }

        deflater.reset();

        return compressedData.toByteArray();
    }

    public static byte[] decompress(ByteBuffer buffer) {
        return decompress(buffer.array(), buffer.position(), buffer.remaining());
    }

    public static byte[] decompress(byte[] data, int offset, int length) {
        ByteArrayOutputStream decompressedData = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read;

        try {
            inflater.setInput(data, offset, length);

            while (!inflater.finished()) {
                read = inflater.inflate(buffer);
                decompressedData.write(buffer, 0, read);
            }

            inflater.reset();

            return decompressedData.toByteArray();
        } catch (Throwable exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
