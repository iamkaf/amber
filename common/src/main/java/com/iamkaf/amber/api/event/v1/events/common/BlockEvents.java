package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockEvents {
    /**
     * An event that is called before a block is broken by a player. This event can be cancelled to prevent the block from being broken.
     */
    public static final Event<BlockBreakBefore> BLOCK_BREAK_BEFORE = EventFactory.createArrayBacked(
            BlockBreakBefore.class, callbacks -> (level, player, pos, state, blockEntity) -> {
                for (BlockBreakBefore callback : callbacks) {
                    InteractionResult result = callback.beforeBlockBreak(level, player, pos, state, blockEntity);
                    if (result != InteractionResult.PASS) {
                        return result; // Early return for cancellation
                    }
                }
                return InteractionResult.PASS; // Allow break by default
            }
    );

    /**
     * An event that is called after a block has been broken by a player.
     */
    public static final Event<BlockBreakAfter> BLOCK_BREAK_AFTER = EventFactory.createArrayBacked(
            BlockBreakAfter.class, callbacks -> (level, player, pos, state, blockEntity) -> {
                for (BlockBreakAfter callback : callbacks) {
                    callback.afterBlockBreak(level, player, pos, state, blockEntity);
                }
            }
    );

    /**
     * An event that is called before a block is placed by a player. This event can be cancelled to prevent the block from being placed.
     */
    public static final Event<BlockPlaceBefore> BLOCK_PLACE_BEFORE = EventFactory.createArrayBacked(
            BlockPlaceBefore.class, callbacks -> (level, player, pos, state, context) -> {
                for (BlockPlaceBefore callback : callbacks) {
                    InteractionResult result = callback.beforeBlockPlace(level, player, pos, state, context);
                    if (result != InteractionResult.PASS) {
                        return result; // Early return for cancellation
                    }
                }
                return InteractionResult.PASS; // Allow placement by default
            }
    );

    /**
     * An event that is called after a block has been placed by a player.
     */
    public static final Event<BlockPlaceAfter> BLOCK_PLACE_AFTER = EventFactory.createArrayBacked(
            BlockPlaceAfter.class, callbacks -> (level, player, pos, state, context) -> {
                for (BlockPlaceAfter callback : callbacks) {
                    callback.afterBlockPlace(level, player, pos, state, context);
                }
            }
    );

    /**
     * An event that is called when a player right-clicks on a block with an item.
     */
    public static final Event<BlockInteract> BLOCK_INTERACT = EventFactory.createArrayBacked(
            BlockInteract.class, callbacks -> (player, level, hand, hitResult) -> {
                for (BlockInteract callback : callbacks) {
                    InteractionResult result = callback.onBlockInteract(player, level, hand, hitResult);
                    if (result != InteractionResult.PASS) {
                        return result; // Early return for cancellation
                    }
                }
                return InteractionResult.PASS; // Allow interaction by default
            }
    );

    /**
     * An event that is called when a player left-clicks on a block (attack/punch).
     */
    public static final Event<BlockClick> BLOCK_CLICK = EventFactory.createArrayBacked(
            BlockClick.class, callbacks -> (player, level, hand, pos, direction) -> {
                for (BlockClick callback : callbacks) {
                    InteractionResult result = callback.onBlockClick(player, level, hand, pos, direction);
                    if (result != InteractionResult.PASS) {
                        return result; // Early return for cancellation
                    }
                }
                return InteractionResult.PASS; // Allow click by default
            }
    );

    @FunctionalInterface
    public interface BlockBreakBefore {
        /**
         * Called before a block is broken by a player.
         *
         * @param level       the level/world where the block is being broken
         * @param player      the player breaking the block
         * @param pos         the position of the block being broken
         * @param state       the state of the block being broken
         * @param blockEntity the block entity at the position, if any (may be null)
         * @return an {@link InteractionResult} indicating whether the break should be allowed or cancelled
         */
        InteractionResult beforeBlockBreak(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity);
    }

    @FunctionalInterface
    public interface BlockBreakAfter {
        /**
         * Called after a block has been broken by a player.
         *
         * @param level       the level/world where the block was broken
         * @param player      the player who broke the block
         * @param pos         the position of the block that was broken
         * @param state       the state of the block that was broken
         * @param blockEntity the block entity that was at the position, if any (may be null)
         */
        void afterBlockBreak(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity);
    }

    @FunctionalInterface
    public interface BlockPlaceBefore {
        /**
         * Called before a block is placed by a player.
         *
         * @param level   the level/world where the block is being placed
         * @param player  the player placing the block
         * @param pos     the position where the block is being placed
         * @param state   the state of the block being placed
         * @param context additional context about the placement (item stack being used)
         * @return an {@link InteractionResult} indicating whether the placement should be allowed or cancelled
         */
        InteractionResult beforeBlockPlace(Level level, Player player, BlockPos pos, BlockState state, ItemStack context);
    }

    @FunctionalInterface
    public interface BlockPlaceAfter {
        /**
         * Called after a block has been placed by a player.
         *
         * @param level   the level/world where the block was placed
         * @param player  the player who placed the block
         * @param pos     the position where the block was placed
         * @param state   the state of the block that was placed
         * @param context additional context about the placement (item stack that was used)
         */
        void afterBlockPlace(Level level, Player player, BlockPos pos, BlockState state, ItemStack context);
    }

    @FunctionalInterface
    public interface BlockInteract {
        /**
         * Called when a player right-clicks on a block.
         *
         * @param player    the player interacting with the block
         * @param level     the level/world where the interaction is happening
         * @param hand      the hand used for the interaction
         * @param hitResult the hit result containing position and face information
         * @return an {@link InteractionResult} indicating whether the interaction should be allowed or cancelled
         */
        InteractionResult onBlockInteract(Player player, Level level, InteractionHand hand, BlockHitResult hitResult);
    }

    @FunctionalInterface
    public interface BlockClick {
        /**
         * Called when a player left-clicks (attacks/punches) a block.
         *
         * @param player    the player clicking the block
         * @param level     the level/world where the click is happening
         * @param hand      the hand used for the click
         * @param pos       the position of the block being clicked
         * @param direction the face of the block being clicked
         * @return an {@link InteractionResult} indicating whether the click should be allowed or cancelled
         */
        InteractionResult onBlockClick(Player player, Level level, InteractionHand hand, BlockPos pos, net.minecraft.core.Direction direction);
    }
}