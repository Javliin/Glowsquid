package me.javlin.glowsquid.network.proxy;

import me.javlin.glowsquid.Console;
import me.javlin.glowsquid.network.proxy.module.Module;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PacketInjector {
    private final Queue<byte[]> outboundPacketQueue = new ConcurrentLinkedQueue<>();
    private final Queue<byte[]> inboundPacketQueue = new ConcurrentLinkedQueue<>();

    private final Map<Module, Runnable> tasks = new ConcurrentHashMap<>();
    private final AtomicBoolean run = new AtomicBoolean(true);

    public Runnable run(ReentrantReadWriteLock lock, OutputStream output, OutputStream inbound) {
        return () -> {
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
                tasks.values().forEach(Runnable::run);

                byte[] packet;

                while ((packet = outboundPacketQueue.poll()) != null) {
                    try {
                        lock.readLock().lock();
                        output.write(packet);
                    } catch (IOException ignored) {
                        // Proxy will handle this
                    } finally {
                        lock.readLock().unlock();
                    }
                }

                while ((packet = inboundPacketQueue.poll()) != null) {
                    try {
                        System.out.println("WRITIING INBOUND");
                        lock.writeLock().lock();
                        inbound.write(packet);
                    } catch (IOException ignored) {
                        // Proxy will handle this
                    } finally {
                        lock.writeLock().unlock();
                    }
                }

                // Works with an error of about ~1ms
                try {
                    Thread.sleep(1);
                } catch (InterruptedException exception) {
                    Console.error("INJ_THREAD_INTERRUPT");
                    exception.printStackTrace();
                    break;
                }
            }

            daemonSleepThread.interrupt();
        };
    }

    public void stop() {
        run.set(false);
    }

    public void queueOutbound(byte[] packet) {
        outboundPacketQueue.add(packet);
    }

    public void queueInbound(byte[] packet) {
        inboundPacketQueue.add(packet);
    }

    public void scheduleTask(Module module, Runnable task) {
        tasks.put(module, task);
    }

    public void removeTasks(Module module) {
        tasks.remove(module);
    }
}
