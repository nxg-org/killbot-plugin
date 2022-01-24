package com.killbot.bot;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.libs.kyori.adventure.text.Component;
import com.github.retrooper.packetevents.manager.npc.NPC;
import com.github.retrooper.packetevents.manager.npc.NPCManager;
import com.github.retrooper.packetevents.protocol.player.GameProfile;
import com.github.retrooper.packetevents.protocol.world.Location;

public class PacketEventsBot {


    public static NPC spawn(Location loc, Component displayName, GameProfile profile) {
        NPCManager npcManager = PacketEvents.getAPI().getNPCManager();
        NPC npc = new NPC(displayName, 0, profile);
        return npc;
    }
    
}
