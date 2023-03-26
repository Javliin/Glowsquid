package me.javlin.glowsquid.network.packet;

import lombok.Getter;
import me.javlin.glowsquid.mc.Metadata;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;
import me.javlin.glowsquid.network.util.UtilDataType;

import java.io.ByteArrayOutputStream;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Packet {
    @Getter
    private final List<Integer> fieldSizes = new ArrayList<>();

    private final ByteArrayOutputStream writeBuffer = new ByteArrayOutputStream();
    private ByteBuffer readBuffer;

    protected boolean is18;

    public Packet() {}
    public Packet(PacketDecoder reader) {
        readBuffer = reader.getBuffer();
        is18 = reader.getBuilder().is18();
        read();
    }

    public byte[] getReadData() {
        return readBuffer.array();
    }

    public byte[] getWriteData() {
        writeBuffer.reset();
        write();
        return writeBuffer.toByteArray();
    }

    protected void writeUUID(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16);

        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());

        writeBuffer.write(buffer.array(), 0, buffer.array().length);
    }

    protected void writeString(String string) {
        byte[] length = UtilDataType.writeVarInt(string.getBytes().length);
        byte[] text = string.getBytes();

        ByteBuffer data = ByteBuffer.allocate(length.length + text.length);

        data.put(length);
        data.put(text);

        writeBuffer.write(data.array(), 0, data.array().length);
    }

    protected void writeMetadata(Metadata data) {
        byte[] result = data.write();
        writeBuffer.write(result, 0, result.length);
    }

    protected void writeVarInt(int varInt) {
        byte[] result = UtilDataType.writeVarInt(varInt);
        writeBuffer.write(result, 0 , result.length);
    }

    protected void writeBoolean(boolean data) {
        writeBuffer.write(data ? 1 : 0);
    }

    protected void writeInt(int data) {
        byte[] result = ByteBuffer.allocate(4).putInt(data).array();
        writeBuffer.write(result, 0, result.length);
    }

    protected void writeShort(short data) {
        byte[] result = ByteBuffer.allocate(2).putShort(data).array();
        writeBuffer.write(result, 0, result.length);
    }

    protected void writeDouble(double data) {
        byte[] result = ByteBuffer.allocate(8).putDouble(data).array();
        writeBuffer.write(result, 0, result.length);
    }

    protected void writeLong(long data) {
        byte[] result = ByteBuffer.allocate(8).putLong(data).array();
        writeBuffer.write(result, 0, result.length);
    }

    protected void writeFloat(float data) {
        byte[] result = ByteBuffer.allocate(4).putFloat(data).array();
        writeBuffer.write(result, 0, result.length);
    }

    protected void writeByte(byte data) {
        writeBuffer.write(data);
    }

    protected void writeByteArray(byte[] data) {
        writeBuffer.write(data, 0, data.length);
    }

    protected UUID readUUID() {
        return new UUID(readBuffer.getLong(), readBuffer.getLong());
    }

    protected String readString() {
        int position = readBuffer.position();
        String result = UtilDataType.readString(readBuffer);

        fieldSizes.add(readBuffer.position() - position);
        fieldSizes.add(result.getBytes().length);

        return result;
    }

    protected Metadata readMetadata() {
        int position = readBuffer.position();
        Metadata result = new Metadata(readBuffer);

        fieldSizes.add(readBuffer.position() - position);

        return result;
    }

    protected int readVarInt() {
        int pos = readBuffer.position();
        int result = UtilDataType.readVarInt(readBuffer);

        fieldSizes.add(readBuffer.position() - pos);

        return result;
    }

    protected boolean readBoolean() {
        return readByte() == 1;
    }

    protected int readInt() {
        fieldSizes.add(4);
        return readBuffer.getInt();
    }

    protected short readShort() {
        fieldSizes.add(2);
        return readBuffer.getShort();
    }

    protected double readDouble() {
        fieldSizes.add(8);
        return readBuffer.getDouble();
    }

    protected long readLong() {
        fieldSizes.add(8);
        return readBuffer.getLong();
    }

    protected float readFloat() {
        fieldSizes.add(4);
        return readBuffer.getFloat();
    }

    protected byte readByte() {
        fieldSizes.add(1);
        return readBuffer.get();
    }

    protected byte[] readByteArray(byte[] data) {
        fieldSizes.add(data.length);
        readBuffer.get(data, 0, data.length);
        return data;
    }

    protected byte[] readRemaining() {
        byte[] data = new byte[readBuffer.remaining()];
        fieldSizes.add(data.length);
        readBuffer.get(data, 0, data.length);
        return data;
    }

    protected abstract void read();
    protected abstract void write();
}
