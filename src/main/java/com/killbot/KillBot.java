package com.killbot;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.killbot.handlers.KnockbackHandler;
import com.killbot.logic.Logic;
import com.killbot.manager.BotManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Hello world!
 */
public final class KillBot extends JavaPlugin {

    private static KillBot plugin;
    private BotManager manager;
    private Logic logic;

    @Override
    public void onEnable() {
        plugin = this;
        manager = new BotManager();
        logic = manager.getLogic();

        PacketEvents.getAPI().getEventManager().registerListener(new KnockbackHandler(), PacketListenerPriority.LOW, true);
        PacketEvents.getAPI().init();
    }

    @Override
    public void onDisable() {
    }

    /**
     * @return the manager
     */
    public BotManager getManager() {
        return manager;
    }

    /**
     * @return the plugin
     */
    public static KillBot getPlugin() {
        return plugin;
    }

    /**
     * @param manager the manager to set
     */
    public void setManager(BotManager manager) {
        this.manager = manager;
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

}
