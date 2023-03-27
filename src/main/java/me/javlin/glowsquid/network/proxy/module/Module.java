package me.javlin.glowsquid.network.proxy.module;

import lombok.Getter;
import me.javlin.glowsquid.Glowsquid;
import me.javlin.glowsquid.mc.Player;
import me.javlin.glowsquid.network.packet.event.PacketEventHandler;
import me.javlin.glowsquid.network.proxy.ProxySession;

import java.util.concurrent.ConcurrentHashMap;

public class Module {
    protected static final ModuleManager manager = Glowsquid.getModuleManager();

    protected final ConcurrentHashMap<Integer, Player> playerList;
    protected final ConcurrentHashMap<Integer, Long> recentlyAttackedPlayers;

    protected final Player player;

    protected ProxySession session;

    @Getter
    protected boolean isEnabled = false;

    public Module() {
        this.session = manager.getSession();
        this.playerList = manager.getPlayerList();
        this.recentlyAttackedPlayers = manager.getRecentlyAttackedPlayers();
        this.player = manager.getPlayer();
    }

    public void onEnable() {}
    public void onDisable() {}

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;

        if (enabled) {
            PacketEventHandler.register(this);
            onEnable();
        } else {
            PacketEventHandler.unregister(this);
            session.removeRepeatingTasks(this);
            onDisable();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
