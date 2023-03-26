package me.javlin.glowsquid;

import me.javlin.glowsquid.dummy.DummySession;
import me.javlin.glowsquid.network.DelayedPacket;
import me.javlin.glowsquid.network.DelayedType;
import me.javlin.glowsquid.network.proxy.PacketInjector;
import me.javlin.glowsquid.network.packet.impl.play.inbound.PacketServerChat;
import me.javlin.glowsquid.network.packet.impl.play.outbound.PacketClientChat;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PacketInjectorTest {
    private static final Logger logger =  LoggerFactory.getLogger(PacketInjectorTest.class);

    @Test
    public void measureDelay() throws InterruptedException {
        ExecutorService injectorThread = Executors.newSingleThreadExecutor();
        DummySession session = new DummySession();
        PacketInjector injector = new PacketInjector(session);

        injectorThread.submit(injector);
        Thread.sleep(50); // Wait for injector to start

        long serverTarget = System.nanoTime() + 512000;
        long clientTarget = System.nanoTime() + 412000;

        injector.getDelayedPacketQueue().get(DelayedType.ENEMY_DELAY).add(
                new DelayedPacket(
                        serverTarget / 1000000,
                        new PacketServerChat("Hello world!", PacketServerChat.Position.CHAT_BOX, true)
                )
        );

        injector.getDelayedPacketQueue().get(DelayedType.PLAYER_RANGE).add(
                new DelayedPacket(
                        clientTarget / 1000000,
                        new PacketClientChat("Hello world!")
                )
        );

        Thread.sleep(1000); // Wait for injector to process packets

        injectorThread.shutdownNow();

        long serverInaccuracy = Math.round(Math.abs(serverTarget - session.server) / 1000000D);
        long clientInaccuracy = Math.round(Math.abs(clientTarget - session.client) / 1000000D);

        Assertions.assertTrue(session.server != 0);
        Assertions.assertTrue(session.client != 0);

        logger.info(String.format("Inbound delayed packet inaccuracy: ~%dms", serverInaccuracy));
        logger.info(String.format("Outbound delayed packet inaccuracy: ~%dms", clientInaccuracy));

        Assertions.assertTrue(serverInaccuracy < 50);
        Assertions.assertTrue(clientInaccuracy < 50);
    }
}
