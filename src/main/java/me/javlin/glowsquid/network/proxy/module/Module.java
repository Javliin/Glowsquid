package me.javlin.glowsquid.network.proxy.module;

import lombok.Getter;
import me.javlin.glowsquid.mc.Player;
import me.javlin.glowsquid.network.packet.event.PacketEventHandler;

import java.util.concurrent.ConcurrentHashMap;

public class Module {
    protected static final ModuleManager manager = ModuleManager.getInstance();

    protected final ConcurrentHashMap<Integer, Player> playerList;
    protected final ConcurrentHashMap<Integer, Long> recentlyAttackedPlayers;

    protected final Player player;

    @Getter
    protected boolean isEnabled = false;

    public Module() {
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
            manager.getSession().removeRepeatingTasks(this);
            onDisable();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
