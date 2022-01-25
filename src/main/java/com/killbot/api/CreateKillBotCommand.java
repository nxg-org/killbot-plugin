package com.killbot.api;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.minecraft.server.level.ServerPlayer;

import org.bukkit.command.CommandExecutor;

import java.util.Optional;

import com.killbot.KillBot;
import com.killbot.bot.MyBot;
import com.killbot.util.Debugger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class CreateKillBotCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdName, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("killbot.summon")) {
                Player player = (Player) sender;

                String name = Optional.of(args[0]).orElse("KillBot");// StringUtils.defaultString(args[0], "KillBot");
                Integer amount = NumberUtils.toInt(args[1], 1);
                Player target = null;
                if (args.length == 3) {

                    for (Player test : Bukkit.getServer().getOnlinePlayers()) {
                        if (test.getDisplayName().equals(args[2])) {
                            target = test;
                        }
                    }
                }

                player.sendMessage(
                        ChatColor.WHITE + "Summoning " + ChatColor.RED + amount + " " + ChatColor.WHITE + "KillBots.");

                if (target != null) {
                    KillBot.getPlugin().getManager().createBots(amount, name, target);
                } else {
                    KillBot.getPlugin().getManager().createBots(amount, name, player.getLocation());
                }

          

                return true;
            }
        } else {
            String name = StringUtils.defaultString(args[0], "KillBot");
            Integer amount = NumberUtils.toInt(args[1], 1);
            Debugger.log(ChatColor.WHITE + "Summoning " + ChatColor.RED + amount + " " + ChatColor.WHITE + "KillBots.");

            if (args.length == 3) {
                Player target = null;
                for (Player test : Bukkit.getServer().getOnlinePlayers()) {
                    if (test.getDisplayName().equals(args[2])) {
                        target = test;
                    }
                }

                if (target != null) {
                    KillBot.getPlugin().getManager().createBots(amount, name, target);
                    return true;
                } else {
                    Debugger.log(ChatColor.WHITE + "Could not find player " + ChatColor.GOLD + args[3]);
                }

            } else if (args.length == 6) {
                Location loc = new Location(sender.getServer().getWorld(args[5]), NumberUtils.toInt(args[2], 0),
                        NumberUtils.toInt(args[3], 0), NumberUtils.toInt(args[4], 0));
                KillBot.getPlugin().getManager().createBots(amount, name, loc);
                return true;
            } else {
                Debugger.log(ChatColor.WHITE + "Did not specify a target.");
            }

        }

        return false;
    }

}