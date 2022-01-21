package com.killbot.util;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.permissions.ServerOperator;

public class Debugger {
    
    private static final String PREFIX = ChatColor.YELLOW + "[DEBUG] " + ChatColor.RESET;

    public static void log(Object... objects) {
        String[] values = formStringArray(objects);
        String message = PREFIX + String.join(" ", values);

        Bukkit.getConsoleSender().sendMessage(message);
        Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(p -> p.sendMessage(message));
    }

    private static String[] formStringArray(Object[] objects) {
        return Arrays.stream(objects).map(String::valueOf).toArray(String[]::new);
    }
}
