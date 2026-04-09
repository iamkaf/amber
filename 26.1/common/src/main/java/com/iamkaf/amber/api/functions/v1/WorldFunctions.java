package com.iamkaf.amber.api.functions.v1;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.DifficultyInstance;
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
        long gameTime = level.getGameTime();

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
        var itemEntity = new ItemEntity(level, pos.x(), pos.y(), pos.z(), stack, delta.x(), delta.y(), delta.z());
        level.addFreshEntity(itemEntity);
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
        Vec3 eyePosition = player.getEyePosition();
        Vec3 rotation = player.getViewVector(1);
        double reach = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        Vec3 combined = eyePosition.add(rotation.x * reach, rotation.y * reach, rotation.z * reach);
        return level.clip(new ClipContext(eyePosition, combined, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }

    // ==================== LEVEL-BASED SOUND OPERATIONS ====================

    /**
     * Plays a sound at the specified position in the level with default settings.
     *
     * @param level The level where the sound should be played.
     * @param position The position to play the sound at.
     * @param sound The sound event to play.
     */
    public static void playSoundAt(Level level, Vec3 position, Holder<SoundEvent> sound) {
        level.playSound(null, position.x(), position.y(), position.z(), sound, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    /**
     * Plays a sound at the specified position in the level with a specified sound source.
     *
     * @param level The level where the sound should be played.
     * @param position The position to play the sound at.
     * @param sound The sound event to play.
     * @param source The sound source category.
     */
    public static void playSoundAt(Level level, Vec3 position, Holder<SoundEvent> sound, SoundSource source) {
        level.playSound(null, position.x(), position.y(), position.z(), sound, source, 1.0f, 1.0f);
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
    public static void playSoundAt(Level level, Vec3 position, Holder<SoundEvent> sound, SoundSource source, float volume) {
        level.playSound(null, position.x(), position.y(), position.z(), sound, source, volume, 1.0f);
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
    public static void playSoundAt(Level level, Vec3 position, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch) {
        level.playSound(null, position.x(), position.y(), position.z(), sound, source, volume, pitch);
    }

    /**
     * Plays a sound at the specified position in the level using a BlockPos.
     *
     * @param level The level where the sound should be played.
     * @param position The position to play the sound at.
     * @param sound The sound event to play.
     * @param source The sound source category.
     */
    public static void playSoundAt(Level level, BlockPos position, Holder<SoundEvent> sound, SoundSource source) {
        level.playSound(null, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, sound, source, 1.0f, 1.0f);
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
    public static void playSoundAt(Level level, BlockPos position, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch) {
        level.playSound(null, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, sound, source, volume, pitch);
    }

    // ==================== DIMENSION UTILITIES ====================

    /**
     * Checks if the level is the Overworld dimension.
     *
     * @param level The level to check.
     * @return true if the level is the Overworld, false otherwise.
     */
    public static boolean isOverworld(Level level) {
        return level.dimension() == Level.OVERWORLD;
    }

    /**
     * Checks if the level is the Nether dimension.
     *
     * @param level The level to check.
     * @return true if the level is the Nether, false otherwise.
     */
    public static boolean isNether(Level level) {
        return level.dimension() == Level.NETHER;
    }

    /**
     * Checks if the level is the End dimension.
     *
     * @param level The level to check.
     * @return true if the level is the End, false otherwise.
     */
    public static boolean isEnd(Level level) {
        return level.dimension() == Level.END;
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
        return pos1.distanceTo(pos2);
    }

    /**
     * Calculates the distance between two block positions.
     *
     * @param pos1 The first block position.
     * @param pos2 The second block position.
     * @return The Euclidean distance between the block positions.
     */
    public static double distanceBetween(BlockPos pos1, BlockPos pos2) {
        return Math.sqrt(pos1.distSqr(pos2));
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
        return pos1.distanceToSqr(pos2);
    }

    /**
     * Calculates the squared distance between two block positions.
     *
     * @param pos1 The first block position.
     * @param pos2 The second block position.
     * @return The squared distance between the block positions.
     */
    public static double distanceSquaredBetween(BlockPos pos1, BlockPos pos2) {
        return pos1.distSqr(pos2);
    }

    /**
     * Calculates the horizontal distance between two positions (ignoring Y coordinate).
     *
     * @param pos1 The first position.
     * @param pos2 The second position.
     * @return The horizontal distance between the positions.
     */
    public static double horizontalDistanceBetween(Vec3 pos1, Vec3 pos2) {
        double dx = pos1.x() - pos2.x();
        double dz = pos1.z() - pos2.z();
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
        return level.getOverworldClockTime() % 24000L;
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
        return (int) ((level.getOverworldClockTime() / 24000L) % 8L);
    }

    /**
     * Gets the total number of days that have passed in the level.
     *
     * @param level The level to get the day count from.
     * @return The total number of days passed.
     */
    public static long getDayCount(Level level) {
        return level.getOverworldClockTime() / 24000L;
    }

    /**
     * Gets the total game time in ticks that have passed.
     *
     * @param level The level to get the game time from.
     * @return The total game time in ticks.
     */
    public static long getTotalGameTime(Level level) {
        return level.getGameTime();
    }

    // ==================== DIFFICULTY UTILITIES ====================

    /**
     * Gets the current difficulty at a specific position.
     *
     * @param level The level to check. Must be a ServerLevelAccessor.
     * @param position The position to check difficulty at.
     * @return The difficulty instance at the position.
     */
    public static DifficultyInstance getCurrentDifficulty(ServerLevelAccessor level, BlockPos position) {
        return level.getCurrentDifficultyAt(position);
    }

    /**
     * Gets the current difficulty at a specific position using Vec3.
     *
     * @param level The level to check. Must be a ServerLevelAccessor.
     * @param position The position to check difficulty at.
     * @return The difficulty instance at the position.
     */
    public static DifficultyInstance getCurrentDifficulty(ServerLevelAccessor level, Vec3 position) {
        return level.getCurrentDifficultyAt(BlockPos.containing(position));
    }

    /**
     * Checks if the difficulty at a position is at least hard.
     *
     * @param level The level to check. Must be a ServerLevelAccessor.
     * @param position The position to check difficulty at.
     * @return true if difficulty is hard or harder, false otherwise.
     */
    public static boolean isHardDifficulty(ServerLevelAccessor level, BlockPos position) {
        return getCurrentDifficulty(level, position).isHard();
    }

    /**
     * Checks if the difficulty at a position is at least hard using Vec3.
     *
     * @param level The level to check. Must be a ServerLevelAccessor.
     * @param position The position to check difficulty at.
     * @return true if difficulty is hard or harder, false otherwise.
     */
    public static boolean isHardDifficulty(ServerLevelAccessor level, Vec3 position) {
        return getCurrentDifficulty(level, position).isHard();
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
            return new ArrayList<>(serverLevel.players());
        } else if (level instanceof net.minecraft.client.multiplayer.ClientLevel clientLevel) {
            return new ArrayList<>(clientLevel.players());
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
                center.x() - radius, center.y() - radius, center.z() - radius,
                center.x() + radius, center.y() + radius, center.z() + radius
        );
        return level.getEntities((Entity) null, boundingBox, entity -> true);
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
                center.x() - radius, center.y() - radius, center.z() - radius,
                center.x() + radius, center.y() + radius, center.z() + radius
        );
        return level.getEntities(entityType, boundingBox, entity -> true);
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
                center.x() - radius, center.y() - radius, center.z() - radius,
                center.x() + radius, center.y() + radius, center.z() + radius
        );
        return level.getEntities((Entity) null, boundingBox, predicate);
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
        return getEntitiesInRadius(level, Vec3.atCenterOf(center), radius);
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
        return getEntitiesInRadius(level, Vec3.atCenterOf(center), radius, entityType);
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
                .min(Comparator.comparingDouble(entity -> entity.position().distanceToSqr(center)))
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
    public static Holder<Biome> getBiomeAtPosition(Level level, BlockPos position) {
        return level.getBiome(position);
    }

    /**
     * Gets the biome at a specific position using Vec3.
     *
     * @param level The level to check.
     * @param position The position to get the biome at.
     * @return The biome at the position.
     */
    public static Holder<Biome> getBiomeAtPosition(Level level, Vec3 position) {
        return level.getBiome(BlockPos.containing(position));
    }

    /**
     * Gets the biome value at a specific position.
     *
     * @param level The level to check.
     * @param position The position to get the biome at.
     * @return The biome value at the position.
     */
    public static Biome getBiomeValueAtPosition(Level level, BlockPos position) {
        return level.getBiome(position).value();
    }

    /**
     * Gets the biome value at a specific position using Vec3.
     *
     * @param level The level to check.
     * @param position The position to get the biome at.
     * @return The biome value at the position.
     */
    public static Biome getBiomeValueAtPosition(Level level, Vec3 position) {
        return level.getBiome(BlockPos.containing(position)).value();
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
        return level.getBiome(position).value().getPrecipitationAt(position, level.getSeaLevel()) == precipitation;
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
        return hasPrecipitation(level, BlockPos.containing(position), precipitation);
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
                .map(pos -> pos.subtract(referencePoint))
                .sorted()
                .map(AABB::new)
                .forEachOrdered(box -> {
                    // Reset current bounds if we encounter a new x or y coordinate
                    if (boxMerger.xCoordTracker != box.minX || boxMerger.yCoordTracker != box.minY) {
                        boxMerger.currentBounds = null;
                    }

                    boxMerger.xCoordTracker = box.minX;
                    boxMerger.yCoordTracker = box.minY;

                    Vec3 center = box.getCenter();
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

    // ==================== INTERNAL BOUNDING BOX MERGER CLASS ====================

    /**
     * Internal helper class for merging bounding boxes efficiently.
     */
    private static final class BoundingBoxMerger {
        private static final Long2ObjectMap<Direction> DIRECTION_LOOKUP = Arrays.stream(Direction.values())
                .collect(Collectors.toMap(dir -> new BlockPos(dir.getUnitVec3i()).asLong(),
                        dir -> dir,
                        (a, b) -> {
                            throw new IllegalStateException("Duplicate direction detected.");
                        },
                        Long2ObjectOpenHashMap::new
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
                    direction.getOpposite()
            ) && Arrays.stream(Direction.values())
                    .filter(d -> d.getAxis() != direction.getAxis())
                    .allMatch(d -> getAxisValue(first, d) == getAxisValue(second, d));
        }

        /**
         * Retrieves the value of a bounding box along a specified direction.
         */
        private static double getAxisValue(AABB box, Direction direction) {
            return direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ?
                    box.max(direction.getAxis()) : box.min(
                    direction.getAxis());
        }

        /**
         * Converts a vector into a direction based on its coordinates.
         */
        private static Direction directionFromVector(Vec3 vector) {
            return DIRECTION_LOOKUP.get(BlockPos.asLong((int) vector.x, (int) vector.y, (int) vector.z));
        }

        /**
         * Attempts to merge the current bounding box with its neighboring bounding box.
         */
        private boolean canCombine(AABB current, AABB neighbor, Vec3 center) {
            Direction direction = directionFromVector(center.subtract(current.getCenter()));
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
                Vec3 adjacentCenter = center.add(Vec3.atLowerCornerOf(direction.getUnitVec3i()));
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
            AABB expanded = source.minmax(target);

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