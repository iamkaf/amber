package com.iamkaf.amber.event;

import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
//? if >=1.21.11 {
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.monster.skeleton.Bogged;
//?} else {
/*import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.SnowGolem;
*///?}
//? if >=1.20.5 && <1.21.11
/*import net.minecraft.world.entity.monster.Bogged;*/
//? if >=1.21.9 && <1.21.11
/*import net.minecraft.world.entity.animal.coppergolem.CopperGolem;*/
//? if >=1.21.5
import net.minecraft.world.entity.animal.sheep.Sheep;
//? if <1.21.5
/*import net.minecraft.world.entity.animal.Sheep;*/

/**
 * Turns each successful use of shears on an entity into one {@link EntityEvent#SHEAR}.
 *
 * <p>Loader mixins open a use where shears can act on an entity: a player interacting with it, or a dispenser
 * firing. Inside a use they report the entity once something comes off it, and every item entity that joins the
 * world. The event fires with those items when the outermost use ends. A use lives within one game tick, so one
 * left open by an exception is dropped by the next.</p>
 */
public final class EntityShearing {
    private static final ThreadLocal<Use> CURRENT = new ThreadLocal<>();

    private EntityShearing() {
    }

    public static void beginPlayerUse(Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            begin(serverLevel(serverPlayer), serverPlayer, player.getItemInHand(hand), EntityEvent.ShearSource.PLAYER);
        }
    }

    public static void beginDispenserUse(ServerLevel level, ItemStack shears) {
        begin(level, null, shears, EntityEvent.ShearSource.DISPENSER);
    }

    /**
     * Reports that the open use sheared something off {@code target}. Later reports in the same use are ignored, so
     * hooks along one shearing path may each report it.
     */
    public static void sheared(Entity target) {
        Use use = CURRENT.get();
        if (use != null && use.target == null) {
            use.target = target;
        }
    }

    /** Records an item entity that joined {@code level} during the open use. */
    public static void spawned(ServerLevel level, Entity entity) {
        Use use = CURRENT.get();
        if (use == null || !(entity instanceof ItemEntity item)) {
            return;
        }
        if (use.gameTime != level.getGameTime()) {
            CURRENT.remove();
            return;
        }
        use.drops.add(item.getItem());
    }

    public static void end() {
        Use use = CURRENT.get();
        if (use == null || --use.depth > 0) {
            return;
        }
        CURRENT.remove();
        if (use.target == null) {
            return;
        }

        EntityEvent.SHEAR.invoker().shear(new EntityEvent.SimpleShearingContext(
                use.player,
                use.shears,
                use.target,
                use.level,
                targetType(use.target),
                use.drops,
                true,
                use.source
        ));
    }

    private static void begin(ServerLevel level, @Nullable ServerPlayer player, ItemStack shears,
            EntityEvent.ShearSource source) {
        Use use = CURRENT.get();
        if (use != null && use.gameTime == level.getGameTime()) {
            use.depth++;
            return;
        }
        CURRENT.set(new Use(level, player, shears, source));
    }

    private static EntityEvent.ShearTarget targetType(Entity entity) {
        if (entity instanceof Sheep) {
            return EntityEvent.ShearTarget.SHEEP;
        }
        if (entity instanceof MushroomCow) {
            return EntityEvent.ShearTarget.MUSHROOM_COW;
        }
        if (entity instanceof SnowGolem) {
            return EntityEvent.ShearTarget.SNOW_GOLEM;
        }
        //? if >=1.20.5 {
        if (entity instanceof Bogged) {
            return EntityEvent.ShearTarget.BOGGED;
        }
        //?}
        //? if >=1.21.9 {
        if (entity instanceof CopperGolem) {
            return EntityEvent.ShearTarget.COPPER_GOLEM;
        }
        //?}
        return EntityEvent.ShearTarget.OTHER;
    }

    private static ServerLevel serverLevel(ServerPlayer player) {
        //? if >=1.21.6
        return player.level();
        //? if >=1.20 && <1.21.6
        /*return player.serverLevel();*/
        //? if <1.20
        /*return player.getLevel();*/
    }

    private static final class Use {
        private final ServerLevel level;
        private final long gameTime;
        private final @Nullable ServerPlayer player;
        private final ItemStack shears;
        private final EntityEvent.ShearSource source;
        private final List<ItemStack> drops = new ArrayList<>();
        private @Nullable Entity target;
        private int depth = 1;

        private Use(ServerLevel level, @Nullable ServerPlayer player, ItemStack shears,
                EntityEvent.ShearSource source) {
            this.level = level;
            this.gameTime = level.getGameTime();
            this.player = player;
            this.shears = shears;
            this.source = source;
        }
    }
}
