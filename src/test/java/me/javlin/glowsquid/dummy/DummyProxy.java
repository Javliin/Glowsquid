package me.javlin.glowsquid.dummy;

import me.javlin.glowsquid.network.proxy.MinecraftProxy;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketBuilder;
import org.junit.jupiter.api.Assertions;

import java.io.InputStream;
import java.io.OutputStream;

public class DummyProxy extends MinecraftProxy {
    private final OutputStream raw;

    public DummyProxy(InputStream input, OutputStream output, OutputStream raw, PacketBuilder builder) {
        super(input, output, builder);
        this.raw = raw;
    }

    @Override
    public boolean start() {
        try {
            while (input.available() > 0) {
                byte[] intercept = readPacket(input);

                Assertions.assertNotNull(intercept);

                raw.write(intercept);
                output.write(builder.write(builder.read(intercept).getPacket()).getPacket());
            }

            return true;
        } catch (Throwable exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    protected Packet process(Packet data) {
        return data;
    }
}
