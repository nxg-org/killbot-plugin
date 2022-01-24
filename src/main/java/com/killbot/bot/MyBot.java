package com.killbot.bot;

import java.util.Objects;
import java.util.UUID;

import com.killbot.KillBot;
import com.killbot.util.BotUtils;
import com.killbot.util.MojangAPI;
import com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
public class MyBot extends ServerPlayer {

    private boolean velocityUpdated;
    private boolean positionUpdated;
    private Vector velocity;
    private Vector position;

    private int aliveTicks;
    private int kills;

    private byte fireTicks;
    private byte groundTicks;
    private byte jumpTicks;
    private byte noFallTicks;



    private MyBot(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile) {
        super(minecraftserver, worldserver, gameprofile);
        velocityUpdated = true;
        // TODO Auto-generated constructor stub
    }

    public static MyBot createBot(Location loc, String username) {
        return createBot(username, MojangAPI.getSkin(username), loc);
    }

    public static MyBot createBot(String username, String[] skin, Location loc) {
        ServerLevel nmsWorld = (ServerLevel) Objects.requireNonNull(loc.getWorld());
        return createBot(username, skin, loc.toVector(), nmsWorld);

    }

    public static MyBot createBot(String username, String[] skin, Vector loc, ServerLevel nmsWorld) {
        return createBot(username, skin, nmsWorld, loc.getX(), loc.getY(), loc.getZ());
    }

    public static MyBot createBot(String username, String[] skin, ServerLevel nmsWorld, double x, double y, double z) {
        MinecraftServer nmsServer = (MinecraftServer) Bukkit.getServer();

        UUID uuid = BotUtils.randomSteveUUID();
        MyBot npc = new MyBot(nmsServer, nmsWorld, new GameProfile(uuid, username));
        npc.setPos(x, y, z);
        npc.getBukkitEntity().setNoDamageTicks(0);
        nmsWorld.addEntity(npc, SpawnReason.CUSTOM);
        
        KillBot.getPlugin().getManager().getBots().add(npc); // may move to manager.
        KillBot.getRenderer().renderToAll(npc); // may move to manager.
        return npc;
    }




    @Override
    public void tick() {
        super.tick();
        this.velocityUpdated = false;
        this.positionUpdated = false;

        if (!this.isAlive()) return;

        aliveTicks++;
        if (fireTicks > 0) --fireTicks;
        if (jumpTicks > 0) --jumpTicks;
        if (noFallTicks > 0) --noFallTicks;

        if (checkGround()) {
            if (groundTicks < 5) groundTicks++;
        } else {
            groundTicks = 0;
        }



        float health = getHealth();
        float maxHealth = getMaxHealth();
        float regenAmount = 0.025f;
        float amount;

        if (health < maxHealth - regenAmount) {
            amount = health + regenAmount;
        } else {
            amount = maxHealth;
        }

        setHealth(amount);
        // oldVelocity = velocity.clone();
    }




    //Inefficient.
    public boolean checkGround() {
        double vy = velocity.getY();

        if (vy > 0) {
            return false;
        }

        World world = getBukkitEntity().getWorld();
        BoundingBox box = getBukkitEntity().getBoundingBox();

        double[] xVals = new double[] {
            box.getMinX(),
            box.getMaxX()
        };

        double[] zVals = new double[] {
            box.getMinZ(),
            box.getMaxZ()
        };

        for (double x : xVals) {
            for (double z : zVals) {
                Location loc = new Location(world, x, position.getY() - 0.01, z);
                Block block = world.getBlockAt(loc);

                if (block.getType().isSolid() && BotUtils.solidAt(loc)) {
                    return true;
                }
            }
        }

        return false;
    }


    public Vector getVelocity() {
        if (velocityUpdated) return velocity;
        else {
            velocity = this.getBukkitEntity().getVelocity().clone();
            velocityUpdated = true;
            return velocity;
        }
    }

    public Vector getPosition() {
        if (positionUpdated) return position;
        else {
            position = getBukkitEntity().getLocation().toVector();
            positionUpdated = true;
            return position;
        }
    }


}
