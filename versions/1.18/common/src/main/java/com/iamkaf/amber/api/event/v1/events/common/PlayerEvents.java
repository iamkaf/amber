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
     * entity’s own interaction logic is executed.
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

    public static final Event<PlayerJoin> PLAYER_JOIN = EventFactory.createArrayBacked(
            PlayerJoin.class, callbacks -> (player) -> {
                for (PlayerJoin callback : callbacks) {
                    callback.onPlayerJoin(player);
                }
            }
    );

    public static final Event<PlayerLeave> PLAYER_LEAVE = EventFactory.createArrayBacked(
            PlayerLeave.class, callbacks -> (player) -> {
                for (PlayerLeave callback : callbacks) {
                    callback.onPlayerLeave(player);
                }
            }
    );

    public static final Event<PlayerRespawn> PLAYER_RESPAWN = EventFactory.createArrayBacked(
            PlayerRespawn.class, callbacks -> (oldPlayer, newPlayer, alive) -> {
                for (PlayerRespawn callback : callbacks) {
                    callback.onPlayerRespawn(oldPlayer, newPlayer, alive);
                }
            }
    );

    public static final Event<CraftItem> CRAFT_ITEM = EventFactory.createArrayBacked(
            CraftItem.class, callbacks -> (player, craftedItems) -> {
                for (CraftItem callback : callbacks) {
                    callback.onCraftItem(player, craftedItems);
                }
            }
    );

    public static final Event<ShieldBlock> SHIELD_BLOCK = EventFactory.createArrayBacked(
        ShieldBlock.class, callbacks -> (player, shield, blockedDamage, source) -> {
            for (ShieldBlock callback : callbacks) {
                callback.onShieldBlock(player, shield, blockedDamage, source);
            }
        }
    );

    public static final Event<FishingStart> FISHING_START = EventFactory.createArrayBacked(
            FishingStart.class, callbacks -> context -> {
                for (FishingStart callback : callbacks) {
                    callback.start(context);
                }
            }
    );

    public static final Event<FishingBite> FISHING_BITE = EventFactory.createArrayBacked(
            FishingBite.class, callbacks -> context -> {
                for (FishingBite callback : callbacks) {
                    callback.bite(context);
                }
            }
    );

    public static final Event<FishingSuccess> FISHING_SUCCESS = EventFactory.createArrayBacked(
            FishingSuccess.class, callbacks -> context -> {
                for (FishingSuccess callback : callbacks) {
                    callback.success(context);
                }
            }
    );

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

    @FunctionalInterface
    public interface PlayerJoin {
        void onPlayerJoin(ServerPlayer player);
    }

    @FunctionalInterface
    public interface PlayerLeave {
        void onPlayerLeave(ServerPlayer player);
    }

    @FunctionalInterface
    public interface CraftItem {
        void onCraftItem(ServerPlayer player, List<ItemStack> craftedItems);
    }

    @FunctionalInterface
    public interface PlayerRespawn {
        void onPlayerRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive);
    }

    @FunctionalInterface
    public interface ShieldBlock {
        void onShieldBlock(Player player, ItemStack shield, float blockedDamage, DamageSource source);
    }

    public interface FishingContext {
        Player player();

        @Nullable FishingHook hook();

        Level level();

        ItemStack rod();
    }

    @FunctionalInterface
    public interface FishingStart {
        void start(FishingContext context);
    }

    @FunctionalInterface
    public interface FishingBite {
        void bite(FishingContext context);
    }

    public interface FishingSuccessContext extends FishingContext {
        List<ItemStack> drops();
    }

    @FunctionalInterface
    public interface FishingSuccess {
        void success(FishingSuccessContext context);
    }

    @FunctionalInterface
    public interface FishingStop {
        void stop(FishingContext context);
    }
}
