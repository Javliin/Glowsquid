package me.javlin.glowsquid.network.proxy;

import me.javlin.glowsquid.Console;
import me.javlin.glowsquid.gui.GUIGlowsquid;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketBuilder;
import me.javlin.glowsquid.network.packet.builder.PacketEncoder;
import me.javlin.glowsquid.network.util.UtilDataType;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MinecraftProxy {
    private static final GUIGlowsquid GUI = GUIGlowsquid.getInstance();

    private final ConcurrentLinkedQueue<byte[]> packetQueue = new ConcurrentLinkedQueue<>();

    protected final PacketBuilder builder;

    protected AtomicBoolean condition;
    protected AtomicBoolean success;

    protected InputStream input;
    protected OutputStream output;

    public MinecraftProxy(InputStream input, OutputStream output, PacketBuilder builder) {
        this.input = input;
        this.output = output;
        this.builder = builder;
    }

    public boolean start() {
        Console.info("START_PROXY", getClass().getSimpleName());

        condition = new AtomicBoolean(true);
        success = new AtomicBoolean(false);

        try {
            while (condition.get()) {
                byte[] intercept = readPacket(input);

                if (intercept == null) {
                    Console.error("EOS_PROXY", getClass().getSimpleName());
                    stop(false);
                    break;
                }

                Packet packet = builder.read(intercept).getPacket();
                Packet processed = process(packet);
                PacketEncoder encoder;

                if (packet != null) {
                    encoder = builder.write(processed);
                    intercept = encoder.getPacket();

                    if (processed != null) {
                        GUI.displayPacket(encoder);
                    } else {
                        GUI.displayPacket(builder.write(packet));
                    }
                }

                while (packetQueue.size() > 0) {
                    output.write(packetQueue.poll());
                    output.flush();
                }

                output.write(intercept);
                output.flush();
            }
        } catch (SocketException ignored) { // Unavoidable on Windows following Socket#shutdownInput()
            stop(true);
        } catch (Throwable exception) {
            Console.error("ERROR_PROXY", getClass().getSimpleName());
            exception.printStackTrace();
            stop(false);
        }

        return success.get();
    }

    public void queue(Packet packet) {
        packetQueue.add(builder.write(packet).getPacket());
    }

    public void queue(byte[] packet) {
        packetQueue.add(packet);
    }

    public void stop(boolean success) {
        Console.info("STOP_PROXY", getClass().getSimpleName());

        this.success.set(success);
        this.condition.set(false);
    }

    protected void enableEncryption(SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            Cipher cipher_ = Cipher.getInstance("AES/CFB8/NoPadding");

            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(key.getEncoded()));
            cipher_.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(key.getEncoded()));

            this.input = new CipherInputStream(input, cipher);
            this.output = new CipherOutputStream(output, cipher_);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException exception) {
            exception.printStackTrace();
        }
    }

    protected abstract Packet process(Packet data);

    protected static byte[] readPacket(InputStream inputStream) throws IOException {
        ByteArrayOutputStream finalData = new ByteArrayOutputStream();

        int length = UtilDataType.readVarInt(inputStream);

        int totalRead = 0;
        int read;

        if (length == -1) {
            return null;
        }

        byte[] dataBuffer = new byte[length];

        finalData.write(UtilDataType.writeVarInt(length));

        while (totalRead < dataBuffer.length && (read = inputStream.read(dataBuffer, totalRead, dataBuffer.length - totalRead)) != -1) {
            finalData.write(dataBuffer, totalRead, read);

            totalRead += read;
        }

        return finalData.toByteArray();
    }
}
