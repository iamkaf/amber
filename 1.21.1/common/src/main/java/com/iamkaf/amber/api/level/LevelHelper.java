package com.iamkaf.amber.api.level;

import net.minecraft.world.level.Level;

import java.util.function.Function;

public class LevelHelper {

    /**
     * Runs a specified function every X ticks within the game world.
     * This is designed to be used in tick() methods.
     *
     * @param level The game level or world instance.
     * @param ticks The number of ticks between each function execution.
     * @param run   The function to execute every X ticks, which takes the current game time as a parameter.
     */
    public static void runEveryXTicks(Level level, int ticks, Function<Long, Void> run) {
        // Ensure the level instance is valid.
        if (level == null) return;

        // Get the current game time from the level.
        long gameTime = level.getGameTime();

        // Check if the current game time is evenly divisible by the specified number of ticks.
        // If true, execute the provided function with the game time as an argument.
        if (gameTime % ticks == 0) {
            run.apply(gameTime);
        }
    }
}
