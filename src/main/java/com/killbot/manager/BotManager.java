package com.killbot.manager;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.killbot.bot.BaseBot;
import com.killbot.logic.Logic;
import com.killbot.logic.TestLogic;
import com.killbot.util.BotUtils;
import com.killbot.util.MathUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity.RemovalReason;

//3345

/**
 * The plan is to use this class to manage all of the current entities.
 */
public class BotManager {

    private Integer botCount = 0;
    private final Set<BaseBot> bots;
    private final Set<Logic> activeBots;

    public BotManager() {
        botCount = 0;
        bots = ConcurrentHashMap.newKeySet();
        activeBots = ConcurrentHashMap.newKeySet();
    }

    public Set<BaseBot> getBots() {
        return bots;
    }

    public void createBots(int amount, Location loc) {
        createBots(amount, "KillBot", loc);
    }

    public void createBots(int amount, Player player) {
        createBots(amount, "KillBot", player.getLocation());
    }

    public void createBots(int amount, String tempName, Location loc) {
        for (int i = 0; i < amount; i++) {
            createBot(tempName, loc);
        }
    }

    public void createBots(int amount, String tempName, Player target) {
        for (int i = 0; i < amount; i++) {
            createBot(tempName, target.getWorld(), target.getLocation().toVector().clone().add(MathUtils.randomCircleOffset(5)));
        }
    }

    public void createBot(int amount, World world, double x, double y, double z) {
        createBots(amount, "KillBot", world, x, y, z);
    }

    public void createBots(int amount, String name, World world, double x, double y, double z) {
        Location loc = new Location(world, x, y, z);
        for (int i = 0; i < amount; i++) {
            createBot(name, loc);
        }
    }

    public void createBot(String tempName, World world, Vector vec) {
        createBot(tempName, vec.toLocation(world));
    }

    public void createBot(String tempName, Location loc) {
        botCount++;
        String botName = tempName;
        BaseBot.createBot(loc, botName);
    }

    public void destroyAllBots() {
        
        for (BaseBot bot : bots) {
            destroyBot(bot);
        }
    }

    public void destroyBot(BaseBot bot) {
            Logic log = getFirstActiveMatching(bot);
            if (log != null) {
                log.release();
                activeBots.remove(log);
            }
            packetRemoval(bot);
 
    }

    public void destroyBot(String ident) {
        BaseBot bot = getFirstBotMatching(ident);
        if (bot != null) {
            Logic log = getFirstActiveMatching(bot);
            if (log != null) {
                log.release();
                activeBots.remove(log);
            }
            packetRemoval(bot);
        }
    }

    private void packetRemoval(BaseBot bot) {
        bots.remove(bot);
        ServerLevel nmsWorld = ((CraftWorld) Objects.requireNonNull(bot.getBukkitEntity().getWorld())).getHandle();
        nmsWorld.removePlayerImmediately(bot, RemovalReason.DISCARDED);
    }


    public void applyLogicToAll() {
        for (BaseBot bot: bots) {
            removeLogic(bot);
            applyLogic(bot);
        }
    }


    public void removeLogicFromAll() {
        for (BaseBot bot: bots) {
            removeLogic(bot);
        }
    }

    public void applyLogic(BaseBot player) {
        Logic logicRunner = TestLogic.apply(player);
        activeBots.add(logicRunner);

    }

    public void removeLogic(BaseBot player) {
        for (Logic bot : activeBots) {
            if (bot.getBot() == player) {
                bot.release();
                activeBots.remove(bot);
            }
        }
    };

    public BaseBot getFirstBotMatching(String name) {
        for (BaseBot bot : bots) {
            if (bot.getName().equals(name)) {
                return bot;
            }
        }
        return null;
    }

    public Logic getFirstActiveMatching(String name) {
        for (Logic bot : activeBots) {
            if (bot.getBot().getName().equals(name)) {
                return bot;
            }
        }
        return null;
    }

    public Logic getFirstActiveMatching(BaseBot findBot) {
        for (Logic bot : activeBots) {
            if (bot.getBot() == findBot) {
                return bot;
            }
        }
        return null;
    }

    public BaseBot getClosest(Player player) {
        return getClosest(player.getLocation().toVector());
    }

    public BaseBot getClosest(Location loc) {
        return getClosest(loc.toVector());
    }

    public BaseBot getClosest(Vector loc) {
        BaseBot foundBot = null;
        double distance = 1000;
        for (BaseBot bot : bots) {
            double dist = bot.position().distanceToSqr(loc.getX(), loc.getY(), loc.getZ());
            if (dist < distance) {
                foundBot = bot;
                distance = dist;
            }
        }
        return foundBot;

    }
}
