package me.javlin.glowsquid.network.interceptor;

import com.github.ffalcinelli.jdivert.Packet;
import com.github.ffalcinelli.jdivert.WinDivert;
import com.github.ffalcinelli.jdivert.exceptions.WinDivertException;
import com.github.ffalcinelli.jdivert.headers.Tcp;

import me.javlin.glowsquid.Console;
import me.javlin.glowsquid.Glowsquid;

import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class CInterceptor implements IInterceptor {
    private static final List<Integer> MC_PORTS = new ArrayList<Integer>(){{
        add(25565);
    }};

    private final AtomicBoolean run = new AtomicBoolean(true);
    private final WinDivert wd;

    private final Map<Integer, String> bound;
    private final Map<String, Integer> ignored;

    private String activeConnection;

    public CInterceptor(WinDivert wd, Map<Integer, String> bound, Map<String, Integer> ignored) {
        this.wd = wd;
        this.bound = bound;
        this.ignored = ignored;
    }

    public void start() throws WinDivertException, UnknownHostException {
        while (run.get()) {
            Packet packet = wd.recv();
            String address = packet.getDstAddr();
            Integer port = ignored.get(address);

            if (port != null && port == -1) {
                ignored.put(address, packet.getSrcPort());
                port = packet.getSrcPort();
            }

            if (packet.getSrcPort().equals(port)) {
                wd.send(packet);

                if (packet.getTcp().is(Tcp.Flag.FIN)) {
                    ignored.remove(address);
                }

                continue;
            }

            // Bind a connection to a local server
            if (packet.getTcp().is(Tcp.Flag.SYN) && MC_PORTS.contains(packet.getDstPort())) {
                bound.put(packet.getSrcPort(), address);
            }

            boolean connectionBound = address.equals(bound.get(packet.getSrcPort()));

            if (packet.getTcp().is(Tcp.Flag.FIN)) {
                bound.remove(packet.getSrcPort());

                if (address.equals(activeConnection)) {
                    activeConnection = null;
                }
            }

            if (connectionBound) { // Reroute outgoing traffic from a bound connection to the local server
                packet.setDstAddr(packet.getSrcAddr());
                packet.setDstPort(Glowsquid.PORT);
            }

            ByteBuffer byteBuffer = ByteBuffer.wrap(packet.getPayload());

            // Listen for handshakes
            if (activeConnection == null && isHandshake(byteBuffer)) {
                if (byteBuffer.get(byteBuffer.capacity() - 1) == 2) {
                    if (connectionBound) {
                        Console.info("INTERCEPT_SUCCESS");
                        activeConnection = address;
                        wd.send(packet);
                        continue;
                    }

                    Console.info("INTERCEPT_FAIL", packet.getDstPort());
                    MC_PORTS.add(packet.getDstPort());

                    // Change the handshake state to STATUS and cause a client disconnect
                    byteBuffer.position(byteBuffer.capacity() - 1);
                    byteBuffer.put((byte) 1);

                    packet.setPayload(byteBuffer.array());
                }
            }

            wd.send(packet);
        }
    }

    public void stop() {
        run.set(false);

        if (wd != null) {
            wd.close();
        }
    }

    private boolean isHandshake(ByteBuffer byteBuffer) {
        return byteBuffer != null
                && byteBuffer.capacity() > 3
                && byteBuffer.get() == byteBuffer.capacity() - byteBuffer.position() // Verify packet length
                && byteBuffer.get() == 0 // Packet ID should be 0 (handshake)
                && byteBuffer.get() <= 47; // Protocol version should be 47 (1.8.X) or older (1.7)
    }
}
