package com.killbot.api;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;

public class CreateKillBotCommand implements CommandExecutor {

    /**
     * 
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdName, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("killbot.summon")) {
                Player player = (Player) sender;
                player.sendMessage(ChatColor.WHITE + "Summoning " + ChatColor.RED + amount + " " + ChatColor.WHITE + "KillBots.");
            }
        }
    }

    
}