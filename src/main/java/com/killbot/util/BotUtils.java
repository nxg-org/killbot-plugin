package com.killbot.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class BotUtils {

    public static final Set<Material> NO_FALL = new HashSet<>(Arrays.asList(
            Material.WATER,
            Material.LAVA,
            Material.TWISTING_VINES,
            Material.VINE));

    public static UUID randomSteveUUID() {
        UUID uuid = UUID.randomUUID();

        if (uuid.hashCode() % 2 == 0) {
            return uuid;
        }

        return randomSteveUUID();
    }




    public static void safeTeleport(Entity bot, Vector loc) {
        safeTeleport(bot, loc.getX(), loc.getY(), loc.getZ());
    }

    public static void safeTeleport(Entity bot, double d0, double d1, double d2) {
        double d3 = bot.getX();
        double d4 = bot.getY();
        double d5 = bot.getZ();
        double d6 = d1;
        boolean flag1 = false;
        BlockPos blockposition = new BlockPos(d0, d1, d2);
        Level level = bot.level;
        if (level.hasChunkAt(blockposition)) {
            boolean flag2 = false;

            while (!flag2 && blockposition.getY() > level.getMinBuildHeight()) {
                BlockPos blockposition1 = blockposition.down();
                BlockState iblockdata = level.getBlockState(blockposition1);
                if (iblockdata.getMaterial().blocksMotion()) {
                    flag2 = true;
                } else {
                    --d6;
                    blockposition = blockposition1;
                }
            }

            if (flag2) {
                bot.setPos(d0, d6, d2);
                if (level.noCollision(bot) && !level.containsAnyLiquid(bot.getBoundingBox())) {
                    flag1 = true;
                }

                bot.setPos(d3, d4, d5);
                if (flag1) {

                    EntityTeleportEvent teleport = new EntityTeleportEvent(bot.getBukkitEntity(),
                            new Location(level.getWorld(), d3, d4, d5), new Location(level.getWorld(), d0, d6, d2));
                    level.getCraftServer().getPluginManager().callEvent(teleport);
                    if (teleport.isCancelled()) {
                        return;
                    }

                    Location to = teleport.getTo();
                    bot.teleportTo(to.getX(), to.getY(), to.getZ());
                }

            }
        }
    }
}
