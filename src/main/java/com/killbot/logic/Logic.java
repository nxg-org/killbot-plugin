package com.killbot.logic;

import com.killbot.KillBot;
import com.killbot.manager.BotManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import org.bukkit.inventory.ItemStack;

public abstract class Logic {

    protected final KillBot plugin;
    protected Player target;
    public String username;
    public BukkitScheduler scheduler;
    public ItemStack defaultItem;
    private ServerPlayer bot;

    protected Logic(ServerPlayer player) {
        plugin = KillBot.getPlugin();
        bot = player;
    }

    public ServerPlayer getBot() {
        return bot;
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

    public double distanceTo(Vec3 vec) {
        return bot.position().distanceTo(vec);
    }


    public abstract void init();

    public abstract void release();

    public abstract void moveToPosition(Location loc);

    public abstract void attackTarget();

    public abstract void attackNearestNonBotPlayer();

    public abstract void attackNearestEntity();

}
