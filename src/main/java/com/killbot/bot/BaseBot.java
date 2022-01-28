package com.killbot.bot;

import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;

import com.killbot.KillBot;
import com.killbot.util.BotUtils;
import com.killbot.util.Debugger;
import com.killbot.util.MojangAPI;
import com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.util.Vector;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;

public class BaseBot extends ControllablePlayer {

    private int kills;
    public double reach;


    public static BaseBot createBot(Location loc, String username) {
        return createBot(username, MojangAPI.getSkin(username), loc);
    }

    public static BaseBot createBot(String username, String[] skin, Location loc) {

        return createBot(username, skin, loc.getWorld(), loc.toVector());

    }

    public static BaseBot createBot(String username, String[] skin, World world, Vector loc) {
        return createBot(username, skin, world, loc.getX(), loc.getY(), loc.getZ());
    }


    public static BaseBot createBot(String username, String[] skin, World world, double x, double y, double z) {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel nmsWorld = ((CraftWorld) Objects.requireNonNull(world)).getHandle();

        UUID uuid = BotUtils.randomSteveUUID();
        BaseBot npc = new BaseBot(nmsServer, nmsWorld, new GameProfile(uuid, username));
        npc.connection = new ServerGamePacketListenerImpl(nmsServer, new Connection(PacketFlow.CLIENTBOUND) {
            @Override
            public void send(Packet<?> packet,
                    @Nullable GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
                        // Debugger.log(packet);
            }
        }, npc);

        // npc.safeTeleport(x, y, z, false, TeleportCause.COMMAND);
        npc.getBukkitEntity().setNoDamageTicks(0);
        KillBot.getRenderer().initRenderToAll(npc);

        BotUtils.safeTeleport(npc, x, y, z);
        nmsWorld.addEntity(npc, SpawnReason.CUSTOM);
        KillBot.getPlugin().getManager().getBots().add(npc); // may move to manager.
        KillBot.getRenderer().renderToAll(npc);

        return npc;
    }

    /**
     * 
     */
    protected BaseBot(MinecraftServer minecraftserver, ServerLevel serverLevel, GameProfile gameprofile) {
        super(minecraftserver, serverLevel, gameprofile);
        this.reach = 3.0D;
    }

    public void realisticAttack(org.bukkit.entity.Entity e) {
        realisticAttack(((CraftPlayer) e).getHandle());
    }

    public void realisticAttack(Entity e) {
        if (this.distanceTo(e) < this.reach) {
            this.lookAt(Anchor.EYES, e, Anchor.EYES);
            this.attack(e);
            this.swing(InteractionHand.MAIN_HAND);
        }

    }


    @Override
    public void tick() {
        super.tick();
    }

}
