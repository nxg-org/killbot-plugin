package com.killbot.logic;

import com.killbot.KillBot;
import com.killbot.manager.BotManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import org.bukkit.inventory.ItemStack;

public abstract class Logic {

    protected final KillBot plugin;
    protected Player target;
    public String username;
    public BukkitScheduler scheduler;
    public ItemStack defaultItem;
    private BotManager manager;

    public Logic(BotManager botManager) {
        plugin = KillBot.getPlugin();
        manager = botManager;
    }

    public void setTarget(Player player) {
        target = player;
    }

    public Player getTarget() {
        return target;
    }

    public void clearTarget() {
        target = null;
    }

    public void distanceTo() {

    }

    public abstract void moveToPosition(Location loc);

    public abstract void attackTarget();

    public abstract void attackNearestNonBotPlayer();

    public abstract void attackNearestEntity();

}
