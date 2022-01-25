package com.killbot.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

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

    public static boolean solidAt(World world, double x, double y, double z) { // not perfect, still cuts corners of fences
        Block block = world.getBlockAt((int) x, (int)y, (int)z); // fast rounding.
        BoundingBox box = block.getBoundingBox();


        double minX = box.getMinX();
        double minY = box.getMinY();
        double minZ = box.getMinZ();

        double maxX = box.getMaxX();
        double maxY = box.getMaxY();
        double maxZ = box.getMaxZ();

        return x > minX && x < maxX && y > minY && y < maxY && z > minZ && z < maxZ;
    }

    public static List<Block> getSpawnOffsets(World world, Vector vec) {
        List<Block> lazy = new ArrayList<Block>() {
            {
                add(world.getBlockAt(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ()));
                add(world.getBlockAt(vec.getBlockX(), vec.getBlockY() + 1, vec.getBlockZ()));
                add(world.getBlockAt(vec.getBlockX(), vec.getBlockY() + 2, vec.getBlockZ()));
            }
        };
        return lazy;
    }

    public static Vector spawnPos(World world, Vector org) {
        List<Block> check = getSpawnOffsets(world, org);
        if (check.stream().anyMatch(block -> !block.isEmpty())) {
            return org.setX(org.getX() + offsetCheckPos(world, check, 0));
        }

        return org;
    }

    public static int offsetCheckPos(World world, List<Block> blocks, int offset) {
        if (blocks.stream().anyMatch(block -> !block.isEmpty())) {
            offset++;
            Block temp = blocks.get(2);
            List<Block> stream = blocks.subList(1, 3);
            stream.add(world.getBlockAt(temp.getX(), temp.getY() + 1, temp.getZ()));
            return offsetCheckPos(world, stream, offset);
        }

        return offset;
    }
}
