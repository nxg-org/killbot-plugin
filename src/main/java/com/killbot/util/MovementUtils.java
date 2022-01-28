package com.killbot.util;

import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class MovementUtils {
   
    

    public static boolean anyBlocksSolidUnderneath(ServerPlayer bot) {
        World world = bot.getBukkitEntity().getWorld();
        BoundingBox box = bot.getBukkitEntity().getBoundingBox();
        Vector max = box.getMax();
        Vector min = box.getMin();
        // AABB box = bot.getBoundingBox().inflate(0.1, 0, 0.1);
        // Vector max = new Vector(box.maxX, box.maxY, box.maxZ);
        // Vector min = new Vector(box.minX, box.minY, box.minZ);
        int y = bot.getBlockY() - 1;
        for (int inc = min.getBlockX(); inc <= max.getBlockX(); inc++) {
            for (int inc1 = min.getBlockZ(); inc1 <= max.getBlockZ(); inc1++) {
                Debugger.log(inc, y, inc1);
                if (!world.getBlockAt(inc, y, inc1).isPassable()) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean solidAt(World world, double x, double y, double z) { // not perfect, still cuts corners of
                                                                               // fences
        org.bukkit.block.Block block = world.getBlockAt((int) x, (int) y, (int) z); // fast rounding.
        BoundingBox box = block.getBoundingBox();

        double minX = box.getMinX();
        double minY = box.getMinY();
        double minZ = box.getMinZ();

        double maxX = box.getMaxX();
        double maxY = box.getMaxY();
        double maxZ = box.getMaxZ();

        return x > minX && x < maxX && y > minY && y < maxY && z > minZ && z < maxZ;
    }

    // static boolean fullyPassable(BlockState state) {
    //     Block block = state.getBlock();
    //     if (block == Blocks.AIR) { // early return for most common case
    //       return true;
    //     }
    //     // exceptions - blocks that are isPassable true, but we can't actually jump through
    //     if (block == Blocks.FIRE
    //         || block == Blocks.TRIPWIRE
    //         || block == Blocks.COBWEB
    //         || block == Blocks.VINE
    //         || block == Blocks.LADDER
    //         || block == Blocks.COCOA
    //         || block instanceof DoorBlock
    //         || block instanceof FenceBlock
    //         || (block instanceof SnowLayerBlock)
    //         || block instanceof LiquidBlock
    //         || block instanceof TrapDoorBlock
    //         || block instanceof EndPortalBlock) {
    //       return false;
    //     }
    //     // door, fence gate, liquid, trapdoor have been accounted for, nothing else uses the world or pos parameters
    //     return block.isPassabe;
    //   }
}
