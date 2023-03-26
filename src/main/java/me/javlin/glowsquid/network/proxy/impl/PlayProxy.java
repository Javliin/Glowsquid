package me.javlin.glowsquid.network.proxy.impl;

import me.javlin.glowsquid.network.proxy.ProxySessionProperties;
import me.javlin.glowsquid.network.proxy.MinecraftProxy;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketBuilder;
import me.javlin.glowsquid.network.packet.PacketInfo;
import me.javlin.glowsquid.network.packet.event.PacketEventHandler;
import me.javlin.glowsquid.network.packet.event.PacketSendEvent;
import me.javlin.glowsquid.network.packet.impl.play.inbound.PacketDisconnect;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class PlayProxy extends MinecraftProxy {
    private final PacketInfo.PacketDirection direction;

    public PlayProxy(InputStream input, OutputStream output, ProxySessionProperties auth) {
        super(input, output, new PacketBuilder(PacketInfo.PacketState.PLAY, auth.getDirection(), auth.getCompressionThreshold(), auth.is18()));

        if (!(input instanceof CipherInputStream) && !(output instanceof CipherOutputStream)) {
            enableEncryption(auth.getKey());
        }

        this.direction = auth.getDirection();
    }

    @Override
    protected Packet process(Packet packet) {
        PacketSendEvent<? extends Packet> event = PacketEventHandler.call(packet, PacketInfo.PacketState.PLAY, direction);

        if (packet instanceof PacketDisconnect) {
            stop(true);
        }

        if (event.isCancelled()) {
            return null;
        }

        return event.getPacket();
    }
}
