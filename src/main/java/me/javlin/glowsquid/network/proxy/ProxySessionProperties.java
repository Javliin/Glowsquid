package me.javlin.glowsquid.network.proxy;

import lombok.*;
import me.javlin.glowsquid.network.packet.PacketInfo;
import me.javlin.glowsquid.network.packet.impl.handshaking.PacketHandshake;

import javax.crypto.SecretKey;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@NoArgsConstructor
@Getter
@Setter
public class ProxySessionProperties {
    private final BlockingQueue<SecretKey> keyBlockingQueue = new LinkedBlockingQueue<>();

    private PacketHandshake handshake;
    private PacketInfo.PacketDirection direction;
    private SecretKey key;
    private String serverId;

    private int compressionThreshold;

    private byte[] publicKey;
    private byte[] verifyToken;

    private boolean is18;

    public ProxySessionProperties(ProxySessionProperties auth) {
        this.handshake = auth.handshake;
        this.direction = auth.direction;
        this.key = auth.key;
        this.serverId = auth.serverId;

        this.compressionThreshold = auth.compressionThreshold;

        this.publicKey = auth.publicKey;
        this.verifyToken = auth.verifyToken;

        this.is18 = auth.is18;
    }
}
