package com.iamkaf.amber.api.functions.v1;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
//? if >=1.18.2
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
//? if >=1.16
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
//? if >=1.16.2
import net.minecraft.world.level.ServerLevelAccessor;
//? if <1.16.2
/*import net.minecraft.world.level.LevelAccessor;*/
//? if <1.16
/*import net.minecraft.world.level.dimension.DimensionType;*/
import net.minecraft.world.level.biome.Biome;
//? if >=1.17
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.Difficulty;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Consolidated utility class for world, level, sound, and geometry operations.
 * This class combines functionality from LevelHelper, SoundHelper, CommonUtils, and BoundingBoxMerger.
 *
 * @since 8.3.0
 */
public final class WorldFunctions {

    private WorldFunctions() {
        // Utility class - prevent instantiation
    }

    // ==================== LEVEL OPERATIONS ====================

    /**
     * Runs a specified function every X ticks within the game world.
     * This is designed to be used in tick() methods.
     *
     * @param level The game level or world instance.
     * @param ticks The number of ticks between each function execution.
     * @param run   The function to execute every X ticks, which takes the current game time as a parameter.
     */
    public static void runEveryXTicks(Level level, int ticks, Consumer<Long> run) {
        // Ensure the level instance is valid.
        if (level == null) return;

        // Get the current game time from the level.
        long gameTime = gameTime(level);

        // Check if the current game time is evenly divisible by the specified number of ticks.
        // If true, execute the provided function with the game time as an argument.
        if (gameTime % ticks == 0) {
            run.accept(gameTime);
        }
    }

    // ==================== ITEM SPAWNING OPERATIONS ====================

    /**
     * Spawns an item entity in the world at a specified position.
     * This method creates an ItemEntity using the provided ItemStack and places it
     * at the given position with slight upward motion to simulate a natural drop.
     *
     * @param level The game level or world instance where the item will be dropped.
     * @param stack The ItemStack to spawn as an item entity in the world.
     * @param pos   The position where the item will be dropped.
     */
    public static void dropItem(Level level, ItemStack stack, Vec3 pos) {
        if (level == null) return;
        dropItem(level, stack, pos, new Vec3(0d, 0.2d, 0d));
    }

    /**
     * Spawns an item entity in the world at a specified position.
     * This method creates an ItemEntity using the provided ItemLike and places it
     * at the given position with slight upward motion to simulate a natural drop.
     *
     * @param level The game level or world instance where the item will be dropped.
     * @param stack The ItemLike to spawn as an item entity in the world.
     * @param pos   The position where the item will be dropped.
     */
    public static void dropItem(Level level, ItemLike stack, Vec3 pos) {
        if (level == null) return;
        dropItem(level, new ItemStack(stack), pos, new Vec3(0d, 0.2d, 0d));
    }

    /**
     * Spawns an item entity in the world at a specified position with custom velocity.
     * This method creates an ItemEntity using the provided ItemStack and places it
     * at the given position with the provided motion.
     *
     * @param level The game level or world instance where the item will be dropped.
     * @param stack The ItemStack to spawn as an item entity in the world.
     * @param pos   The position where the item will be dropped.
     * @param delta The initial velocity for the spawned item.
     */
    public static void dropItem(Level level, ItemStack stack, Vec3 pos, Vec3 delta) {
        if (level == null) return;
        //? if >=1.17
        var itemEntity = new ItemEntity(level, vecX(pos), vecY(pos), vecZ(pos), stack, vecX(delta), vecY(delta), vecZ(delta));
        //? if <1.17 {
        /*var itemEntity = new ItemEntity(level, pos.x(), pos.y(), pos.z(), stack);
        itemEntity.setDeltaMovement(delta);*/
        //?}
        addFreshEntity(level, itemEntity);
    }

    /**
     * Spawns an item entity in the world at a specified position with custom velocity.
     * This method creates an ItemEntity using the provided ItemLike and places it
     * at the given position with the provided motion.
     *
     * @param level The game level or world instance where the item will be dropped.
     * @param stack The ItemLike to spawn as an item entity in the world.
     * @param pos   The position where the item will be dropped.
     * @param delta The initial velocity for the spawned item.
     */
    public static void dropItem(Level level, ItemLike stack, Vec3 pos, Vec3 delta) {
        if (level == null) return;
        dropItem(level, new ItemStack(stack), pos, delta);
    }

    // ==================== RAYTRACING OPERATIONS ====================

