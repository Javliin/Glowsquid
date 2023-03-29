package me.javlin.glowsquid;

import com.github.ffalcinelli.jdivert.WinDivert;
import com.github.ffalcinelli.jdivert.exceptions.WinDivertException;
import me.javlin.glowsquid.gui.GUIGlowsquid;
import me.javlin.glowsquid.network.interceptor.IInterceptor;
import me.javlin.glowsquid.network.proxy.ProxySession;
import me.javlin.glowsquid.network.interceptor.SInterceptor;
import me.javlin.glowsquid.network.interceptor.CInterceptor;
import me.javlin.glowsquid.network.packet.builder.PacketBuilder;
import me.javlin.glowsquid.network.proxy.module.ModuleManager;
import me.javlin.glowsquid.network.proxy.module.impl.PlayerTrackerModule;
import me.javlin.glowsquid.network.proxy.module.impl.FilterModule;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Glowsquid {
    public static final Map<String, String> ACCESS_TOKENS = new HashMap<>();
    public static final String VERSION = "v1.2";
    public static short PORT = 25565;

    private static final Map<String, Integer> IGNORED = new ConcurrentHashMap<>();
    private static final List<IInterceptor> INTERCEPTORS = new ArrayList<>();

    private static final ExecutorService PROXY_POOL = Executors.newCachedThreadPool(new GlowsquidThreadFactory("proxy"));
    private static final ExecutorService INTERCEPTOR_POOL = Executors.newFixedThreadPool(2, new GlowsquidThreadFactory("windivert"));

    private static final AtomicBoolean run = new AtomicBoolean(true);

    public static void main(String[] args) {
        Thread.currentThread().setName("glowsquid");
        GUIGlowsquid.getInstance(); // Initialize GUI

        try {
            PacketBuilder.registerPacketClasses();
        } catch (Throwable exception) {
            Console.error("REGISTER_FAIL");
            exception.printStackTrace();
            return;
        }

        if (args.length > 0) {
            try {
                PORT = Short.parseShort(args[0]);
            } catch (NumberFormatException exception) {
                Console.warn("INVALID_PORT");
            }
        }

        Console.info("START");

        // We need the access tokens of the connecting accounts to hijack authentication
        loadAccessTokens();

        if (ACCESS_TOKENS.isEmpty()) {
            Console.warn("NO_ACCOUNTS");
            return;
        }

        // Intercept outbound TCP connections to Minecraft servers and redirect them to our local proxy
        if (!startInterceptors()) {
            stop();
            return;
        }

        ModuleManager.getInstance()
                .register(PlayerTrackerModule.class)
                .register(FilterModule.class)
                .loadModulesDirectory();

        listen();
    }

    public static void stop() {
        run.set(false);
        INTERCEPTORS.forEach(IInterceptor::stop);

        PROXY_POOL.shutdownNow();
        INTERCEPTOR_POOL.shutdownNow();
    }

    public static void ignore(String address) {
        IGNORED.put(address, -1);
    }

    public static void unignore(String address) {
        IGNORED.remove(address);
    }

    // Local proxy to accept redirected MC connections
    private static void listen() {
        try (ServerSocket socket = new ServerSocket(PORT)) {
            Console.info("ACCEPTING");

            while (run.get()) {
                ProxySession connectionHandler = new ProxySession(socket.accept());

                PROXY_POOL.submit(connectionHandler::start);
            }
        } catch (Throwable exception) {
            Console.error("LISTEN_FAIL");
            exception.printStackTrace();
            stop();
        }
    }

    private static void loadAccessTokens() {
        String home = System.getProperty("user.home");

        if (loadAccessJSON(Console.get("LUNAR_CLIENT", home))
                | loadAccessJSON(Console.get("MINECRAFT", home))) {
            Console.info("SUCCESS_LOAD_AT", ACCESS_TOKENS.size());
        } else {
            Console.error("FAIL_LOAD_AT");
        }
    }

    private static boolean loadAccessJSON(String file) {
        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(new FileReader(file));

            List<String> accounts = new ArrayList<>();
            JSONObject authenticationDatabase = (JSONObject) jsonObject.get("accounts");

            for (Object account : authenticationDatabase.keySet()) {
                accounts.add((String) account);
            }

            for (String account : accounts) {
                JSONObject accountObject = (JSONObject) authenticationDatabase.get(account);
                JSONObject profileContainer = (JSONObject) accountObject.get("minecraftProfile");
                String accessToken = (String) accountObject.get("accessToken");

                if (accessToken == null
                        || profileContainer == null
                        || accessToken.isEmpty()) {
                    continue;
                }

                String name = (String) profileContainer.get("name");

                if (name == null || name.isEmpty()) {
                    continue;
                }

                ACCESS_TOKENS.put(name, accessToken);
            }

            return true;
        } catch (IOException | ParseException exception) {
            Console.error("FAIL_LOAD_AT_FILE", file);
            exception.printStackTrace();
            return false;
        }
    }

    private static boolean startInterceptors() {
        try {
            WinDivert outbound = new WinDivert("outbound && tcp");
            WinDivert inbound = new WinDivert("loopback && tcp");

            outbound.open();
            inbound.open();

            Map<Integer, String> bound = new ConcurrentHashMap<>();
            CInterceptor client = new CInterceptor(outbound, bound, IGNORED);
            SInterceptor server = new SInterceptor(inbound, bound);

            INTERCEPTORS.add(client);
            INTERCEPTORS.add(server);

            PROXY_POOL.submit(new InterceptorThread(client));
            PROXY_POOL.submit(new InterceptorThread(server));

            return true;
        } catch (WinDivertException exception) {
            if (exception.getCode() == 5) {
                Console.error("WD_FAIL_RUN_AS_ADMIN");
            } else {
                Console.error("WD_FAIL");
                exception.printStackTrace();
            }

            return false;
        }
    }

    private static class InterceptorThread implements Runnable {
        private final IInterceptor interceptor;

        public InterceptorThread(IInterceptor interceptor) {
            this.interceptor = interceptor;
        }

        @Override
        public void run() {
            try {
                interceptor.start();
            } catch (WinDivertException | UnknownHostException exception) {
                if (exception instanceof WinDivertException && ((WinDivertException) exception).getCode() == 995) { // Result of calling stop()
                    return;
                }

                Console.error("WD_ERROR", "OUTBOUND");
                exception.printStackTrace();
                stop();
            }
        }
    }
}
