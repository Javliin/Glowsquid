package me.javlin.glowsquid.network.proxy.module;

import lombok.Getter;
import me.javlin.glowsquid.Console;
import me.javlin.glowsquid.mc.Player;
import me.javlin.glowsquid.network.proxy.ProxySession;
import me.javlin.glowsquid.network.packet.event.PacketEventHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    private final ConcurrentHashMap<Integer, Player> playerList = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Long> recentlyAttackedPlayers = new ConcurrentHashMap<>();

    private final Player player = new Player(0, 0, 0);

    protected ProxySession session;

    public ModuleManager(ProxySession session) {
        this.session = session;
    }

    public ModuleManager register(Class<? extends Module> clazz) {
        try {
            Module module = clazz.getConstructor(ModuleManager.class).newInstance(this);
            modules.add(module);
            PacketEventHandler.register(module);
        } catch (NoSuchMethodException
                 | InstantiationException
                 | IllegalAccessException
                 | InvocationTargetException exception) {
            Console.error("MODULE_FAIL_CREATE", clazz.getSimpleName());
            exception.printStackTrace();
        }

        return this;
    }

    public void unregister() {
        PacketEventHandler.unregisterAll();
    }
}
