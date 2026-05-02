package com.iamkaf.amber.util.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class WorldCompat {
    private WorldCompat() {
    }

    public static long gameTime(Level level) {
        return level.getGameTime();
    }

    public static long dayTime(Level level) {
        return level.getDayTime();
    }

    public static double vecX(Vec3 vector) {
        return vector.x;
    }

    public static double vecY(Vec3 vector) {
        return vector.y;
    }

    public static double vecZ(Vec3 vector) {
        return vector.z;
    }

    public static int blockX(BlockPos position) {
        return position.getX();
    }

    public static int blockY(BlockPos position) {
        return position.getY();
    }

    public static int blockZ(BlockPos position) {
        return position.getZ();
    }

    public static Vec3 entityPosition(Entity entity) {
        return entity.position();
    }

    public static void addFreshEntity(Level level, Entity entity) {
        level.addFreshEntity(entity);
    }

    public static Vec3 playerEyePosition(Player player) {
        return player.getEyePosition();
    }

    public static Vec3 playerViewVector(Player player, float partialTick) {
        return player.getViewVector(partialTick);
    }

    public static BlockHitResult clip(Level level, ClipContext context) {
        return level.clip(context);
    }

    public static void playLevelSound(
            Level level,
            Player player,
            double x,
            double y,
            double z,
            Holder<SoundEvent> sound,
            SoundSource source,
            float volume,
            float pitch
    ) {
        level.playSound(player, x, y, z, sound.value(), source, volume, pitch);
    }

    public static DifficultyInstance currentDifficulty(
            ServerLevelAccessor level,
            BlockPos position) {
        return level.getCurrentDifficultyAt(position);
    }

    public static boolean isDifficultyHard(DifficultyInstance difficulty) {
        return difficulty.isHard();
    }

    public static Biome.Precipitation precipitation(Biome biome) {
        return biome.hasPrecipitation() ? Biome.Precipitation.RAIN : Biome.Precipitation.NONE;

    }

    public static List<Player> players(Level level) {
        return new ArrayList<>(level.players());
    }

    public static <T> T holderValue(Holder<T> holder) {
        return holder.value();
    }

    public static String dimensionPath(Level level) {
        ResourceKey<?> key = level.dimension();
        return key.location().getPath();
    }

    public static int seaLevel(Level level) {
        return level.getSeaLevel();
    }

    public static List<Entity> entities(Level level, Entity except, AABB bounds, java.util.function.Predicate<Entity> predicate) {
        return level.getEntities(except, bounds, predicate);
    }

    public static <T extends Entity> List<T> typedEntities(Level level, EntityType<T> type, AABB bounds, java.util.function.Predicate<Entity> predicate) {
        return level.getEntities(type, bounds, predicate);
    }

    public static Holder<Biome> biome(Level level, BlockPos position) {
        return level.getBiome(position);
    }

    public static double aabbMinX(AABB box) { return box.minX; }
    public static double aabbMinY(AABB box) { return box.minY; }
    public static double aabbMinZ(AABB box) { return box.minZ; }
    public static double aabbMaxX(AABB box) { return box.maxX; }
    public static double aabbMaxY(AABB box) { return box.maxY; }
    public static double aabbMaxZ(AABB box) { return box.maxZ; }

    public static int vec3iX(Vec3i vector) {
        return vector.getX();
    }

    public static int vec3iY(Vec3i vector) {
        return vector.getY();
    }

    public static int vec3iZ(Vec3i vector) {
        return vector.getZ();
    }

    public static Vec3i directionNormal(Direction direction) {
        return direction.getUnitVec3i();
    }

    public static Direction directionOpposite(Direction direction) {
        return direction.getOpposite();
    }

    public static Direction.Axis directionAxis(Direction direction) {
        return direction.getAxis();
    }

    public static Direction.AxisDirection directionAxisDirection(Direction direction) {
        return direction.getAxisDirection();
    }
}
