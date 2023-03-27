package me.javlin.glowsquid.network.proxy;

import lombok.Getter;
import me.javlin.glowsquid.Console;
import me.javlin.glowsquid.network.DelayedPacket;
import me.javlin.glowsquid.network.DelayedType;
import me.javlin.glowsquid.network.packet.PacketInfo;
import me.javlin.glowsquid.network.proxy.module.Module;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class PacketInjector implements Runnable {
    @Getter
    private final ConcurrentHashMap<DelayedType, List<DelayedPacket>> delayedPacketQueue = new ConcurrentHashMap<>();

    private final ProxySession handler;

    private final Map<Module, Runnable> tasks = new ConcurrentHashMap<>();
    private final AtomicBoolean run = new AtomicBoolean(true);

    public PacketInjector(ProxySession handler) {
        this.handler = handler;

        for (DelayedType type : DelayedType.values()) {
            delayedPacketQueue.put(type, new ArrayList<>());
        }
    }

    public void run() {
        // https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6435126
        Thread daemonSleepThread = new Thread(() -> {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException exception) {
                Console.error("INJ_DAEMON_INTERRUPT");
            }
        });

        daemonSleepThread.setDaemon(true);
        daemonSleepThread.start();

        while (run.get()) {
            synchronized (delayedPacketQueue) {
                delayedPacketQueue.forEach((type, packets) -> {
                    Iterator<DelayedPacket> iterator = packets.iterator();
                    DelayedPacket packet;

                    while (iterator.hasNext()) {
                        packet = iterator.next();

                        if (System.currentTimeMillis() >= packet.getSendTime()) {
                            if (type.getDirection() == PacketInfo.PacketDirection.OUTBOUND) {
                                handler.queueOutbound(packet.getPacket());
                            } else {
                                handler.queueInbound(packet.getPacket());
                            }

                            iterator.remove();
                        }
                    }
                });
            }

            tasks.values().forEach(Runnable::run);

            // Works with an error of about ~1ms
            try {
                Thread.sleep(1);
            }catch(InterruptedException exception) {
                Console.error("INJ_THREAD_INTERRUPT");
                exception.printStackTrace();
                break;
            }
        }

        daemonSleepThread.interrupt();
    }

    public void stop() {
        run.set(false);
    }

    public void scheduleTask(Module module, Runnable task) {
        tasks.put(module, task);
    }

    public void removeTasks(Module module) {
        tasks.remove(module);
    }
}
