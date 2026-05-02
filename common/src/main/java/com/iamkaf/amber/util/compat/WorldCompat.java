package com.iamkaf.amber.util.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
//? if >=1.18.2
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
//? if >=1.16.2
import net.minecraft.world.level.ServerLevelAccessor;
//? if <1.16.2
/*import net.minecraft.world.level.LevelAccessor;*/
//? if <1.16
/*import net.minecraft.world.level.dimension.DimensionType;*/
import net.minecraft.world.level.biome.Biome;
//? if >=1.16
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
        //? if >=1.17
        return player.getEyePosition();
        //? if <1.17
        /*return player.getEyePosition(1.0F);*/
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
            //? if >=1.18.2
            Holder<SoundEvent> sound,
            //? if <1.18.2
            /*SoundEvent sound,*/
            SoundSource source,
            float volume,
            float pitch
    ) {
        //? if >=1.18.2
        level.playSound(player, x, y, z, sound.value(), source, volume, pitch);
        //? if <1.18.2
        /*level.playSound(player, x, y, z, sound, source, volume, pitch);*/
    }

    public static DifficultyInstance currentDifficulty(
            //? if >=1.16.2
            ServerLevelAccessor level,
            //? if <1.16.2
            /*LevelAccessor level,*/
            BlockPos position) {
        return level.getCurrentDifficultyAt(position);
    }

    public static boolean isDifficultyHard(DifficultyInstance difficulty) {
        //? if >=1.17
        return difficulty.isHard();
        //? if <1.17
        /*return difficulty.getDifficulty() == Difficulty.HARD;*/
    }

    public static Biome.Precipitation precipitation(Biome biome) {
        //? if >=1.21
        return biome.hasPrecipitation() ? Biome.Precipitation.RAIN : Biome.Precipitation.NONE;
        //? if <1.21
        /*
        return biome.getPrecipitation();
        */
    }

    public static List<Player> players(Level level) {
        return new ArrayList<>(level.players());
    }

//? if >=1.18.2 {
    public static <T> T holderValue(Holder<T> holder) {
        return holder.value();
    }
//?}

    public static String dimensionPath(Level level) {
        //? if >=1.16 {
        ResourceKey<?> key = level.dimension();
        return key.location().getPath();
        //?} else {
        /*return DimensionType.getName(level.dimension.getType()).getPath();*/
        //?}
    }

    public static int seaLevel(Level level) {
        return level.getSeaLevel();
    }

    public static List<Entity> entities(Level level, Entity except, AABB bounds, java.util.function.Predicate<Entity> predicate) {
        return level.getEntities(except, bounds, predicate);
    }

    public static <T extends Entity> List<T> typedEntities(Level level, EntityType<T> type, AABB bounds, java.util.function.Predicate<Entity> predicate) {
        //? if >=1.17
        return level.getEntities(type, bounds, predicate);
        //? if <1.17 {
        /*@SuppressWarnings("unchecked")
        List<T> result = (List<T>) (List<?>) level.getEntities(type, bounds, predicate);
        return result;*/
        //?}
    }

    //? if >=1.18.2
    public static Holder<Biome> biome(Level level, BlockPos position) {
    //? if <1.18.2
    /*public static Biome biome(Level level, BlockPos position) {*/
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
        //? if >=1.21.2
        return direction.getUnitVec3i();
        //? if <1.21.2
        /*return direction.getNormal();*/
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
