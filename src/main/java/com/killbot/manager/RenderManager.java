package com.killbot.manager;

import com.killbot.KillBot;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket.Action;
// import net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation;
// import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
// import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
// import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
// import net.minecraft.server.level.ServerPlayer;
// import net.minecraft.server.network.ServerGamePacketListenerImpl;
// import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.level.ServerPlayer;
// import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class RenderManager {
    private Server server;

    public RenderManager() {
        server = Bukkit.getServer();
    }

    public static void sendAll(Packet<?>[] packets) {
        RenderManager renderer = KillBot.getRenderer();
        renderer.sendToAll(packets);
    }

    public void sendToAll(Packet<?>[] packets) {
        server.getOnlinePlayers().forEach(p -> {
            // ServerGamePacketListenerImpl ps = new
            // ServerGamePacketListenerImpl(Bukkit.getServer(), p (ServerPlayer) p);
            ServerGamePacketListenerImpl ps = ((CraftPlayer) p).getHandle().connection;
            sendAllPackets(ps, packets);
        });
    }

    private static void sendAllPackets(ServerGamePacketListenerImpl conn, Packet<?>[] packets) {
        for (Packet<?> packet : packets) {
            conn.send(packet);
        }
    }

    public static void initRenderAll(ServerPlayer player) {
        RenderManager renderer = KillBot.getRenderer();
        renderer.initRenderToAll(player);
    }

    public void initRenderToAll(ServerPlayer player) {
        Packet<?>[] lazy = {new ClientboundPlayerInfoPacket(Action.ADD_PLAYER, player)};
        sendToAll(lazy);
    }

    public static void renderAll(ServerPlayer player) {
        RenderManager renderer = KillBot.getRenderer();
        renderer.renderToAll(player);
    }

    public void renderToAll(ServerPlayer player) {
        Packet<?>[] renderPackets = renderPackets(player);
        sendToAll(renderPackets);
    }

    private static Packet<?>[] renderPackets(ServerPlayer player) {
        return new Packet[] {
                new ClientboundAddPlayerPacket(player),
                new ClientboundSetEntityDataPacket(player.getId(), player.getEntityData(), true)
                // new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a,
                // player),
                // new PacketPlayOutNamedEntitySpawn(player),
                // new PacketPlayOutEntityMetadata(player.getId(), player.getDataWatcher(),
                // true),
                // new PacketPlayOutEntityHeadRotation(player, (byte) ((player.getYRot() * 256f)
                // / 360f))
        };
    }

    // private static Packet<?>[] addToTab(ServerPlayer player) {
    // return new Packet[] {
    // new ClientboundTabListPacket(var0, var1)
    // }
    // }
}
