package com.killbot.manager;

import com.killbot.KillBot;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
public class RenderManager {
    private Server server;

    public RenderManager() {
        server = Bukkit.getServer();
    }

    public void renderToAll(ServerPlayer player) {
        server.getOnlinePlayers().forEach(p -> {
            // ServerGamePacketListenerImpl ps = new ServerGamePacketListenerImpl(Bukkit.getServer(), p (ServerPlayer) p);
            ServerGamePacketListenerImpl ps = ((CraftPlayer) p).getHandle().connection;
            sendAllPackets(ps, renderPackets(player));
        });
    }

    public static void renderAll(ServerPlayer player) {
        RenderManager renderer = KillBot.getRenderer();
        renderer.renderToAll(player);
    }

    private static void sendAllPackets(ServerGamePacketListenerImpl conn, Packet<?>[] packets) {
        for (Packet<?> packet : packets) {
            conn.send(packet);
        }
    }

    private static Packet<?>[] renderPackets(ServerPlayer player) {
        return new Packet[] {
                new ClientboundAddPlayerPacket(player),
        };
    }
}
