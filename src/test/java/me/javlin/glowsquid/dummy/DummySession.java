package me.javlin.glowsquid.dummy;

import me.javlin.glowsquid.network.proxy.ProxySession;
import me.javlin.glowsquid.network.packet.Packet;

public class DummySession extends ProxySession {
    public long server;
    public long client;

    public DummySession() {
        super(null);
    }

    @Override
    public void queueInbound(Packet packet) {
        server = System.nanoTime();
    }

    @Override
    public void queueOutbound(Packet packet) {
        client = System.nanoTime();
    }
}
