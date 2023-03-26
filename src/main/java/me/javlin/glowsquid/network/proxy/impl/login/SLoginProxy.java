package me.javlin.glowsquid.network.proxy.impl.login;

import me.javlin.glowsquid.Console;
import me.javlin.glowsquid.network.proxy.ProxySessionProperties;
import me.javlin.glowsquid.network.proxy.MinecraftProxy;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketBuilder;
import me.javlin.glowsquid.network.packet.PacketInfo;
import me.javlin.glowsquid.network.packet.impl.login.inbound.PacketEncryptionRequest;
import me.javlin.glowsquid.network.packet.impl.login.inbound.PacketLoginSuccess;
import me.javlin.glowsquid.network.packet.impl.login.inbound.PacketSetCompression;
import me.javlin.glowsquid.network.util.UtilEncryption;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public class SLoginProxy extends MinecraftProxy {
    private final ProxySessionProperties auth;

    public SLoginProxy(InputStream input, OutputStream output, ProxySessionProperties auth) {
        super(input, output, new PacketBuilder(PacketInfo.PacketState.LOGIN, PacketInfo.PacketDirection.INBOUND, -1, auth.is18()));

        this.auth = auth;
    }

    @Override
    protected Packet process(Packet packet) {
        if (packet instanceof PacketEncryptionRequest) {
            Console.info("LOGIN_REQ_RECEIVED");

            // Intercept and modify encryption request
            PacketEncryptionRequest encryptionRequest = (PacketEncryptionRequest) packet;

            String serverId = encryptionRequest.getServerId();
            byte[] publicKey = encryptionRequest.getPublicKey();
            byte[] verifyToken = encryptionRequest.getVerifyToken();

            auth.setServerId(serverId);
            auth.setPublicKey(publicKey);
            auth.setVerifyToken(verifyToken);

            try {
                PacketEncryptionRequest req = new PacketEncryptionRequest(
                        serverId,
                        UtilEncryption.getKeyPair().getPublic().getEncoded(),
                        verifyToken,
                        auth.is18()
                );

                output.write(builder.write(req).getPacket());
                output.flush();
            } catch (IOException exception) {
                Console.error("LOGIN_FAIL_REQ");
                exception.printStackTrace();
                stop(false);
                return null;
            }

            Console.info("LOGIN_REQ_SENT");

            try {
                // Wait for client interceptor to receive and pass shared secret
                SecretKey key = auth.getKeyBlockingQueue().poll(3000, TimeUnit.MILLISECONDS);

                if (key == null) {
                    Console.error("LOGIN_KEY_TIMEOUT");
                    stop(false);
                    return null;
                }

                enableEncryption(key);
            } catch (InterruptedException exception) {
                Console.error("LOGIN_KEY_INTERRUPT");
                exception.printStackTrace();
                stop(false);
            }
        } else if (packet instanceof PacketSetCompression) {
            int threshold = ((PacketSetCompression) packet).getThreshold();

            Console.info("LOGIN_COMPRESSION", threshold);

            queue(packet); // Need to encode this packet before compression is enabled

            auth.setCompressionThreshold(threshold);
            builder.compression(threshold);
        } else if (packet instanceof PacketLoginSuccess) {
            Console.info("LOGIN_SUCCESS");
            stop(true);
            return packet;
        } else {
            Console.error("INVALID_PACKET", getClass().getSimpleName(), builder.getState().name());
            stop(false);
        }

        return null;
    }
}