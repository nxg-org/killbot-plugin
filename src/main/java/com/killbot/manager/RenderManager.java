package com.killbot.manager;

import com.killbot.KillBot;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.server.level.ServerPlayer;
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
        };
    }

    // private static Packet<?>[] addToTab(ServerPlayer player) {
    //     return new Packet[] {
    //         new ClientboundTabListPacket(var0, var1)
    //     }
    // }
}
