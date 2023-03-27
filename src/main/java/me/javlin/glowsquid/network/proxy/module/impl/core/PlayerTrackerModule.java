package me.javlin.glowsquid.network.proxy.module.impl.core;

import me.javlin.glowsquid.mc.Player;
import me.javlin.glowsquid.network.proxy.module.Module;
import me.javlin.glowsquid.network.proxy.module.ModuleManager;
import me.javlin.glowsquid.network.packet.event.PacketEvent;
import me.javlin.glowsquid.network.packet.event.PacketSendEvent;
import me.javlin.glowsquid.network.packet.impl.play.outbound.PacketUseEntity;
import me.javlin.glowsquid.network.packet.impl.play.inbound.PacketDestroyEntities;
import me.javlin.glowsquid.network.packet.impl.play.inbound.PacketJoinGame;
import me.javlin.glowsquid.network.packet.impl.play.inbound.PacketSpawnPlayer;

public class PlayerTrackerModule extends Module {
    private static final int RECENTLY_ATTACKED_STOREDTIME = 2500;

    private int tickCount;

    @Override
    public void onEnable() {
        manager.getSession().scheduleRepeatingTask(this, () -> {
            tickCount++;

            if(tickCount % 50 == 0) {
                recentlyAttackedPlayers.entrySet().removeIf(entry -> System.currentTimeMillis() >= entry.getValue());
            }
        });
    }

    @PacketEvent
    public void onJoinGame(PacketSendEvent<PacketJoinGame> event) {
        PacketJoinGame joinGame = event.getPacket();
        player.setEntityId(joinGame.getEntityId());
    }

    @PacketEvent
    public void onPlayerSpawn(PacketSendEvent<PacketSpawnPlayer> event) {
        PacketSpawnPlayer spawnPlayer = event.getPacket();
        playerList.put(spawnPlayer.getEntityId(), new Player(spawnPlayer.getEntityId(), spawnPlayer.getX(), spawnPlayer.getY(), spawnPlayer.getZ()));
    }

    @PacketEvent
    public void onEntityDespawn(PacketSendEvent<PacketDestroyEntities> event) {
        PacketDestroyEntities destroyEntities = event.getPacket();
        destroyEntities.getDestroyedEntities().forEach(playerList::remove);
    }

    @PacketEvent
    public void onUseEntity(PacketSendEvent<PacketUseEntity> event) {
        PacketUseEntity useEntity = event.getPacket();

        if (useEntity.getAction() == PacketUseEntity.Action.ATTACK) {
            recentlyAttackedPlayers.put(useEntity.getEntityId(), System.currentTimeMillis() + RECENTLY_ATTACKED_STOREDTIME);
        }
    }
}
