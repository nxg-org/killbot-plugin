package com.killbot.manager;

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

    public void renderPlayer(ServerPlayer player) {
        server.getOnlinePlayers().forEach(p -> {
            ServerGamePacketListenerImpl ps = ((CraftPlayer) p).getHandle().connection;
            sendAllPackets(ps, renderPackets(player));
        });
    }

    private void sendAllPackets(ServerGamePacketListenerImpl conn, Packet<?>[] packets) {
        for (Packet<?> packet : packets) {
            conn.send(packet);
        }
    }

    private Packet<?>[] renderPackets(ServerPlayer player) {
        return new Packet[] {
                new ClientboundAddPlayerPacket(player),
        };
    }
}
