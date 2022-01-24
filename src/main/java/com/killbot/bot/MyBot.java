package com.killbot.bot;

import java.util.Objects;
import java.util.UUID;

import com.killbot.KillBot;
import com.killbot.util.BotUtils;
import com.killbot.util.MojangAPI;
import com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.util.Vector;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class MyBot extends ServerPlayer {

    private MyBot(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile) {
        super(minecraftserver, worldserver, gameprofile);
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
        nmsWorld.addEntity(npc, SpawnReason.CUSTOM);
        npc.setPos(x, y, z);
        npc.getBukkitEntity().setNoDamageTicks(0);

        KillBot.getPlugin().getManager().getBots().add(npc); // may move to manager.
        KillBot.getRenderer().renderToAll(npc); // may move to manager.
        return npc;
    }

    public Vector getVelocity() {
        return this.getVelocity().clone();
    }

}
