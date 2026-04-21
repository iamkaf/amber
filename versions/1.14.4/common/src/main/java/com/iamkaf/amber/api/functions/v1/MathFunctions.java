package com.iamkaf.amber.api.functions.v1;

import net.minecraft.util.Mth;

import java.util.Map;
import java.util.Random;

/**
 * Pre-1.20.5 math helpers still need Mth.clamp rather than Math.clamp.
 */
public final class MathFunctions {

    private static final Random RANDOM = new Random();

    private MathFunctions() {
    }

    public static boolean chance(float percent) {
        float chance = clampProbability(percent);
        if (chance == 0f) {
            return false;
        }
        if (chance == 1f) {
            return true;
        }
        return RANDOM.nextFloat() < chance;
    }

    public static boolean of(float percent) {
        return chance(percent);
    }

    public static float clampProbability(float percent) {
        return Mth.clamp(percent, 0f, 1f);
    }

    public static float nextFloat() {
        return RANDOM.nextFloat();
    }

    public static int nextInt(int bound) {
        return RANDOM.nextInt(bound);
    }

    public static int nextInt(int origin, int bound) {
        return RANDOM.nextInt(bound - origin) + origin;
    }

    public static double nextDouble() {
        return RANDOM.nextDouble();
    }

    public static double nextDouble(double origin, double bound) {
        return RANDOM.nextDouble() * (bound - origin) + origin;
    }

    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }

    public static boolean nextBoolean(float probability) {
        return chance(probability);
    }

    public static int nextIntInclusive(int min, int max) {
        return RANDOM.nextInt(max - min + 1) + min;
    }

    public static float nextFloat(float min, float max) {
        return min + RANDOM.nextFloat() * (max - min);
    }

    public static double nextGaussian() {
        return RANDOM.nextGaussian();
    }

    public static double nextGaussianClamped(double mean, double stdDev, double min, double max) {
        double value = mean + RANDOM.nextGaussian() * stdDev;
        return Math.max(min, Math.min(max, value));
    }

    public static <T> T pickWeighted(Map<T, Double> weightedChoices) {
        if (weightedChoices == null || weightedChoices.isEmpty()) {
            throw new IllegalArgumentException("Weighted choices cannot be null or empty");
        }

        double totalWeight = weightedChoices.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalWeight <= 0) {
            throw new IllegalArgumentException("Total weight must be positive");
        }

        double random = RANDOM.nextDouble() * totalWeight;
        double currentWeight = 0;

        for (Map.Entry<T, Double> entry : weightedChoices.entrySet()) {
            currentWeight += entry.getValue();
            if (random <= currentWeight) {
                return entry.getKey();
            }
        }

        return weightedChoices.keySet().iterator().next();
    }

    public static double lerp(double a, double b, double t) {
        return a + (b - a) * Mth.clamp(t, 0.0, 1.0);
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double map(double value, double inMin, double inMax, double outMin, double outMax) {
        return outMin + (value - inMin) * (outMax - outMin) / (inMax - inMin);
    }

    public static double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }

    public static boolean approximately(double a, double b, double tolerance) {
        return Math.abs(a - b) <= tolerance;
    }

    public static boolean approximately(float a, float b, float tolerance) {
        return Math.abs(a - b) <= tolerance;
    }

    public static double toRadians(double degrees) {
        return degrees * Math.PI / 180.0;
    }

    public static double toDegrees(double radians) {
        return radians * 180.0 / Math.PI;
    }
}
