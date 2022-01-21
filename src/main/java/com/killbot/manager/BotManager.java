package com.killbot.manager;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.killbot.logic.Logic;
import com.killbot.logic.test.TestLogic;

import org.bukkit.entity.Player;

import net.minecraft.server.level.ServerPlayer;

/**
 * The plan is to use this class to manage all of the current entities.
 */
public class BotManager {

    private Integer botCount = 0;
    private Logic logic;
    private final Set<ServerPlayer> bots;

    public BotManager() {
        botCount = 0;
        logic = new TestLogic(this);
        bots = ConcurrentHashMap.newKeySet();
    }

    public Set<ServerPlayer> getBots() {
        return bots;
    }

    public void createBots(Player player, int amount, Optional<String> tempName, Optional<Player> target) {
        botCount++;
        String botName = tempName.orElse("KillBot" + botCount);
        for (int i = 0; i < amount; i++) {
        }
        // bots.add();

    }


    public void applyLogic(ServerPlayer player) {
        
    }

    public ServerPlayer getFirstMatchingName(String name) {
        for (ServerPlayer bot : bots) {
            if (bot.getName().equals(name)) {
                return bot;
            }
        }
        return null;
    }

    /**
     * @return the logic
     */
    public Logic getLogic() {
        return logic;
    }

    /**
     * @param logic the logic to set
     */
    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    // public BaseBot getClosestMatchingName(String name) {
    // return bots.getClass();
    // }

}
