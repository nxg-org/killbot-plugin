package com.killbot.util;

import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.killbot.logic.Logic;

public class MathUtils {

    public static final Random RANDOM = new Random();
    public static final DecimalFormat FORMATTER_1 = new DecimalFormat("0.#");
    public static final DecimalFormat FORMATTER_2 = new DecimalFormat("0.##");

    public static float[] fetchYawPitch(Vector dir) {
        double x = dir.getX();
        double z = dir.getZ();

        float[] out = new float[2];

        if (x == 0.0D && z == 0.0D) {
            out[1] = (float) (dir.getY() > 0.0D ? -90 : 90);
        }

        else {
            double theta = Math.atan2(-x, z);
            out[0] = (float) Math.toDegrees((theta + 6.283185307179586D) % 6.283185307179586D);

            double x2 = NumberConversions.square(x);
            double z2 = NumberConversions.square(z);
            double xz = Math.sqrt(x2 + z2);
            out[1] = (float) Math.toDegrees(Math.atan(-dir.getY() / xz));
        }

        return out;
    }

    public static float fetchPitch(Vector dir) {
        double x = dir.getX();
        double z = dir.getZ();

        float result;

        if (x == 0.0D && z == 0.0D) {
            result = (float) (dir.getY() > 0.0D ? -90 : 90);
        }

        else {
            double x2 = NumberConversions.square(x);
            double z2 = NumberConversions.square(z);
            double xz = Math.sqrt(x2 + z2);
            result = (float) Math.toDegrees(Math.atan(-dir.getY() / xz));
        }

        return result;
    }

    public static Vector randomCircleOffset(double r) {
        double rad = 2 * Math.random() * Math.PI;

        double x = r * Math.random() * Math.cos(rad);
        double z = r * Math.random() * Math.sin(rad);

        return new Vector(x, 0, z);
    }

    public static double square(double n) {
        return n * n;
    }

    public static String round1Dec(double n) {
        return FORMATTER_1.format(n);
    }

    public static String round2Dec(double n) {
        return FORMATTER_2.format(n);
    }

    public static List<Map.Entry<Logic, Integer>> sortByValue(HashMap<Logic, Integer> hm) {
        List<Map.Entry<Logic, Integer>> list = new LinkedList<>(hm.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);
        return list;
    }

    public static double random(double low, double high) {
        return Math.random() * (high - low) + low;
    }

    public static double sum(List<Double> list) {
        return list.stream().mapToDouble(n -> n).sum();
    }

    public static double min(List<Double> list) {
        if (list.isEmpty()) {
            return 0;
        }

        double min = Double.MAX_VALUE;

        for (double n : list) {
            if (n < min) {
                min = n;
            }
        }

        return min;
    }

    public static double max(List<Double> list) {
        if (list.isEmpty()) {
            return 0;
        }

        double max = 0;

        for (double n : list) {
            if (n > max) {
                max = n;
            }
        }

        return max;
    }

    public static double getMidValue(List<Double> list) {
        return (min(list) + max(list)) / 2D;
    }

    public static <T> T getRandomSetElement(Set<T> set) {
        return set.isEmpty() ? null : set.stream().skip(RANDOM.nextInt(set.size())).findFirst().orElse(null);
    }



    public static double trueDistance(Entity org, org.bukkit.entity.Entity dest) {
        return MathUtils.trueDistanceRaw(org.getEyePosition(), ((CraftPlayer) dest).getHandle().getBoundingBox());
    }

    public static double trueDistance(Entity org, Entity dest) {
        return MathUtils.trueDistanceRaw(org.getEyePosition(), dest.getBoundingBox());
    }

    public static double trueDistanceRaw(final Vec3 point, final AABB aabb) {
        double[] XYZ = { point.x, point.y, point.z };
        double[] minXYZmaxXYZ = { aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ };
        double[] res = new double[3];
        for (int i = 0; i < 3; i++) {
            res[i] = Math.max(Math.max(minXYZmaxXYZ[i] - XYZ[i], 0), XYZ[i] - minXYZmaxXYZ[i + 3]);
        }
        return Math.sqrt(res[0] * res[0] + res[1] * res[1] + res[2] * res[2]);
    }

}
