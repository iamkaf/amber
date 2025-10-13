package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Events related to farming and agriculture mechanics in Minecraft.
 */
public class FarmingEvents {

    /**
     * An event that is fired when bonemeal is applied to a block.
     * <p>
     * This event is fired before the bonemeal effect is applied and can be used to modify or cancel the effect.
     * </p>
     *
     * <p>Return values:</p>
     * <ul>
     *     <li>PASS - Allow bonemeal to be applied normally</li>
     *     <li>FAIL - Cancel the bonemeal application</li>
     *     <li>SUCCESS - Cancel vanilla behavior (bonemeal consumed, custom behavior applied)</li>
     *     <li>CONSUME - Cancel vanilla behavior (bonemeal consumed, custom behavior applied)</li>
     * </ul>
     */
    public static final Event<BonemealUse> BONEMEAL_USE = EventFactory.createArrayBacked(
            BonemealUse.class, callbacks -> (level, pos, state, stack, entity) -> {
                for (BonemealUse callback : callbacks) {
                    InteractionResult result = callback.onBonemealUse(level, pos, state, stack, entity);
                    if (result != InteractionResult.PASS) {
                        return result; // Early return for cancellation or custom behavior
                    }
                }
                return InteractionResult.PASS; // Allow bonemeal by default
            }
    );

    /**
     * An event that is fired when an entity tramples farmland.
     * <p>
     * This event is fired before the farmland is turned to dirt and can be used to prevent trampling.
     * </p>
     *
     * <p>Return values:</p>
     * <ul>
     *     <li>PASS - Allow farmland to be trampled normally</li>
     *     <li>FAIL - Prevent farmland trampling</li>
     *     <li>SUCCESS - Prevent farmland trampling</li>
     *     <li>CONSUME - Prevent farmland trampling</li>
     * </ul>
     */
    public static final Event<FarmlandTrample> FARMLAND_TRAMPLE = EventFactory.createArrayBacked(
            FarmlandTrample.class, callbacks -> (level, pos, state, fallDistance, entity) -> {
                for (FarmlandTrample callback : callbacks) {
                    InteractionResult result = callback.onFarmlandTrample(level, pos, state, fallDistance, entity);
                    if (result != InteractionResult.PASS) {
                        return result; // Early return for cancellation
                    }
                }
                return InteractionResult.PASS; // Allow trampling by default
            }
    );

    /**
     * An event that is fired when a crop attempts to grow.
     * <p>
     * This event is fired before the crop grows and can be used to modify or cancel growth.
     * <p>
     * This event fires on the server side only.
     * </p>
     * </p>
     *
     * <p>Return values:</p>
     * <ul>
     *     <li>PASS - Allow crop to grow normally</li>
     *     <li>FAIL - Cancel crop growth</li>
     *     <li>SUCCESS - Cancel vanilla growth (custom growth applied)</li>
     *     <li>CONSUME - Cancel vanilla growth (custom growth applied)</li>
     * </ul>
     */
    public static final Event<CropGrow> CROP_GROW = EventFactory.createArrayBacked(
            CropGrow.class, callbacks -> (level, pos, state) -> {
                for (CropGrow callback : callbacks) {
                    InteractionResult result = callback.onCropGrow(level, pos, state);
                    if (result != InteractionResult.PASS) {
                        return result; // Early return for cancellation or custom behavior
                    }
                }
                return InteractionResult.PASS; // Allow growth by default
            }
    );

    /**
     * Functional interface for handling {@link #BONEMEAL_USE} callbacks.
     */
    @FunctionalInterface
    public interface BonemealUse {
        /**
         * Called when bonemeal is applied to a block.
         *
         * @param level  the level/world where bonemeal is being applied
         * @param pos    the position of the block being bonemealed
         * @param state  the block state being bonemealed
         * @param stack  the bonemeal item stack
         * @param entity the entity applying the bonemeal (usually a player)
         * @return an {@link InteractionResult} indicating whether the bonemeal should be applied
         */
        InteractionResult onBonemealUse(Level level, BlockPos pos, BlockState state, ItemStack stack, Entity entity);
    }

    /**
     * Functional interface for handling {@link #FARMLAND_TRAMPLE} callbacks.
     */
    @FunctionalInterface
    public interface FarmlandTrample {
        /**
         * Called when an entity tramples farmland.
         *
         * @param level        the level/world where trampling occurs
         * @param pos          the position of the farmland block
         * @param state        the farmland block state
         * @param fallDistance the fall distance of the entity
         * @param entity       the entity trampling the farmland
         * @return an {@link InteractionResult} indicating whether the farmland should be trampled
         */
        InteractionResult onFarmlandTrample(Level level, BlockPos pos, BlockState state, float fallDistance,
                Entity entity);
    }

    /**
     * Functional interface for handling {@link #CROP_GROW} callbacks.
     */
    @FunctionalInterface
    public interface CropGrow {
        /**
         * Called when a crop attempts to grow.
         *
         * @param level the level/world where the crop is growing
         * @param pos   the position of the crop block
         * @param state the crop block state
         * @return an {@link InteractionResult} indicating whether the crop should grow
         */
        InteractionResult onCropGrow(Level level, BlockPos pos, BlockState state);
    }
}
