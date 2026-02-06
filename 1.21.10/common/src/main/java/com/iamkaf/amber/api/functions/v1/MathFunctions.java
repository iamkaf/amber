package com.iamkaf.amber.api.functions.v1;

import java.util.Map;
import java.util.Random;

/**
 * Consolidated utility class for mathematical operations and probability calculations.
 * This class combines functionality from the Chance class with additional utilities for game development.
 *
 * @since 8.3.0
 */
public final class MathFunctions {

    private static final Random RANDOM = new Random();

    private MathFunctions() {
        // Utility class - prevent instantiation
    }

    // ==================== PROBABILITY OPERATIONS ====================

    /**
     * Simulates a random event occurring with a specified probability.
     * <p>
     * This method evaluates a given probability and returns {@code true} if the event
     * is determined to happen based on that probability, or {@code false} if it is not.
     * The probability is defined as a percentage from 0 (0%) to 1 (100%). If the input
     * is below 0 or above 1, it is clamped to fit within this range.
     * </p>
     *
     * @param percent A float value representing the probability (from 0 to 1) that the event will occur.
     *                Values outside this range are clamped to fit within it.
     * @return {@code true} if the event occurs based on the specified probability, {@code false} otherwise.
     */
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

    /**
     * Simulates a random event occurring with a specified probability.
     * <p>
     * This is an alias for {@link #chance(float)} to maintain compatibility with the original API.
     * </p>
     *
     * @param percent A float value representing the probability (from 0 to 1) that the event will occur.
     * @return {@code true} if the event occurs based on the specified probability, {@code false} otherwise.
     */
    public static boolean of(float percent) {
        return chance(percent);
    }

    /**
     * Clamps a probability value between 0 and 1, ensuring it falls within valid bounds.
     * <p>
     * The input is adjusted to remain between 0 and 1 inclusive. This helps to avoid
     * unexpected behavior due to invalid values being passed.
     * </p>
     *
     * @param percent The raw probability input.
     * @return A clamped probability value from 0 to 1.
     */
    public static float clampProbability(float percent) {
        return Math.clamp(percent, 0f, 1f);
    }

    // ==================== RANDOM NUMBER OPERATIONS ====================

    /**
     * Returns a random float between 0.0 (inclusive) and 1.0 (exclusive).
     *
     * @return A random float between 0.0 and 1.0.
     */
    public static float nextFloat() {
        return RANDOM.nextFloat();
    }

    /**
     * Returns a random integer between 0 (inclusive) and the specified bound (exclusive).
     *
     * @param bound The upper bound (exclusive).
     * @return A random integer between 0 (inclusive) and bound (exclusive).
     */
    public static int nextInt(int bound) {
        return RANDOM.nextInt(bound);
    }

    /**
     * Returns a random integer between the specified origin (inclusive) and bound (exclusive).
     *
     * @param origin The lower bound (inclusive).
     * @param bound The upper bound (exclusive).
     * @return A random integer between origin (inclusive) and bound (exclusive).
     */
    public static int nextInt(int origin, int bound) {
        return RANDOM.nextInt(bound - origin) + origin;
    }

    /**
     * Returns a random double between 0.0 (inclusive) and 1.0 (exclusive).
     *
     * @return A random double between 0.0 and 1.0.
     */
    public static double nextDouble() {
        return RANDOM.nextDouble();
    }

    /**
     * Returns a random double between the specified origin (inclusive) and bound (exclusive).
     *
     * @param origin The lower bound (inclusive).
     * @param bound The upper bound (exclusive).
     * @return A random double between origin (inclusive) and bound (exclusive).
     */
    public static double nextDouble(double origin, double bound) {
        return RANDOM.nextDouble() * (bound - origin) + origin;
    }

