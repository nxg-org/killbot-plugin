package com.killbot.bot;

import java.util.Objects;
import java.util.UUID;

import com.killbot.KillBot;
import com.killbot.util.BotUtils;
import com.killbot.util.MojangAPI;
import com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.entity.player.Player;

public class MyBot extends ServerPlayer {

    private MyBot(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile) {
        super(minecraftserver, worldserver, gameprofile);
        // TODO Auto-generated constructor stub
    }

    public static MyBot createBot(Location loc, String username) {
        return createBot(loc, username, MojangAPI.getSkin(username));
    }

    public static MyBot createBot(Location loc, String username, String[] skin) {
        MinecraftServer nmsServer = (MinecraftServer) Bukkit.getServer();
        ServerLevel nmsWorld = (ServerLevel) Objects.requireNonNull(loc.getWorld());

        UUID uuid = BotUtils.randomSteveUUID();
        MyBot npc = new MyBot(nmsServer, nmsWorld, new GameProfile(uuid, username));
        KillBot.getPlugin().getManager().getBots().add(npc);
        return npc;

    }

    public void setItem(ItemStack itemStack) {

    }

}
