package com.killbot.logic;

import com.killbot.KillBot;
import com.killbot.bot.BaseBot;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import net.minecraft.util.Mth;

import org.bukkit.inventory.ItemStack;

public abstract class Logic {

    protected final KillBot plugin;
    protected final BukkitScheduler scheduler;
    protected final BaseBot bot;
    protected Entity target;

    protected int taskID;

    protected Logic(BaseBot player) {
        plugin = KillBot.getPlugin();
        scheduler = Bukkit.getScheduler();
        bot = player;
    }

    public BaseBot getBot() {
        return bot;
    }

    public void setTarget(Entity player) {
        target = player;
    }

    public Entity getTarget() {
        return target;
    }

    public void clearTarget() {
        target = null;
    }

    public void init() {
        taskID = scheduler.scheduleSyncRepeatingTask(plugin, this::perTick, 0, 1);
    };

    public void release() {
        scheduler.cancelTask(taskID);
    };

    

    public double distanceTo(Vector vec) {
        return bot.position().distanceToSqr(vec.getX(), vec.getY(), vec.getZ());
    }



    public abstract void perTick();

    public abstract void moveToPosition(Location loc);

    public abstract void attackTarget();

    public abstract void attackNearestNonBotPlayer();

    public abstract void attackNearestEntity();

}
