package me.javlin.glowsquid.network.proxy.module;

import me.javlin.glowsquid.mc.Player;
import me.javlin.glowsquid.network.DelayedPacket;
import me.javlin.glowsquid.network.DelayedType;
import me.javlin.glowsquid.network.proxy.ProxySession;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Module {
    protected final ConcurrentHashMap<Integer, Player> playerList;
    protected final ConcurrentHashMap<Integer, Long> recentlyAttackedPlayers;
    protected final ConcurrentHashMap<DelayedType, List<DelayedPacket>> delayedPacketQueues;

    protected final Player player;
    protected final boolean is18;

    protected ProxySession session;

    public Module(ModuleManager manager) {
        this.session = manager.getSession();
        this.playerList = manager.getPlayerList();
        this.recentlyAttackedPlayers = manager.getRecentlyAttackedPlayers();
        this.delayedPacketQueues = session.getDelayedPacketQueue();
        this.player = manager.getPlayer();
        this.is18 = session.is18();
    }
}
