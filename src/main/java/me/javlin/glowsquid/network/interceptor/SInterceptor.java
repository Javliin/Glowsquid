package me.javlin.glowsquid.network.interceptor;

import com.github.ffalcinelli.jdivert.Packet;
import com.github.ffalcinelli.jdivert.WinDivert;
import com.github.ffalcinelli.jdivert.exceptions.WinDivertException;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SInterceptor implements IInterceptor {
    private final AtomicBoolean run = new AtomicBoolean(true);

    private final WinDivert wd;
    private final Map<Integer, String> bound;

    public SInterceptor(WinDivert wd, Map<Integer, String> bound) {
        this.wd = wd;
        this.bound = bound;
    }

    public void start() throws WinDivertException, UnknownHostException {
        while (run.get()) {
            Packet packet = wd.recv();

            if (packet.getSrcPort() == 25565) {
                // Manipulate the TCP source address of our traffic to spoof it as the real server
                if (bound.containsKey(packet.getDstPort())) {
                    packet.setSrcAddr(bound.get(packet.getDstPort()));
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
}
