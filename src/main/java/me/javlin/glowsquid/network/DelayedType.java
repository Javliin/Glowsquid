package me.javlin.glowsquid.network;

import lombok.Getter;
import lombok.Setter;
import me.javlin.glowsquid.network.packet.PacketInfo;

@Getter
public enum DelayedType {
    PLAYER_RANGE(PacketInfo.PacketDirection.OUTBOUND, 150, "Player range delay"),
    ENEMY_RANGE(PacketInfo.PacketDirection.INBOUND, 100, "Enemy range delay"),
    ENEMY_DELAY(PacketInfo.PacketDirection.INBOUND, 50, "Enemy general delay"),
    TELEPORT_SMOOTHING(PacketInfo.PacketDirection.INBOUND, 100, "Teleport smoothing delay");

    private final PacketInfo.PacketDirection direction;
    private final String description;

    @Setter
    private int delay;

    DelayedType(PacketInfo.PacketDirection direction, int delay, String description) {
        this.direction = direction;
        this.delay = delay;
        this.description = description;
    }
}