    /**
     * Performs a raytrace to determine the block the player is looking at.
     *
     * @param level  The level in which the player is located.
     * @param player The player performing the raytrace.
     * @return The result of the raytrace.
     */
    public static @NotNull BlockHitResult raytrace(Level level, Player player) {
        //? if >=1.17
        Vec3 eyePosition = playerEyePosition(player);
        //? if <1.17
        /*Vec3 eyePosition = player.getEyePosition(1.0F);*/
        Vec3 rotation = playerViewVector(player, 1.0f);
        //? if >=1.20.5
        double reach = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        //? if <1.20.5
        /*double reach = 4.5;*/
        Vec3 combined = new Vec3(
                vecX(eyePosition) + vecX(rotation) * reach,
                vecY(eyePosition) + vecY(rotation) * reach,
                vecZ(eyePosition) + vecZ(rotation) * reach
        );
        return clip(level, new ClipContext(eyePosition, combined, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }

    // ==================== LEVEL-BASED SOUND OPERATIONS ====================

    /**
     * Plays a sound at the specified position in the level with default settings.
     *
     * @param level The level where the sound should be played.
     * @param position The position to play the sound at.
     * @param sound The sound event to play.
     */
    public static void playSoundAt(Level level, Vec3 position,
            //? if >=1.18.2
            Holder<SoundEvent> sound
            //? if <1.18.2
            /*SoundEvent sound*/
    ) {
        playLevelSound(level, null, vecX(position), vecY(position), vecZ(position), soundEvent(sound), SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    /**
     * Plays a sound at the specified position in the level with a specified sound source.
     *
     * @param level The level where the sound should be played.
     * @param position The position to play the sound at.
     * @param sound The sound event to play.
     * @param source The sound source category.
     */
    public static void playSoundAt(Level level, Vec3 position,
            //? if >=1.18.2
            Holder<SoundEvent> sound,
            //? if <1.18.2
            /*SoundEvent sound,*/
            SoundSource source) {
        playLevelSound(level, null, vecX(position), vecY(position), vecZ(position), soundEvent(sound), source, 1.0f, 1.0f);
    }

    /**
     * Plays a sound at the specified position in the level with specified volume.
     *
     * @param level The level where the sound should be played.
     * @param position The position to play the sound at.
     * @param sound The sound event to play.
     * @param source The sound source category.
     * @param volume The volume level (1.0 = normal volume).
     */
    public static void playSoundAt(Level level, Vec3 position,
            //? if >=1.18.2
            Holder<SoundEvent> sound,
            //? if <1.18.2
            /*SoundEvent sound,*/
            SoundSource source, float volume) {
        playLevelSound(level, null, vecX(position), vecY(position), vecZ(position), soundEvent(sound), source, volume, 1.0f);
    }

    /**
     * Plays a sound at the specified position in the level with full customization options.
     *
     * @param level The level where the sound should be played.
     * @param position The position to play the sound at.
     * @param sound The sound event to play.
     * @param source The sound source category.
     * @param volume The volume level (1.0 = normal volume).
     * @param pitch The pitch multiplier (1.0 = normal pitch).
     */
    public static void playSoundAt(Level level, Vec3 position,
            //? if >=1.18.2
            Holder<SoundEvent> sound,
            //? if <1.18.2
            /*SoundEvent sound,*/
            SoundSource source, float volume, float pitch) {
        playLevelSound(level, null, vecX(position), vecY(position), vecZ(position), soundEvent(sound), source, volume, pitch);
    }

    /**
     * Plays a sound at the specified position in the level using a BlockPos.
     *
     * @param level The level where the sound should be played.
     * @param position The position to play the sound at.
     * @param sound The sound event to play.
     * @param source The sound source category.
     */
    public static void playSoundAt(Level level, BlockPos position,
            //? if >=1.18.2
            Holder<SoundEvent> sound,
            //? if <1.18.2
            /*SoundEvent sound,*/
            SoundSource source) {
        playLevelSound(level, null, blockX(position) + 0.5, blockY(position) + 0.5, blockZ(position) + 0.5, soundEvent(sound), source, 1.0f, 1.0f);
    }

    /**
     * Plays a sound at the specified position in the level using a BlockPos with full customization options.
     *
     * @param level The level where the sound should be played.
     * @param position The position to play the sound at.
     * @param sound The sound event to play.
     * @param source The sound source category.
     * @param volume The volume level (1.0 = normal volume).
     * @param pitch The pitch multiplier (1.0 = normal pitch).
     */
    public static void playSoundAt(Level level, BlockPos position,
            //? if >=1.18.2
            Holder<SoundEvent> sound,
            //? if <1.18.2
            /*SoundEvent sound,*/
            SoundSource source, float volume, float pitch) {
        playLevelSound(level, null, blockX(position) + 0.5, blockY(position) + 0.5, blockZ(position) + 0.5, soundEvent(sound), source, volume, pitch);
    }

    private static SoundEvent soundEvent(
            //? if >=1.18.2
            Holder<SoundEvent> sound
            //? if <1.18.2
            /*SoundEvent sound*/
    ) {
        //? if >=1.18.2
        return holderValue(sound);
        //? if <1.18.2
        /*return sound;*/
    }

    // ==================== DIMENSION UTILITIES ====================

    /**
     * Checks if the level is the Overworld dimension.
     *
     * @param level The level to check.
     * @return true if the level is the Overworld, false otherwise.
     */
    public static boolean isOverworld(Level level) {
        //? if >=1.16
        return dimensionPath(level).equals("overworld");
        //? if <1.16
        /*return level.dimension.getType() == DimensionType.OVERWORLD;*/
    }

    /**
     * Checks if the level is the Nether dimension.
     *
     * @param level The level to check.
     * @return true if the level is the Nether, false otherwise.
     */
    public static boolean isNether(Level level) {
        //? if >=1.16
        return dimensionPath(level).equals("the_nether");
        //? if <1.16
        /*return level.dimension.getType() == DimensionType.NETHER;*/
    }

    /**
     * Checks if the level is the End dimension.
     *
     * @param level The level to check.
     * @return true if the level is the End, false otherwise.
     */
    public static boolean isEnd(Level level) {
        //? if >=1.16
        return dimensionPath(level).equals("the_end");
        //? if <1.16
        /*return level.dimension.getType() == DimensionType.THE_END;*/
    }

    // ==================== DISTANCE UTILITIES ====================

    /**
     * Calculates the distance between two positions.
     *
     * @param pos1 The first position.
     * @param pos2 The second position.
     * @return The Euclidean distance between the positions.
     */
    public static double distanceBetween(Vec3 pos1, Vec3 pos2) {
        return Math.sqrt(distanceSquaredBetween(pos1, pos2));
    }

    /**
     * Calculates the distance between two block positions.
     *
     * @param pos1 The first block position.
     * @param pos2 The second block position.
     * @return The Euclidean distance between the block positions.
     */
    public static double distanceBetween(BlockPos pos1, BlockPos pos2) {
        return Math.sqrt(distanceSquaredBetween(pos1, pos2));
    }

    /**
     * Calculates the distance between two entities.
     *
     * @param entity1 The first entity.
     * @param entity2 The second entity.
     * @return The Euclidean distance between the entities.
     */
    public static double distanceBetween(Entity entity1, Entity entity2) {
        return entity1.position().distanceTo(entity2.position());
    }

    /**
     * Calculates the squared distance between two positions (more efficient than distance).
     *
     * @param pos1 The first position.
     * @param pos2 The second position.
     * @return The squared distance between the positions.
     */
    public static double distanceSquaredBetween(Vec3 pos1, Vec3 pos2) {
        double dx = vecX(pos1) - vecX(pos2);
        double dy = vecY(pos1) - vecY(pos2);
        double dz = vecZ(pos1) - vecZ(pos2);
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Calculates the squared distance between two block positions.
     *
     * @param pos1 The first block position.
     * @param pos2 The second block position.
     * @return The squared distance between the block positions.
     */
    public static double distanceSquaredBetween(BlockPos pos1, BlockPos pos2) {
        double dx = blockX(pos1) - blockX(pos2);
        double dy = blockY(pos1) - blockY(pos2);
        double dz = blockZ(pos1) - blockZ(pos2);
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Calculates the horizontal distance between two positions (ignoring Y coordinate).
     *
     * @param pos1 The first position.
     * @param pos2 The second position.
     * @return The horizontal distance between the positions.
     */
    public static double horizontalDistanceBetween(Vec3 pos1, Vec3 pos2) {
        double dx = vecX(pos1) - vecX(pos2);
        double dz = vecZ(pos1) - vecZ(pos2);
        return Math.sqrt(dx * dx + dz * dz);
    }

    // ==================== TIME UTILITIES ====================

    /**
     * Gets the current time of day in ticks (0-23999).
     *
     * @param level The level to get the time from.
     * @return The current time of day in ticks.
     */
    public static long getTimeOfDay(Level level) {
        //? if >=26.1
        return dayTime(level) % 24000L;
        //? if <26.1
        /*return dayTime(level) % 24000L;*/
    }

    /**
     * Gets the current time of day in hours (0.0-23.99).
     *
     * @param level The level to get the time from.
     * @return The current time of day in hours.
     */
    public static double getTimeOfDayInHours(Level level) {
        return (getTimeOfDay(level) / 1000.0);
    }

    /**
     * Checks if it is currently daytime in the level.
     *
     * @param level The level to check.
     * @return true if it is daytime, false otherwise.
     */
    public static boolean isDaytime(Level level) {
        long time = getTimeOfDay(level);
        return time < 12000L || time > 23850L; // Day is 0-12000, plus brief dawn/dusk
    }

    /**
     * Checks if it is currently nighttime in the level.
     *
     * @param level The level to check.
     * @return true if it is nighttime, false otherwise.
     */
    public static boolean isNighttime(Level level) {
        return !isDaytime(level);
    }

    /**
     * Gets the current moon phase (0-7).
     *
     * @param level The level to get the moon phase from.
     * @return The current moon phase (0=full moon, 4=new moon).
     */
    public static int getMoonPhase(Level level) {
        //? if >=26.1
        return (int) ((dayTime(level) / 24000L) % 8L);
        //? if <26.1
        /*return (int) ((dayTime(level) / 24000L) % 8L);*/
    }

    /**
     * Gets the total number of days that have passed in the level.
     *
     * @param level The level to get the day count from.
     * @return The total number of days passed.
     */
    public static long getDayCount(Level level) {
        //? if >=26.1
        return dayTime(level) / 24000L;
        //? if <26.1
        /*return dayTime(level) / 24000L;*/
    }

    /**
     * Gets the total game time in ticks that have passed.
     *
     * @param level The level to get the game time from.
     * @return The total game time in ticks.
     */
    public static long getTotalGameTime(Level level) {
        return gameTime(level);
    }

    // ==================== DIFFICULTY UTILITIES ====================

    /**
     * Gets the current difficulty at a specific position.
     *
     * @param level The level to check. Must be a ServerLevelAccessor.
     * @param position The position to check difficulty at.
     * @return The difficulty instance at the position.
     */
    public static DifficultyInstance getCurrentDifficulty(
            //? if >=1.16.2
            ServerLevelAccessor level,
            //? if <1.16.2
            /*LevelAccessor level,*/
            BlockPos position) {
        return currentDifficulty(level, position);
    }

    /**
     * Gets the current difficulty at a specific position using Vec3.
     *
     * @param level The level to check. Must be a ServerLevelAccessor.
     * @param position The position to check difficulty at.
     * @return The difficulty instance at the position.
     */
    public static DifficultyInstance getCurrentDifficulty(
            //? if >=1.16.2
            ServerLevelAccessor level,
            //? if <1.16.2
            /*LevelAccessor level,*/
            Vec3 position) {
        //? if >=1.19.4
        return currentDifficulty(level, BlockPos.containing(position));
        //? if <1.19.4
        /*return currentDifficulty(level, new BlockPos(position));*/
    }

    /**
     * Checks if the difficulty at a position is at least hard.
     *
     * @param level The level to check. Must be a ServerLevelAccessor.
     * @param position The position to check difficulty at.
     * @return true if difficulty is hard or harder, false otherwise.
     */
    public static boolean isHardDifficulty(
            //? if >=1.16.2
            ServerLevelAccessor level,
            //? if <1.16.2
            /*LevelAccessor level,*/
            BlockPos position) {
        //? if >=1.17
        return isDifficultyHard(getCurrentDifficulty(level, position));
        //? if <1.17
        /*return getCurrentDifficulty(level, position).getDifficulty() == Difficulty.HARD;*/
    }

    /**
     * Checks if the difficulty at a position is at least hard using Vec3.
     *
     * @param level The level to check. Must be a ServerLevelAccessor.
     * @param position The position to check difficulty at.
     * @return true if difficulty is hard or harder, false otherwise.
     */
    public static boolean isHardDifficulty(
            //? if >=1.16.2
            ServerLevelAccessor level,
            //? if <1.16.2
            /*LevelAccessor level,*/
            Vec3 position) {
        //? if >=1.17
        return isDifficultyHard(getCurrentDifficulty(level, position));
        //? if <1.17
        /*return getCurrentDifficulty(level, position).getDifficulty() == Difficulty.HARD;*/
    }

    // ==================== ENTITY UTILITIES ====================

    /**
     * Gets all players currently in the level.
     *
     * @param level The level to get players from.
     * @return A list of all players in the level.
     */
    public static List<Player> getPlayers(Level level) {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            return players(serverLevel);
        //? if >=1.15
        } else if (level instanceof net.minecraft.client.multiplayer.ClientLevel clientLevel) {
        //? if <1.15
        /*} else if (level instanceof net.minecraft.client.multiplayer.MultiPlayerLevel clientLevel) {*/
            return players(clientLevel);
        }
        return new ArrayList<>();
    }

    /**
     * Gets all entities within a specified radius of a position.
     *
     * @param level The level to search in.
     * @param center The center position to search around.
     * @param radius The search radius.
     * @return A list of entities within the radius.
     */
    public static List<Entity> getEntitiesInRadius(Level level, Vec3 center, double radius) {
        AABB boundingBox = new AABB(
                vecX(center) - radius, vecY(center) - radius, vecZ(center) - radius,
                vecX(center) + radius, vecY(center) + radius, vecZ(center) + radius
        );
        return entities(level, null, boundingBox, entity -> true);
    }

    /**
     * Gets all entities of a specific type within a specified radius.
     *
     * @param level The level to search in.
     * @param center The center position to search around.
     * @param radius The search radius.
     * @param entityType The type of entity to search for.
     * @param <T> The entity type.
     * @return A list of entities of the specified type within the radius.
     */
    public static <T extends Entity> List<T> getEntitiesInRadius(Level level, Vec3 center, double radius, EntityType<T> entityType) {
        AABB boundingBox = new AABB(
                vecX(center) - radius, vecY(center) - radius, vecZ(center) - radius,
                vecX(center) + radius, vecY(center) + radius, vecZ(center) + radius
        );
        //? if >=1.15
        return typedEntities(level, entityType, boundingBox, entity -> true);
        //? if <1.15
        /*return (List<T>) (List<?>) level.getEntities(entityType, boundingBox, entity -> true);*/
    }

    /**
     * Gets all entities matching a predicate within a specified radius.
     *
     * @param level The level to search in.
     * @param center The center position to search around.
     * @param radius The search radius.
     * @param predicate The predicate to filter entities.
     * @return A list of entities matching the predicate within the radius.
     */
    public static List<Entity> getEntitiesInRadius(Level level, Vec3 center, double radius, java.util.function.Predicate<Entity> predicate) {
        AABB boundingBox = new AABB(
                vecX(center) - radius, vecY(center) - radius, vecZ(center) - radius,
                vecX(center) + radius, vecY(center) + radius, vecZ(center) + radius
        );
        return entities(level, null, boundingBox, predicate);
    }

    /**
     * Gets all entities within a specified radius of a block position.
     *
     * @param level The level to search in.
     * @param center The center block position to search around.
     * @param radius The search radius.
     * @return A list of entities within the radius.
     */
    public static List<Entity> getEntitiesInRadius(Level level, BlockPos center, double radius) {
        //? if >=1.16
        return getEntitiesInRadius(level, blockCenter(center), radius);
        //? if <1.16
        /*return getEntitiesInRadius(level, blockCenter(center), radius);*/
    }

    /**
     * Gets all entities of a specific type within a specified radius of a block position.
     *
     * @param level The level to search in.
     * @param center The center block position to search around.
     * @param radius The search radius.
     * @param entityType The type of entity to search for.
     * @param <T> The entity type.
     * @return A list of entities of the specified type within the radius.
     */
    public static <T extends Entity> List<T> getEntitiesInRadius(Level level, BlockPos center, double radius, EntityType<T> entityType) {
        //? if >=1.16
        return getEntitiesInRadius(level, blockCenter(center), radius, entityType);
        //? if <1.16
        /*return getEntitiesInRadius(level, blockCenter(center), radius, entityType);*/
    }

    /**
     * Gets the nearest entity to a position within a specified radius.
     *
     * @param level The level to search in.
     * @param center The center position to search around.
     * @param radius The search radius.
     * @return The nearest entity, or null if none found.
     */
    @Nullable
    public static Entity getNearestEntity(Level level, Vec3 center, double radius) {
        List<Entity> entities = getEntitiesInRadius(level, center, radius);
        return entities.stream()
                .min(Comparator.comparingDouble(entity -> distanceSquaredBetween(entityPosition(entity), center)))
                .orElse(null);
    }

    // ==================== BIOME UTILITIES ====================

    /**
     * Gets the biome at a specific position.
     *
     * @param level The level to check.
     * @param position The position to get the biome at.
     * @return The biome at the position.
     */
    //? if >=1.18.2
    public static Holder<Biome> getBiomeAtPosition(Level level, BlockPos position) {
    //? if <1.18.2
    /*public static Biome getBiomeAtPosition(Level level, BlockPos position) {*/
        return biome(level, position);
    }

    /**
     * Gets the biome at a specific position using Vec3.
     *
     * @param level The level to check.
     * @param position The position to get the biome at.
     * @return The biome at the position.
     */
    //? if >=1.18.2
    public static Holder<Biome> getBiomeAtPosition(Level level, Vec3 position) {
    //? if <1.18.2
    /*public static Biome getBiomeAtPosition(Level level, Vec3 position) {*/
        //? if >=1.19.4
        return level.getBiome(BlockPos.containing(position));
        //? if <1.19.4 && >=1.18.2
        /*return biome(level, new BlockPos(position));*/
        //? if <1.18.2
        /*return level.getBiome(new BlockPos(position));*/
    }

    /**
     * Gets the biome value at a specific position.
     *
     * @param level The level to check.
     * @param position The position to get the biome at.
     * @return The biome value at the position.
     */
    public static Biome getBiomeValueAtPosition(Level level, BlockPos position) {
        //? if >=1.18.2
        return holderValue(biome(level, position));
        //? if <1.18.2
        /*return level.getBiome(position);*/
    }

    /**
     * Gets the biome value at a specific position using Vec3.
     *
     * @param level The level to check.
     * @param position The position to get the biome at.
     * @return The biome value at the position.
     */
    public static Biome getBiomeValueAtPosition(Level level, Vec3 position) {
        //? if >=1.19.4
        return level.getBiome(BlockPos.containing(position)).value();
        //? if <1.19.4 && >=1.18.2
        /*return holderValue(biome(level, new BlockPos(position)));*/
        //? if <1.18.2
        /*return level.getBiome(new BlockPos(position));*/
    }

    /**
     * Checks if the position is in a specific precipitation type.
     *
     * @param level The level to check.
     * @param position The position to check.
     * @param precipitation The precipitation type to check for.
     * @return true if the position has the specified precipitation, false otherwise.
     */
    public static boolean hasPrecipitation(Level level, BlockPos position, Biome.Precipitation precipitation) {
        //? if >=1.21.2
        return holderValue(biome(level, position)).getPrecipitationAt(position, seaLevel(level)) == precipitation;
        //? if <1.21.2 && >=1.19.4
        /*return level.getBiome(position).value().getPrecipitationAt(position) == precipitation;*/
        //? if <1.19.4 && >=1.18.2
        /*return precipitation(holderValue(biome(level, position))) == precipitation;*/
        //? if <1.18.2
        /*return level.getBiome(position).getPrecipitation() == precipitation;*/
    }

    /**
     * Checks if the position is in a specific precipitation type using Vec3.
     *
     * @param level The level to check.
     * @param position The position to check.
     * @param precipitation The precipitation type to check for.
     * @return true if the position has the specified precipitation, false otherwise.
     */
    public static boolean hasPrecipitation(Level level, Vec3 position, Biome.Precipitation precipitation) {
        //? if >=1.19.4
        return hasPrecipitation(level, BlockPos.containing(position), precipitation);
        //? if <1.19.4
        /*return hasPrecipitation(level, new BlockPos(position), precipitation);*/
    }

    // ==================== BOUNDING BOX OPERATIONS ====================

    /**
     * Merges the provided block positions into a collection of bounding boxes
     * by attempting to combine adjacent positions into larger axis-aligned bounding boxes (AABBs).
     *
     * @param positions      The block positions to merge.
     * @param referencePoint The reference point for normalization.
     * @return A collection of merged bounding boxes.
     */
    public static Collection<AABB> mergeBoundingBoxes(Collection<BlockPos> positions, BlockPos referencePoint) {
        BoundingBoxMerger boxMerger = new BoundingBoxMerger();

        positions.stream()
                .map(pos -> relativeBlockPos(pos, referencePoint))
                .sorted()
                .map(AABB::new)
                .forEachOrdered(box -> {
                    // Reset current bounds if we encounter a new x or y coordinate
                    if (boxMerger.xCoordTracker != aabbMinX(box) || boxMerger.yCoordTracker != aabbMinY(box)) {
                        boxMerger.currentBounds = null;
                    }

                    boxMerger.xCoordTracker = aabbMinX(box);
                    boxMerger.yCoordTracker = aabbMinY(box);

                    Vec3 center = aabbCenter(box);
                    boxMerger.currentCenter = center;

                    // Attempt to combine with the current bounds or adjacent boxes
                    if (boxMerger.currentBounds != null && boxMerger.canCombine(
                            boxMerger.currentBounds,
                            box,
                            center
                    )) {
                        return;
                    }

                    if (boxMerger.tryCombineAdjacent(center, box)) {
                        return;
                    }

                    // Store as a new bounding box
                    boxMerger.currentBounds = box;
                    boxMerger.positionToBox.put(center, box);
                    boxMerger.boxToPosition.put(box, center);
                });

        return boxMerger.boxToPosition.keySet();
    }

    private static long gameTime(Level level) {
        try {
            Object value = level.getClass().getMethod("getGameTime").invoke(level);
            return value instanceof Long time ? time : 0L;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve level game time", exception);
        }
    }

    private static double vecX(Vec3 vector) {
        try {
            return ((Number) vector.getClass().getField("x").get(vector)).doubleValue();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve vector x", exception);
        }
    }

    private static int blockX(BlockPos position) {
        try {
            Object value = position.getClass().getMethod("getX").invoke(position);
            return value instanceof Integer coordinate ? coordinate : 0;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve block x", exception);
        }
    }

    private static int blockY(BlockPos position) {
        try {
            Object value = position.getClass().getMethod("getY").invoke(position);
            return value instanceof Integer coordinate ? coordinate : 0;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve block y", exception);
        }
    }

    private static int blockZ(BlockPos position) {
        try {
            Object value = position.getClass().getMethod("getZ").invoke(position);
            return value instanceof Integer coordinate ? coordinate : 0;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve block z", exception);
        }
    }

    private static double vecY(Vec3 vector) {
        try {
            return ((Number) vector.getClass().getField("y").get(vector)).doubleValue();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve vector y", exception);
        }
    }

    private static double vecZ(Vec3 vector) {
        try {
            return ((Number) vector.getClass().getField("z").get(vector)).doubleValue();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve vector z", exception);
        }
    }

    private static Vec3 blockCenter(BlockPos position) {
        return new Vec3(blockX(position) + 0.5D, blockY(position) + 0.5D, blockZ(position) + 0.5D);
    }

    private static BlockPos relativeBlockPos(BlockPos position, BlockPos referencePoint) {
        return new BlockPos(
                blockX(position) - blockX(referencePoint),
                blockY(position) - blockY(referencePoint),
                blockZ(position) - blockZ(referencePoint)
        );
    }

    private static Vec3 entityPosition(Entity entity) {
        try {
            return (Vec3) entity.getClass().getMethod("position").invoke(entity);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve entity position", exception);
        }
    }

    private static long dayTime(Level level) {
        try {
            Object value = level.getClass().getMethod("getDayTime").invoke(level);
            return value instanceof Long time ? time : 0L;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve level day time", exception);
        }
    }

    private static void addFreshEntity(Level level, Entity entity) {
        try {
            level.getClass().getMethod("addFreshEntity", Entity.class).invoke(level, entity);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to add entity to level", exception);
        }
    }

    private static Vec3 playerEyePosition(Player player) {
        try {
            return (Vec3) player.getClass().getMethod("getEyePosition").invoke(player);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve player eye position", exception);
        }
    }

    private static Vec3 playerViewVector(Player player, float partialTick) {
        try {
            return (Vec3) player.getClass().getMethod("getViewVector", float.class).invoke(player, partialTick);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve player view vector", exception);
        }
    }

    private static BlockHitResult clip(Level level, ClipContext context) {
        try {
            return (BlockHitResult) level.getClass().getMethod("clip", ClipContext.class).invoke(level, context);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to raytrace level", exception);
        }
    }

    private static void playLevelSound(Level level, Player player, double x, double y, double z, SoundEvent sound,
                                       SoundSource source, float volume, float pitch) {
        try {
            level.getClass()
                    .getMethod("playSound", Player.class, double.class, double.class, double.class, SoundEvent.class, SoundSource.class, float.class, float.class)
                    .invoke(level, player, x, y, z, sound, source, volume, pitch);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to play level sound", exception);
        }
    }

    private static DifficultyInstance currentDifficulty(Object level, BlockPos position) {
        try {
            return (DifficultyInstance) level.getClass().getMethod("getCurrentDifficultyAt", BlockPos.class).invoke(level, position);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve current difficulty", exception);
        }
    }

    private static boolean isDifficultyHard(DifficultyInstance difficulty) {
        try {
            Object value = difficulty.getClass().getMethod("isHard").invoke(difficulty);
            return value instanceof Boolean hard && hard;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve difficulty hardness", exception);
        }
    }

    private static Biome.Precipitation precipitation(Biome biome) {
        try {
            return (Biome.Precipitation) biome.getClass().getMethod("getPrecipitation").invoke(biome);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve biome precipitation", exception);
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Player> players(Object level) {
        try {
            return new ArrayList<>((List<Player>) level.getClass().getMethod("players").invoke(level));
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve level players", exception);
        }
    }

    //? if >=1.18.2 {
    private static <T> T holderValue(Holder<T> holder) {
        try {
            @SuppressWarnings("unchecked")
            T value = (T) holder.getClass().getMethod("value").invoke(holder);
            return value;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve holder value", exception);
        }
    }
    //?}

    private static String dimensionPath(Level level) {
        try {
            ResourceKey<?> key = (ResourceKey<?>) level.getClass().getMethod("dimension").invoke(level);
            ResourceLocation location = (ResourceLocation) key.getClass().getMethod("location").invoke(key);
            return resourcePath(location);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve level dimension", exception);
        }
    }

    private static int seaLevel(Level level) {
        try {
            Object value = level.getClass().getMethod("getSeaLevel").invoke(level);
            return value instanceof Integer number ? number : 0;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve level sea level", exception);
        }
    }

    private static String resourcePath(ResourceLocation location) {
        try {
            Object value = location.getClass().getMethod("getPath").invoke(location);
            return value instanceof String path ? path : "";
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve resource path", exception);
        }
    }

    private static List<Entity> entities(Level level, Entity except, AABB bounds, java.util.function.Predicate<Entity> predicate) {
        try {
            @SuppressWarnings("unchecked")
            List<Entity> result = (List<Entity>) level.getClass().getMethod("getEntities", Entity.class, AABB.class, java.util.function.Predicate.class)
                    .invoke(level, except, bounds, predicate);
            return result;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve level entities", exception);
        }
    }

    private static <T extends Entity> List<T> typedEntities(Level level, EntityType<T> type, AABB bounds, java.util.function.Predicate<Entity> predicate) {
        try {
            @SuppressWarnings("unchecked")
            //? if >=1.17 {
            List<T> result = (List<T>) level.getClass().getMethod("getEntities", EntityTypeTest.class, AABB.class, java.util.function.Predicate.class)
                    .invoke(level, type, bounds, predicate);
            //?} else {
            /*List<T> result = (List<T>) level.getClass().getMethod("getEntities", EntityType.class, AABB.class, java.util.function.Predicate.class)
                    .invoke(level, type, bounds, predicate);
            *///?}
            return result;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve typed level entities", exception);
        }
    }

    //? if >=1.18.2
    private static Holder<Biome> biome(Level level, BlockPos position) {
    //? if <1.18.2
    /*private static Biome biome(Level level, BlockPos position) {*/
        try {
            //? if >=1.18.2 {
            @SuppressWarnings("unchecked")
            Holder<Biome> result = (Holder<Biome>) level.getClass().getMethod("getBiome", BlockPos.class).invoke(level, position);
            return result;
            //?} else {
            /*return (Biome) level.getClass().getMethod("getBiome", BlockPos.class).invoke(level, position);
            *///?}
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve biome at " + position, exception);
        }
    }

    private static Vec3 aabbCenter(AABB box) {
        return new Vec3(
                (aabbMinX(box) + aabbMaxX(box)) / 2.0D,
                (aabbMinY(box) + aabbMaxY(box)) / 2.0D,
                (aabbMinZ(box) + aabbMaxZ(box)) / 2.0D
        );
    }

    private static Vec3 subtractVectors(Vec3 first, Vec3 second) {
        return new Vec3(vecX(first) - vecX(second), vecY(first) - vecY(second), vecZ(first) - vecZ(second));
    }

    private static Vec3 addVectors(Vec3 first, Vec3 second) {
        return new Vec3(vecX(first) + vecX(second), vecY(first) + vecY(second), vecZ(first) + vecZ(second));
    }

    private static AABB mergeAabbs(AABB first, AABB second) {
        return new AABB(
                Math.min(aabbMinX(first), aabbMinX(second)),
                Math.min(aabbMinY(first), aabbMinY(second)),
                Math.min(aabbMinZ(first), aabbMinZ(second)),
                Math.max(aabbMaxX(first), aabbMaxX(second)),
                Math.max(aabbMaxY(first), aabbMaxY(second)),
                Math.max(aabbMaxZ(first), aabbMaxZ(second))
        );
    }

    private static double aabbMinX(AABB box) {
        return aabbCoordinate(box, "minX");
    }

    private static double aabbMinY(AABB box) {
        return aabbCoordinate(box, "minY");
    }

    private static double aabbMinZ(AABB box) {
        return aabbCoordinate(box, "minZ");
    }

    private static double aabbMaxX(AABB box) {
        return aabbCoordinate(box, "maxX");
    }

    private static double aabbMaxY(AABB box) {
        return aabbCoordinate(box, "maxY");
    }

    private static double aabbMaxZ(AABB box) {
        return aabbCoordinate(box, "maxZ");
    }

    private static double aabbCoordinate(AABB box, String fieldName) {
        try {
            Object value = box.getClass().getField(fieldName).get(box);
            return value instanceof Number number ? number.doubleValue() : 0.0D;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve AABB " + fieldName, exception);
        }
    }

    private static String vectorKey(Vec3 vector) {
        return vectorKey((int) vecX(vector), (int) vecY(vector), (int) vecZ(vector));
    }

    private static String vectorKey(Object vector) {
        return vectorKey(vec3iX(vector), vec3iY(vector), vec3iZ(vector));
    }

    private static String vectorKey(int x, int y, int z) {
        return x + "," + y + "," + z;
    }

    private static Vec3 directionVector(Direction direction) {
        Object normal = directionNormal(direction);
        return new Vec3(vec3iX(normal), vec3iY(normal), vec3iZ(normal));
    }

    private static int vec3iX(Object vector) {
        try {
            Object value = vector.getClass().getMethod("getX").invoke(vector);
            return value instanceof Integer coordinate ? coordinate : 0;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve Vec3i x", exception);
        }
    }

    private static int vec3iY(Object vector) {
        try {
            Object value = vector.getClass().getMethod("getY").invoke(vector);
            return value instanceof Integer coordinate ? coordinate : 0;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve Vec3i y", exception);
        }
    }

    private static int vec3iZ(Object vector) {
        try {
            Object value = vector.getClass().getMethod("getZ").invoke(vector);
            return value instanceof Integer coordinate ? coordinate : 0;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve Vec3i z", exception);
        }
    }

    private static Object directionNormal(Direction direction) {
        try {
            Object normal;
            try {
                normal = direction.getClass().getMethod("getUnitVec3i").invoke(direction);
            } catch (NoSuchMethodException exception) {
                normal = direction.getClass().getMethod("getNormal").invoke(direction);
            }
            return normal;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve direction normal", exception);
        }
    }

    private static Direction directionOpposite(Direction direction) {
        try {
            return (Direction) direction.getClass().getMethod("getOpposite").invoke(direction);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve opposite direction", exception);
        }
    }

    private static Object directionAxis(Direction direction) {
        try {
            return direction.getClass().getMethod("getAxis").invoke(direction);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve direction axis", exception);
        }
    }

    private static Object directionAxisDirection(Direction direction) {
        try {
            return direction.getClass().getMethod("getAxisDirection").invoke(direction);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve direction axis direction", exception);
        }
    }

    // ==================== INTERNAL BOUNDING BOX MERGER CLASS ====================

    /**
     * Internal helper class for merging bounding boxes efficiently.
     */
    private static final class BoundingBoxMerger {
        private static final Map<String, Direction> DIRECTION_LOOKUP = Arrays.stream(Direction.values())
                .collect(Collectors.toMap(dir -> vectorKey(directionNormal(dir)),
                        dir -> dir,
                        (a, b) -> {
                            throw new IllegalStateException("Duplicate direction detected.");
                        },
                        HashMap::new
                ));

        private final Map<Vec3, AABB> positionToBox = new HashMap<>();
        private final Multimap<AABB, Vec3> boxToPosition = HashMultimap.create();
        private double xCoordTracker = Double.NEGATIVE_INFINITY;
        private double yCoordTracker = Double.NEGATIVE_INFINITY;
        private Vec3 currentCenter = null;
        private AABB currentBounds = null;

        /**
         * Determines if two bounding boxes are aligned along a given direction.
         */
        private static boolean isAligned(AABB first, AABB second, Direction direction) {
            return getAxisValue(first, direction) == getAxisValue(
                    second,
                    directionOpposite(direction)
            ) && Arrays.stream(Direction.values())
                    .filter(d -> directionAxis(d) != directionAxis(direction))
                    .allMatch(d -> getAxisValue(first, d) == getAxisValue(second, d));
        }

        /**
         * Retrieves the value of a bounding box along a specified direction.
         */
        private static double getAxisValue(AABB box, Direction direction) {
            Object axis = directionAxis(direction);
            boolean positive = "POSITIVE".equals(((Enum<?>) directionAxisDirection(direction)).name());
            return switch (((Enum<?>) axis).name()) {
                case "X" -> positive ? aabbMaxX(box) : aabbMinX(box);
                case "Y" -> positive ? aabbMaxY(box) : aabbMinY(box);
                case "Z" -> positive ? aabbMaxZ(box) : aabbMinZ(box);
                default -> throw new IllegalStateException("Unknown direction axis: " + axis);
            };
        }

        /**
         * Converts a vector into a direction based on its coordinates.
         */
        private static Direction directionFromVector(Vec3 vector) {
            return DIRECTION_LOOKUP.get(vectorKey((int) vecX(vector), (int) vecY(vector), (int) vecZ(vector)));
        }

        /**
         * Attempts to merge the current bounding box with its neighboring bounding box.
         */
        private boolean canCombine(AABB current, AABB neighbor, Vec3 center) {
            Direction direction = directionFromVector(subtractVectors(center, aabbCenter(current)));
            return direction != null && isAligned(current, neighbor, direction) && mergeBoxes(
                    current,
                    neighbor,
                    center
            );
        }

        /**
         * Attempts to merge the provided bounding box with any neighboring bounding boxes.
         */
        private boolean tryCombineAdjacent(Vec3 center, AABB box) {
            for (Direction direction : Direction.values()) {
                Vec3 adjacentCenter = addVectors(center, directionVector(direction));
                AABB adjacentBox = positionToBox.get(adjacentCenter);

                if (adjacentBox != null && isAligned(box, adjacentBox, direction)) {
                    return mergeBoxes(adjacentBox, box, center);
                }
            }
            return false;
        }

        /**
         * Merges two bounding boxes and updates the necessary mappings.
         */
        private boolean mergeBoxes(AABB source, AABB target, Vec3 center) {
            AABB expanded = mergeAabbs(source, target);

            Set<Vec3> mergedPositions = new HashSet<>(boxToPosition.removeAll(source));
            mergedPositions.forEach(v -> positionToBox.put(v, expanded));

            boxToPosition.putAll(expanded, mergedPositions);
            positionToBox.put(center, expanded);
            boxToPosition.put(expanded, center);

            currentBounds = expanded;
            return true;
        }
    }
}
