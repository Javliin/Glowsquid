package me.javlin.glowsquid.network.proxy.impl.login;

import lombok.Getter;
import me.javlin.glowsquid.Console;
import me.javlin.glowsquid.Glowsquid;
import me.javlin.glowsquid.network.proxy.ProxySessionProperties;
import me.javlin.glowsquid.network.proxy.MinecraftProxy;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketBuilder;
import me.javlin.glowsquid.network.packet.PacketInfo;
import me.javlin.glowsquid.network.packet.impl.login.outbound.PacketEncryptionResponse;
import me.javlin.glowsquid.network.packet.impl.login.outbound.PacketLoginStart;
import me.javlin.glowsquid.network.util.UtilDataType;
import me.javlin.glowsquid.network.util.UtilEncryption;
import me.javlin.glowsquid.network.util.UtilMojangAPI;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class CLoginProxy extends MinecraftProxy {
    private final ProxySessionProperties auth;

    @Getter
    private String name;
    @Getter
    private UUID uuid;

    private SecretKey sharedSecret;

    public CLoginProxy(InputStream input, OutputStream output, ProxySessionProperties auth) {
        super(input, output, new PacketBuilder(PacketInfo.PacketState.LOGIN, PacketInfo.PacketDirection.OUTBOUND, -1, auth.is18()));

        this.auth = auth;
    }

    @Override
    protected Packet process(Packet packet) {
        if (packet instanceof PacketLoginStart) { // Login start packet
            name = ((PacketLoginStart) packet).getName();

            // We can't do this unless we have the accessToken of the joining account
            if(Glowsquid.ACCESS_TOKENS.get(name) == null) {
                Console.error("LOGIN_NO_ACCOUNT", name);

                stop(false);
                return null;
            }

            byte[] handshake = builder.state(PacketInfo.PacketState.HANDSHAKING).write(auth.getHandshake()).getPacket();

            queue(handshake);

            builder.state(PacketInfo.PacketState.LOGIN);

            Console.info("HS_FORWARD");
            Console.info("LOGIN_START_FORWARD");

            return packet;
        } else if (packet instanceof PacketEncryptionResponse) { // Encryption response packet
            Console.info("LOGIN_RESPONSE_RECEIVED");

            // This should be set by the ProxyListener by the time this packet is received

            String serverId = auth.getServerId();
            byte[] publicKey = auth.getPublicKey();
            byte[] verifyToken = auth.getVerifyToken();

            if(serverId == null || publicKey == null || verifyToken == null) {
                Console.error("LOGIN_NO_DATA");
                stop(false);
                return null;
            }

            sharedSecret = UtilEncryption.decryptRSA(((PacketEncryptionResponse) packet).getSharedSecret());

            if (sharedSecret == null) {
                Console.error("LOGIN_FAIL_DECRYPT");
                stop(false);
                return null;
            }

            Console.info("LOGIN_SUCCEED_DECRYPT");

            try {
                // Complete auth with custom keypair
                String uuidNoDashes;
                String response = UtilMojangAPI.finishAuth(
                        name,
                        generateHash(UtilEncryption.getKeyPair().getPublic().getEncoded(), serverId)
                );

                if (response != null) {
                    Console.info("LOGIN_SUCCEED_API_FIN");

                    uuidNoDashes = (String) ((JSONObject) JSONValue.parse(response)).get("id");
                    uuid = UtilDataType.toUUID(uuidNoDashes);
                } else {
                    Console.error("LOGIN_FAIL_API_FIN");

                    stop(false);
                    return null;
                }

                // Start REAL auth with REAL keypair
                if (UtilMojangAPI.startAuth(
                        Glowsquid.ACCESS_TOKENS.get(name),
                        uuidNoDashes,
                        generateHash(publicKey, serverId))) {
                    Console.info("LOGIN_SUCCEED_API_START");
                } else {
                    Console.error("LOGIN_FAIL_API_START");
                    stop(false);
                    return null;
                }

                if (!auth.getKeyBlockingQueue().offer(sharedSecret)) {
                    Console.error("LOGIN_QUEUE_FULL");
                    stop(false);
                    return null;
                }

                auth.setKey(sharedSecret);
                stop(true);

                Console.info("LOGIN_RESPONSE_SENT");

                // Send REAL encryption response packet, finishing auth upon server confirmation
                return new PacketEncryptionResponse(
                        UtilEncryption.encryptRSA(sharedSecret.getEncoded(), publicKey),
                        UtilEncryption.encryptRSA(verifyToken, publicKey),
                        auth.is18()
                );
            } catch (IOException exception) {
                Console.error("LOGIN_FAIL_API");
                exception.printStackTrace();
                stop(false);
            } catch (NoSuchAlgorithmException exception) {
                Console.error("LOGIN_FAIL_HASH");
                exception.printStackTrace();
                stop(false);
            }
        } else {
            Console.error("INVALID_PACKET", getClass().getSimpleName(), builder.getState().name());
            stop(false);
        }

        return null;
    }

    private String generateHash(byte[] publicKey, String serverId) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");

        digest.reset();
        digest.update(serverId.getBytes(StandardCharsets.US_ASCII));
        digest.update(sharedSecret.getEncoded());
        digest.update(publicKey);

        return new BigInteger(digest.digest()).toString(16);
    }
}
