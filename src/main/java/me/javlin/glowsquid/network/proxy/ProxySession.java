package me.javlin.glowsquid.network.proxy;

import me.javlin.glowsquid.Console;
import me.javlin.glowsquid.Glowsquid;
import me.javlin.glowsquid.GlowsquidThreadFactory;
import me.javlin.glowsquid.network.packet.impl.handshaking.PacketHandshake;
import me.javlin.glowsquid.network.proxy.module.Module;
import me.javlin.glowsquid.network.proxy.module.ModuleManager;
import me.javlin.glowsquid.network.proxy.impl.HandshakeProxy;
import me.javlin.glowsquid.network.proxy.impl.login.CLoginProxy;
import me.javlin.glowsquid.network.proxy.impl.PlayProxy;
import me.javlin.glowsquid.network.proxy.impl.login.SLoginProxy;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.PacketInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ProxySession {
    private final PacketInjector injector = new PacketInjector();

    private final ProxySessionProperties data = new ProxySessionProperties();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final Socket server = new Socket();
    private final Socket client;

    private volatile boolean stopped = false;

    private ExecutorService threadPool;

    private PlayProxy clientMCConnection;
    private PlayProxy serverMCConnection;

    public ProxySession(Socket client) {
        this.client = client;
    }

    public void start() {
        threadPool = Executors.newCachedThreadPool(new GlowsquidThreadFactory(Thread.currentThread().getName() + "-thread"));

        ModuleManager manager = null;
        String address = null;

        try {
            client.setTcpNoDelay(true);
            client.setSoTimeout(3000);

            if (!new HandshakeProxy(client.getInputStream(), client.getOutputStream(), data).start()) {
                Console.error("SESSION_FAIL_HS");
                stop();
                return;
            }

            if (data.getHandshake() == null) { // Server list ping
                stop();
                return;
            }

            PacketHandshake handshake = data.getHandshake();
            address = InetAddress.getByName(handshake.getAddress()).getHostAddress();

            // We need to make sure the connection is ignored by the TCP interceptor, so it won't be redirected
            Glowsquid.ignore(address);

            server.setTcpNoDelay(true);
            server.setSoTimeout(3000);

            server.connect(new InetSocketAddress(address, handshake.getPort()));

            CLoginProxy clientLogin = new CLoginProxy(
                    client.getInputStream(),
                    server.getOutputStream(),
                    data
            );

            SLoginProxy serverLogin = new SLoginProxy(
                    server.getInputStream(),
                    client.getOutputStream(),
                    data
            );

            // Handle the connection between the client and the local server
            threadPool.submit(() -> {
                try {
                    if (!clientLogin.start()) { // Client login interceptor failed
                        if (stopped) {
                            return;
                        }

                        Console.error("SESSION_FAIL_CLIENT");

                        clientLogin.stop(false);
                        serverLogin.stop(false);
                        stop();

                        return;
                    }

                    // Auth is complete, we can now start the packet event system
                    ProxySessionProperties auth = new ProxySessionProperties(data);

                    auth.setDirection(PacketInfo.PacketDirection.OUTBOUND);

                    clientMCConnection = new PlayProxy(
                            client.getInputStream(),
                            server.getOutputStream(),
                            auth,
                            lock.writeLock()
                    );

                    boolean result = clientMCConnection.start();

                    if (!stopped) {
                        if (result) {
                            Console.info("SESSION_FINISH");
                            stop();
                        } else {
                            Console.error("SESSION_FAIL_CLIENT");
                            stop();
                        }
                    }
                } catch (Throwable exception) {
                    exception.printStackTrace();
                    stop();
                }
            });

            if (!serverLogin.start()) { // Server login interceptor failed
                if (stopped) {
                    return;
                }

                Console.error("SESSION_FAIL_SERVER");

                serverLogin.stop(false);
                clientLogin.stop(false);
                stop();

                return;
            }

            ProxySessionProperties serverData = new ProxySessionProperties(data);

            serverData.setDirection(PacketInfo.PacketDirection.INBOUND);

            // Set compression is sent after the client play proxy starts, so it needs to be updated
            clientMCConnection.builder.compression(serverData.getCompressionThreshold());

            manager = ModuleManager.getInstance();
            serverMCConnection = new PlayProxy(
                    serverLogin.input,
                    serverLogin.output,
                    serverData,
                    lock.readLock()
            );

            manager.setSession(this);
            manager.getCoreModules().forEach(module -> module.setEnabled(true));
            threadPool.submit(injector.run(lock, serverMCConnection.output, clientMCConnection.output));

            try {
                boolean result = serverMCConnection.start();

                if (!stopped) {
                    if (result) {
                        Console.info("SESSION_FINISH");
                        stop();
                    } else {
                        Console.error("SESSION_FAIL_SERVER");

                        serverMCConnection.stop(false);
                        clientMCConnection.stop(false);
                        stop();
                    }
                }
            } finally {
                ModuleManager.getInstance().setSession(null);
            }
        } catch (Throwable exception) {
            Console.error("SESSION_ERROR");
            exception.printStackTrace();
            stop();
        } finally {
            if (manager != null) {
                manager.unregisterAll();
            }

            if (address != null) {
                Glowsquid.unignore(address);
            }
        }
    }

    public void stop() {
        stopped = true;

        try {
            if (server.isConnected() && !server.isInputShutdown() && !server.isClosed()) {
                server.shutdownInput();
            }

            if (client.isConnected() && !client.isInputShutdown() && !client.isClosed()) {
                client.shutdownInput();
            }
        } catch (IOException exception) {
            Console.error("SESSION_STOP_FAIL");
            exception.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

    public void scheduleRepeatingTask(Module module, Runnable task) {
        injector.scheduleTask(module, task);
    }

    public void removeRepeatingTasks(Module module) {
        injector.removeTasks(module);
    }

    public void queueInbound(Packet packet) {
        injector.queueOutbound(clientMCConnection.builder.write(packet).getPacket());
    }

    public void queueOutbound(Packet packet) {
        injector.queueInbound(serverMCConnection.builder.write(packet).getPacket());
    }

    public boolean is18() {
        return data.is18();
    }
}
