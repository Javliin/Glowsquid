package me.javlin.glowsquid.mc;

import lombok.Getter;
import me.javlin.glowsquid.Console;
import me.nullicorn.nedit.NBTReader;
import me.nullicorn.nedit.type.NBTCompound;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

@Getter
public class Slot {
    private final ByteBuffer buffer;

    private short id;

    private byte count;
    private short damage;

    private NBTCompound nbt;

    public Slot(ByteBuffer buffer) {
        this.buffer = buffer;

        read();
    }

    private void read() {
        id = buffer.getShort();

        if (id == -1) {
            return;
        }

        count = buffer.get();
        damage = buffer.getShort();

        if (buffer.get(buffer.position()) != 0) {
            try {
                InputStream in = new ByteArrayInputStream(Arrays.copyOfRange(buffer.array(), buffer.position(), buffer.capacity()));
                nbt = NBTReader.read(in);
                buffer.position(buffer.position() + (buffer.remaining() - in.available()));
            } catch (IOException exception) {
                Console.error("SLOT_FAIL_READ_NBT");
                exception.printStackTrace();
            }
        }
    }
}
