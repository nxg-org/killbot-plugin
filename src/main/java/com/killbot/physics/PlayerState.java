package com.killbot.physics;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class PlayerState {
    private final World world;
    private Vector position;
    private Vector velocity;
    private float yaw;
    private float pitch;
    private BoundingBox aabb;

    private boolean isInWater;
    private boolean isInLava;
    private boolean isInWeb;
    private boolean isCollidedVertically;
    private boolean isCollidedHorizontally;

    public PlayerState(World world, Vector velocity, Vector position, float yaw, float pitch, BoundingBox box) {
        this.world = world;
        this.position = position;
        this.velocity = velocity;
        this.yaw = yaw;
        this.pitch = pitch;
        this.aabb = box;
    }

    public PlayerState(Location loc, Vector vel, BoundingBox box) {
        this.world = loc.getWorld();
        this.position = loc.toVector();
        this.velocity = vel.clone();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
        this.aabb = box;
    };

    public PlayerState(Player player) {
        this.world = player.getWorld();
        this.position = player.getLocation().toVector();
        this.velocity = player.getVelocity();
        this.yaw = player.getLocation().getYaw();
        this.pitch = player.getLocation().getPitch();
        this.aabb = player.getBoundingBox();
    }

    public void updateAABB(Player player) {
        this.aabb = player.getBoundingBox();
    }
}
