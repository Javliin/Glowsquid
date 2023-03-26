package me.javlin.glowsquid.mc;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class Player {
    private double x;
    private double y;
    private double z;

    @Setter
    private int entityId;

    public Player(int entityId, double x, double y, double z) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Player(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Player setPos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;

        return this;
    }

    public double distanceTo(Player player) {
        if (player == null) {
            return 9999;
        }

        return Math.sqrt(Math.pow((x) - (player.getX()), 2) + Math.pow((y) - (player.getY()), 2) + Math.pow((z) - (player.getZ()), 2));
    }
}
