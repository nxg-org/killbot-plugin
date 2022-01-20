package com.killbot;

import org.bukkit.plugin.java.JavaPlugin;


/**
 * Hello world!
 */
public final class KillBot extends JavaPlugin {

    private static KillBot plugin;


    @Override
    public void onEnable() {
        plugin = this;
    }
    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }


    public static KillBot getPlugin() {
        return plugin;
    }
}
