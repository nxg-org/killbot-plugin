package com.killbot.api;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;

import java.util.Optional;

import com.killbot.KillBot;
import com.killbot.util.Debugger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class HuntCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdName, String[] args) {
        if (sender.hasPermission("killbot.huntstart")) {
            KillBot.getPlugin().getManager().applyLogicToAll();
        }

        return false;
    }

}