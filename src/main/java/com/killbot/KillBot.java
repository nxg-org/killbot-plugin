package com.killbot;

// import com.github.retrooper.packetevents.PacketEvents;
// import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.killbot.api.CreateKillBotCommand;
import com.killbot.api.DestroyKillBotsCommand;
// import com.killbot.handlers.KnockbackHandler;
import com.killbot.logic.Logic;
import com.killbot.manager.BotManager;
import com.killbot.manager.RenderManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Hello world!
 */
public final class KillBot extends JavaPlugin {

    private static KillBot plugin;
    private static Logic logic;
    private static RenderManager renderer;
    private BotManager manager;

    @Override
    public void onEnable() {
        plugin = this;
        manager = new BotManager();
        renderer = new RenderManager();

        // PacketEvents.getAPI().getEventManager().registerListener(new KnockbackHandler(), PacketListenerPriority.LOW,
        //         true);
        // PacketEvents.getAPI().init();

        getCommand("kbsummon").setExecutor(new CreateKillBotCommand());
        getCommand("kbclear").setExecutor(new DestroyKillBotsCommand());
    }

    @Override
    public void onDisable() {
        manager.destroyAllBots();
    }

    /**
     * @return the renderer
     */
    public static RenderManager getRenderer() {
        return renderer;
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
        KillBot.logic = logic;
    }

}
