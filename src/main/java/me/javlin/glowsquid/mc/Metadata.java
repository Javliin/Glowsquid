package me.javlin.glowsquid.mc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.javlin.glowsquid.network.util.UtilDataType;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// TODO:
public class Metadata {
    @Getter
    private final Map<Integer, MetadataEntry<?>> metadata = new HashMap<>();
    private final ByteBuffer buffer;

    private int temp;

    public Metadata(ByteBuffer buffer) {
        this.buffer = buffer;
        read();
    }

    private void read() {
        temp = buffer.position();

        while (buffer.hasRemaining()) {
            int bite = buffer.get() & 0xFF;

            if (bite == 0x7F) {
                break;
            }

            int index = bite & 0x1F;
            int type = (bite >> 5);

            Object value = null;

            switch (type) {
                case 0:
                    value = buffer.get();
                    break;
                case 1:
                    value = buffer.getShort();
                    break;
                case 2:
                    value = buffer.getInt();
                    break;
                case 3:
                    value = buffer.getFloat();
                    break;
                case 4:
                    value = UtilDataType.readString(buffer);
                    break;
                case 5:
                    value = new Slot(buffer);
                    break;
                case 6:
                    value = new int[]{buffer.getInt(), buffer.getInt(), buffer.getInt()};
                    break;
                case 7:
                    value = new float[]{buffer.getFloat(), buffer.getFloat(), buffer.getFloat()};
            }

            metadata.put(index, new MetadataEntry<>(type, value));
        }
    }

    public byte[] write() {
        return Arrays.copyOfRange(buffer.array(), temp, buffer.capacity());
    }

    @RequiredArgsConstructor
    @Getter
    private static class MetadataEntry<T> {
        private final int type;
        private final T value;
    }
}
