package me.javlin.glowsquid.network.proxy.impl;

import me.javlin.glowsquid.Console;
import me.javlin.glowsquid.Glowsquid;
import me.javlin.glowsquid.network.proxy.ProxySessionProperties;
import me.javlin.glowsquid.network.proxy.MinecraftProxy;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketBuilder;
import me.javlin.glowsquid.network.packet.PacketInfo;
import me.javlin.glowsquid.network.packet.impl.handshaking.PacketHandshake;
import me.javlin.glowsquid.network.packet.impl.status.outbound.PacketStatusRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class HandshakeProxy extends MinecraftProxy {
    private final ProxySessionProperties auth;

    private PacketHandshake proxiedHandshake;

    public HandshakeProxy(InputStream input, OutputStream output, ProxySessionProperties auth) {
        super(input, output, new PacketBuilder(
                PacketInfo.PacketState.HANDSHAKING,
                PacketInfo.PacketDirection.OUTBOUND,
                -1,
                auth.is18()));

        this.auth = auth;
    }

    @Override
    protected Packet process(Packet packet) {
        if(packet == null) {
            Console.error("INVALID_PACKET", getClass().getSimpleName(), builder.getState().name());
            stop(false);
            return null;
        }

        switch (builder.getState()) {
            case HANDSHAKING: {
                if (!(packet instanceof PacketHandshake)) {
                    Console.error("INVALID_PACKET", getClass().getSimpleName(), builder.getState().name());
                    stop(false);
                    break;
                }

                PacketHandshake handshake = (PacketHandshake) packet;

                if (handshake.getState() == 1) {
                    builder.state(PacketInfo.PacketState.STATUS);
                    proxiedHandshake = handshake;
                } else {
                    auth.set18(handshake.getProtocol() >= 47);
                    auth.setHandshake(handshake);

                    stop(true);
                    return null;
                }

                break;
            }

            case STATUS: {
                if (packet instanceof PacketStatusRequest) {
                    Packet response = getServerListPing(proxiedHandshake, (PacketStatusRequest) packet);

                    if (response == null) {
                        stop(false);
                        return null;
                    }

                    stop(true);
                    return response;
                } else {
                    Console.error("INVALID_PACKET", getClass().getSimpleName(), builder.getState().name());
                    stop(false);
                }
            }
        }

        return null;
    }

    /**
     * Proxies an attempted server list ping to the correct server, and returns the JSON response packet if successful
     * @param handshake Parent handshake packet
     * @param packet Parent server list ping request packet
     * @return Server list ping response packet if successful, otherwise an empty byte array
     */
    private Packet getServerListPing(PacketHandshake handshake, PacketStatusRequest packet) {
        String address = handshake.getAddress();
        short port = handshake.getPort();

        try (Socket socket = new Socket()) {
            String ip = InetAddress.getByName(address).getHostAddress(); // Convert to IP address

            socket.setTcpNoDelay(true);
            socket.setSoTimeout(3000);

            // We need to make sure the connection is ignored by the TCP interceptor, so it won't be redirected
            Glowsquid.ignore(ip);
            socket.connect(new InetSocketAddress(ip, port));

            OutputStream output = socket.getOutputStream();

            output.write(builder.write(handshake).getPacket());
            output.write(builder.write(packet).getPacket());

            Packet response = builder
                    .direction(PacketInfo.PacketDirection.INBOUND)
                    .read(readPacket(socket.getInputStream())).getPacket();

            builder.direction(PacketInfo.PacketDirection.OUTBOUND);

            return response;
        } catch (SocketException | UnknownHostException exception) { // Failed to ping server, no response
            if (exception instanceof ConnectException) {
                // No FIN packet was sent, so we need to remove the ignored connection manually
                try {
                    Glowsquid.unignore(InetAddress.getByName(address).getHostAddress());
                } catch (UnknownHostException ignored) {} // Should never happen
            }
        } catch (IOException exception) {
            Console.error("FAIL_STATUS", handshake.getAddress());
            exception.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }
}