    /**
     * Returns a random boolean value.
     *
     * @return A random boolean value.
     */
    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }

    /**
     * Returns true with the specified probability (0.0 to 1.0), false otherwise.
     * This is a convenience method that uses the chance calculation internally.
     *
     * @param probability The probability of returning true (0.0 to 1.0).
     * @return True with the specified probability, false otherwise.
     */
    public static boolean nextBoolean(float probability) {
        return chance(probability);
    }

    // ==================== ENHANCED RANDOM RANGE OPERATIONS ====================

    /**
     * Returns a random integer between min (inclusive) and max (inclusive).
     * This is a convenience method that works with inclusive bounds, unlike nextInt(origin, bound) which uses exclusive upper bounds.
     *
     * @param min The minimum value (inclusive).
     * @param max The maximum value (inclusive).
     * @return A random integer between min and max.
     */
    public static int nextIntInclusive(int min, int max) {
        return RANDOM.nextInt(max - min + 1) + min;
    }

    /**
     * Returns a random float between min (inclusive) and max (inclusive).
     *
     * @param min The minimum value (inclusive).
     * @param max The maximum value (inclusive).
     * @return A random float between min and max.
     */
    public static float nextFloat(float min, float max) {
        return min + RANDOM.nextFloat() * (max - min);
    }

    /**
     * Returns a random number following a normal (Gaussian) distribution.
     *
     * @return A normally distributed random number with mean 0 and standard deviation 1.
     */
    public static double nextGaussian() {
        return RANDOM.nextGaussian();
    }

    /**
     * Returns a normally distributed number clamped to a range.
     *
     * @param mean The mean value.
     * @param stdDev The standard deviation.
     * @param min The minimum value to clamp to.
     * @param max The maximum value to clamp to.
     * @return A normally distributed random number within the specified range.
     */
    public static double nextGaussianClamped(double mean, double stdDev, double min, double max) {
        double value = mean + RANDOM.nextGaussian() * stdDev;
        return Math.max(min, Math.min(max, value));
    }

    // ==================== WEIGHTED CHOICE OPERATIONS ====================

    /**
     * Selects an element from the map with weighted probabilities.
     * Weights don't need to sum to 1 - they're automatically normalized.
     *
     * @param weightedChoices Map of elements to their weights. Must not be null or empty.
     * @return A randomly selected element based on weight probabilities.
     * @throws IllegalArgumentException if the weighted choices map is null or empty
     */
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

        // Fallback to last entry (shouldn't happen with valid weights)
        return weightedChoices.keySet().iterator().next();
    }

    // ==================== UTILITY FUNCTIONS ====================

    /**
     * Linear interpolation between two values.
     *
     * @param a The start value.
     * @param b The end value.
     * @param t The interpolation factor (0.0 = a, 1.0 = b).
     * @return The interpolated value.
     */
    public static double lerp(double a, double b, double t) {
        return a + (b - a) * Math.clamp(t, 0.0, 1.0);
    }

    /**
     * Clamps a value between min and max.
     *
     * @param value The value to clamp.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The clamped value.
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Maps a value from one range to another.
     *
     * @param value The value to map.
     * @param inMin The minimum of the input range.
     * @param inMax The maximum of the input range.
     * @param outMin The minimum of the output range.
     * @param outMax The maximum of the output range.
     * @return The mapped value.
     */
    public static double map(double value, double inMin, double inMax, double outMin, double outMax) {
        return (value - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }

    /**
     * Returns true with a 1 in N chance.
     *
     * @param n The denominator of the chance (1 in N). Must be positive.
     * @return True with probability 1/N, false otherwise.
     */
    public static boolean oneIn(int n) {
        return n > 0 && RANDOM.nextInt(n) == 0;
    }

    /**
     * Returns true with approximately N in M chance.
     *
     * @param n The numerator of the chance (N in M).
     * @param m The denominator of the chance (N in M). Must be positive.
     * @return True with probability N/M, false otherwise.
     */
    public static boolean chanceOf(int n, int m) {
        return m > 0 && n >= 0 && RANDOM.nextInt(m) < n;
    }

    // ==================== ANGLE & TRIGONOMETRY UTILITIES ====================

    /**
     * Converts degrees to radians.
     *
     * @param degrees The angle in degrees.
     * @return The angle in radians.
     */
    public static double toRadians(double degrees) {
        return Math.toRadians(degrees);
    }

    /**
     * Converts radians to degrees.
     *
     * @param radians The angle in radians.
     * @return The angle in degrees.
     */
    public static double toDegrees(double radians) {
        return Math.toDegrees(radians);
    }

    /**
     * Returns a random angle in radians.
     *
     * @return A random angle between 0 and 2π radians.
     */
    public static double randomAngle() {
        return RANDOM.nextDouble() * Math.PI * 2;
    }

    /**
     * Returns a random angle in degrees.
     *
     * @return A random angle between 0 and 360 degrees.
     */
    public static double randomAngleDegrees() {
        return RANDOM.nextDouble() * 360.0;
    }

    // ==================== CHOICE OPERATIONS ====================

    /**
     * Randomly selects one element from the given array.
     *
     * @param array The array to pick from. Must not be null or empty.
     * @return A randomly selected element from the array.
     * @throws IllegalArgumentException if the array is null or empty
     */
    @SafeVarargs
    public static <T> T pick(T... array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }
        return array[RANDOM.nextInt(array.length)];
    }

    /**
     * Randomly selects one element from the given list.
     *
     * @param list The list to pick from. Must not be null or empty.
     * @return A randomly selected element from the list.
     * @throws IllegalArgumentException if the list is null or empty
     */
    public static <T> T pick(java.util.List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List cannot be null or empty");
        }
        return list.get(RANDOM.nextInt(list.size()));
    }
}