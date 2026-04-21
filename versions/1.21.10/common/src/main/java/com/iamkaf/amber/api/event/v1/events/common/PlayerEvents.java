package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PlayerEvents {

    /**
     * An event that is fired when a player attempts to interact with (e.g. right-clicks) an entity.
     * <p>
     * Equivalent to Fabric's {@code EntityInteractEvent}.
     * <p>
     *
     * <p>
     * It is invoked from {@link Player#interactOn(Entity, InteractionHand)} before the target
     * entity's own interaction logic is executed.
     * </p>
     *
     * <p>On the logical server, the return values have the following meaning:</p>
     *  <ul>
     *      <li>PASS falls back to further processing.</li>
     *      <li>Any other value cancels further processing.</li>
     *  </ul>
     *
     * If every listener returns {@code PASS}, vanilla behaviour proceeds unchanged.
     */
    public static final Event<EntityInteract> ENTITY_INTERACT = EventFactory.createArrayBacked(
            EntityInteract.class, callbacks -> (player, world, hand, entity) -> {
                for (EntityInteract callback : callbacks) {
                    InteractionResult result = callback.interact(player, world, hand, entity);
                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }
                return InteractionResult.PASS;
            }
    );

    /**
     * An event that is fired when a player joins the server.
     * <p>
     * This event is fired on the logical server side after the player has fully logged in and is ready to play.
     * </p>
     */
    public static final Event<PlayerJoin> PLAYER_JOIN = EventFactory.createArrayBacked(
            PlayerJoin.class, callbacks -> (player) -> {
                for (PlayerJoin callback : callbacks) {
                    callback.onPlayerJoin(player);
                }
            }
    );

    /**
     * An event that is fired when a player leaves the server.
     * <p>
     * This event is fired on the logical server side when a player disconnects from the server.
     * </p>
     */
    public static final Event<PlayerLeave> PLAYER_LEAVE = EventFactory.createArrayBacked(
            PlayerLeave.class, callbacks -> (player) -> {
                for (PlayerLeave callback : callbacks) {
                    callback.onPlayerLeave(player);
                }
            }
    );

    /**
     * An event that is fired after a player respawns.
     * <p>
     * This event is fired on the logical server side after the player has been respawned,
     * either from death or from exiting the End dimension.
     * </p>
     */
    public static final Event<PlayerRespawn> PLAYER_RESPAWN = EventFactory.createArrayBacked(
            PlayerRespawn.class, callbacks -> (oldPlayer, newPlayer, alive) -> {
                for (PlayerRespawn callback : callbacks) {
                    callback.onPlayerRespawn(oldPlayer, newPlayer, alive);
                }
            }
    );

    /**
     * An event that is fired when a player crafts an item.
     * <p>
     * This event is fired on the logical server side after a player successfully crafts an item
     * using a crafting table or other crafting mechanism.
     * </p>
     * <p>
     * <b>This event is informational only and cannot be cancelled.</b>
     * The crafting has already completed when this event is fired.
     * </p>
     */
    public static final Event<CraftItem> CRAFT_ITEM = EventFactory.createArrayBacked(
            CraftItem.class, callbacks -> (player, craftedItems) -> {
                for (CraftItem callback : callbacks) {
                    callback.onCraftItem(player, craftedItems);
                }
            }
    );

    /**
     * An event that is fired when a player successfully blocks damage with a shield.
     * <p>
     * This event is fired on both the client and server side when a player blocks
     * damage using a shield. The shield ItemStack and blocked damage are provided.
     * </p>
     * <p>
     * <b>This event is informational only and cannot be cancelled.</b>
     * The blocking has already completed when this event is fired.
     * </p>
     */
    public static final Event<ShieldBlock> SHIELD_BLOCK = EventFactory.createArrayBacked(
        ShieldBlock.class, callbacks -> (player, shield, blockedDamage, source) -> {
            for (ShieldBlock callback : callbacks) {
                callback.onShieldBlock(player, shield, blockedDamage, source);
            }
        }
    );

    /**
     * An informational event fired when a player starts fishing.
     */
    public static final Event<FishingStart> FISHING_START = EventFactory.createArrayBacked(
            FishingStart.class, callbacks -> context -> {
                for (FishingStart callback : callbacks) {
                    callback.start(context);
                }
            }
    );

    /**
     * An informational event fired when a fishing hook gets a bite.
     */
    public static final Event<FishingBite> FISHING_BITE = EventFactory.createArrayBacked(
            FishingBite.class, callbacks -> context -> {
                for (FishingBite callback : callbacks) {
                    callback.bite(context);
                }
            }
    );

    /**
     * An informational event fired when fishing succeeds.
     */
    public static final Event<FishingSuccess> FISHING_SUCCESS = EventFactory.createArrayBacked(
            FishingSuccess.class, callbacks -> context -> {
                for (FishingSuccess callback : callbacks) {
                    callback.success(context);
                }
            }
    );

    /**
     * An informational event fired when fishing stops.
     */
    public static final Event<FishingStop> FISHING_STOP = EventFactory.createArrayBacked(
            FishingStop.class, callbacks -> context -> {
                for (FishingStop callback : callbacks) {
                    callback.stop(context);
                }
            }
    );

    /**
     * Functional interface for handling {@link #ENTITY_INTERACT} callbacks.
     */
    @FunctionalInterface
    public interface EntityInteract {

        /**
         * Called when a player interacts with an entity.
         *
         * @param player the player performing the interaction
         * @param level  the level in which the interaction occurs
         * @param hand   the hand used ({@code MAIN_HAND} or {@code OFF_HAND})
         * @param entity the target entity that was clicked
         * @return an {@link InteractionResult} indicating how the interaction should be handled
         */
        InteractionResult interact(Player player, Level level, InteractionHand hand, Entity entity);
    }

    /**
     * Functional interface for handling {@link #PLAYER_JOIN} callbacks.
     */
    @FunctionalInterface
    public interface PlayerJoin {

        /**
         * Called when a player joins the server.
         *
         * @param player the player who joined
         */
        void onPlayerJoin(ServerPlayer player);
    }

    /**
     * Functional interface for handling {@link #PLAYER_LEAVE} callbacks.
     */
    @FunctionalInterface
    public interface PlayerLeave {

        /**
         * Called when a player leaves the server.
         *
         * @param player the player who left
         */
        void onPlayerLeave(ServerPlayer player);
    }

    /**
     * Functional interface for handling {@link #CRAFT_ITEM} callbacks.
     */
    @FunctionalInterface
    public interface CraftItem {

        /**
         * Called when a player crafts an item.
         * <p>
         * This is an informational event only - you cannot cancel the crafting.
         * </p>
         *
         * @param player the player who crafted the item
         * @param craftedItems the list of item stacks that were crafted (including byproducts)
         */
        void onCraftItem(ServerPlayer player, List<ItemStack> craftedItems);
    }

    /**
     * Functional interface for handling {@link #PLAYER_RESPAWN} callbacks.
     */
    @FunctionalInterface
    public interface PlayerRespawn {

        /**
         * Called after a player respawns.
         *
         * @param oldPlayer the player before respawning
         * @param newPlayer the player after respawning
         * @param alive whether the player was alive before respawning (false if respawning from death)
         */
        void onPlayerRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive);
    }

    /**
     * Functional interface for handling {@link #SHIELD_BLOCK} callbacks.
     */
    @FunctionalInterface
    public interface ShieldBlock {

        /**
         * Called when a player blocks damage with a shield.
         * <p>
         * This is an informational event only - you cannot cancel the block.
         * </p>
         *
         * @param player the player who blocked with the shield
         * @param shield the shield ItemStack that blocked the damage
         * @param blockedDamage the amount of damage that was blocked
         * @param source the damage source that was blocked
         */
        void onShieldBlock(Player player, ItemStack shield, float blockedDamage, DamageSource source);
    }

    @FunctionalInterface
    public interface FishingStart {
        /**
         * Called when a player casts a fishing line.
         *
         * @param context the fishing context
         */
        void start(FishingContext context);
    }

    @FunctionalInterface
    public interface FishingBite {
        /**
         * Called when a fishing hook gets a bite.
         *
         * @param context the fishing context
         */
        void bite(FishingContext context);
    }

    @FunctionalInterface
    public interface FishingSuccess {
        /**
         * Called when fishing successfully catches something.
         *
         * @param context the success context
         */
        void success(FishingSuccessContext context);
    }

    @FunctionalInterface
    public interface FishingStop {
        /**
         * Called when fishing stops.
         *
         * @param context the stop context
         */
        void stop(FishingStopContext context);
    }

    public interface FishingContext {
        ServerPlayer getPlayer();

        ItemStack getFishingRod();

        FishingHook getFishingHook();

        Level getLevel();
    }

    public interface FishingSuccessContext extends FishingContext {
        FishingResult getResult();

        ItemStack getCaughtItem();

        @Nullable Entity getCaughtEntity();

        boolean isTreasure();

        int getExperienceValue();
    }

    public interface FishingStopContext extends FishingContext {
        FishingStopReason getReason();

        boolean wasSuccessful();
    }

    public enum FishingResult {
        FISH,
        TREASURE,
        JUNK,
        ENTITY,
        NOTHING
    }

    public enum FishingStopReason {
        REELED_IN,
        ROD_BROKE,
        HOOK_ESCAPED,
        TIME_OUT,
        PLAYER_LOGOUT
    }

    public record SimpleFishingContext(
            ServerPlayer player,
            ItemStack fishingRod,
            FishingHook fishingHook,
            Level level
    ) implements FishingContext {
        public SimpleFishingContext {
            fishingRod = fishingRod.copy();
        }

        @Override
        public ServerPlayer getPlayer() {
            return player;
        }

        @Override
        public ItemStack getFishingRod() {
            return fishingRod.copy();
        }

        @Override
        public FishingHook getFishingHook() {
            return fishingHook;
        }

        @Override
        public Level getLevel() {
            return level;
        }
    }

    public record SimpleFishingSuccessContext(
            ServerPlayer player,
            ItemStack fishingRod,
            FishingHook fishingHook,
            Level level,
            FishingResult result,
            ItemStack caughtItem,
            @Nullable Entity caughtEntity,
            boolean treasure,
            int experienceValue
    ) implements FishingSuccessContext {
        public SimpleFishingSuccessContext {
            fishingRod = fishingRod.copy();
            caughtItem = caughtItem.copy();
        }

        @Override
        public ServerPlayer getPlayer() {
            return player;
        }

        @Override
        public ItemStack getFishingRod() {
            return fishingRod.copy();
        }

        @Override
        public FishingHook getFishingHook() {
            return fishingHook;
        }

        @Override
        public Level getLevel() {
            return level;
        }

        @Override
        public FishingResult getResult() {
            return result;
        }

        @Override
        public ItemStack getCaughtItem() {
            return caughtItem.copy();
        }

        @Override
        public @Nullable Entity getCaughtEntity() {
            return caughtEntity;
        }

        @Override
        public boolean isTreasure() {
            return treasure;
        }

        @Override
        public int getExperienceValue() {
            return experienceValue;
        }
    }

    public record SimpleFishingStopContext(
            ServerPlayer player,
            ItemStack fishingRod,
            FishingHook fishingHook,
            Level level,
            FishingStopReason reason,
            boolean wasSuccessful
    ) implements FishingStopContext {
        public SimpleFishingStopContext {
            fishingRod = fishingRod.copy();
        }

        @Override
        public ServerPlayer getPlayer() {
            return player;
        }

        @Override
        public ItemStack getFishingRod() {
            return fishingRod.copy();
        }

        @Override
        public FishingHook getFishingHook() {
            return fishingHook;
        }

        @Override
        public Level getLevel() {
            return level;
        }

        @Override
        public FishingStopReason getReason() {
            return reason;
        }

        @Override
        public boolean wasSuccessful() {
            return wasSuccessful;
        }
    }
}
