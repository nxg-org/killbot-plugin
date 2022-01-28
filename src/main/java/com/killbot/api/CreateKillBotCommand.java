package com.killbot.api;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.minecraft.server.level.ServerPlayer;

import org.bukkit.command.CommandExecutor;

import java.util.Optional;

import com.killbot.KillBot;
import com.killbot.bot.ControllablePlayer;
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
                Location targetLoc = null;
                if (args.length == 3) {

                    for (Player test : Bukkit.getServer().getOnlinePlayers()) {
                        if (test.getDisplayName().equals(args[2])) {
                            target = test;
                        }
                    }
                } else if (args.length == 5) {
                    targetLoc = new Location(player.getWorld(), NumberUtils.toInt(args[2], 0),
                            NumberUtils.toInt(args[3], 0), NumberUtils.toInt(args[4], 0));
                }

                player.sendMessage(
                        ChatColor.WHITE + "Summoning " + ChatColor.RED + amount + " " + ChatColor.WHITE + "KillBots.");

                if (targetLoc != null) {
                    KillBot.getPlugin().getManager().createBots(amount, name, targetLoc);
                } else if (target != null) {
                    KillBot.getPlugin().getManager().createBots(amount, name, target);
                } else {
                    KillBot.getPlugin().getManager().createBots(amount, name, player.getLocation());
                }

                return true;
            }
        } else {
            String name = StringUtils.defaultString(args[0], "KillBot");
            Integer amount = NumberUtils.toInt(args[1], 1);
            Player target = null;
            Location targetLoc = null;
            if (args.length == 3) {

                for (Player test : Bukkit.getServer().getOnlinePlayers()) {
                    if (test.getDisplayName().equals(args[2])) {
                        target = test;
                    }
                }
            } else if (args.length == 6) {
                targetLoc = new Location(Bukkit.getWorld(args[5]), NumberUtils.toInt(args[2], 0),
                        NumberUtils.toInt(args[3], 0), NumberUtils.toInt(args[4], 0));
            }

       

            if (targetLoc != null) {
                Debugger.log(ChatColor.WHITE + "Summoning " + ChatColor.RED + amount + " " + ChatColor.WHITE + "KillBots.");
                KillBot.getPlugin().getManager().createBots(amount, name, targetLoc);
            } else if (target != null) {
                Debugger.log(ChatColor.WHITE + "Summoning " + ChatColor.RED + amount + " " + ChatColor.WHITE + "KillBots.");
                KillBot.getPlugin().getManager().createBots(amount, name, target);
            } else {
                Debugger.log(ChatColor.WHITE + "Did not specify a target.");
                return false;
            }

            return true;

        }

        return false;
    }

}