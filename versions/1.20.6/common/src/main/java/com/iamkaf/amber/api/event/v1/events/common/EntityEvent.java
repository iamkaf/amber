package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntityEvent {
    public static final Event<EntitySpawn> ENTITY_SPAWN = EventFactory.createArrayBacked(
            EntitySpawn.class, callbacks -> (entity, level) -> {
                for (EntitySpawn callback : callbacks) {
                    callback.onEntitySpawn(entity, level);
                }
            }
    );

    public static final Event<EntityDeath> ENTITY_DEATH = EventFactory.createArrayBacked(
            EntityDeath.class, callbacks -> (entity, source) -> {
                for (EntityDeath callback : callbacks) {
                    callback.onEntityDeath(entity, source);
                }
            }
    );

    public static final Event<EntityDamage> ENTITY_DAMAGE = EventFactory.createArrayBacked(
            EntityDamage.class, callbacks -> (entity, source, amount) -> {
                for (EntityDamage callback : callbacks) {
                    InteractionResult result = callback.onEntityDamage(entity, source, amount);
                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }
                return InteractionResult.PASS;
            }
    );

    public static final Event<AfterDamage> AFTER_DAMAGE = EventFactory.createArrayBacked(
            AfterDamage.class, callbacks -> (entity, source, baseDamageTaken, damageTaken, blocked) -> {
                for (AfterDamage callback : callbacks) {
                    callback.afterDamage(entity, source, baseDamageTaken, damageTaken, blocked);
                }
            }
    );

    public static final Event<Shear> SHEAR = EventFactory.createArrayBacked(
            Shear.class, callbacks -> context -> {
                for (Shear callback : callbacks) {
                    callback.shear(context);
                }
            }
    );

    @FunctionalInterface
    public interface EntitySpawn {
        void onEntitySpawn(Entity entity, Level level);
    }

    @FunctionalInterface
    public interface EntityDeath {
        void onEntityDeath(LivingEntity entity, DamageSource source);
    }

    @FunctionalInterface
    public interface EntityDamage {
        InteractionResult onEntityDamage(LivingEntity entity, DamageSource source, float amount);
    }

    @FunctionalInterface
    public interface AfterDamage {
        void afterDamage(LivingEntity entity, DamageSource source, float baseDamageTaken, float damageTaken, boolean blocked);
    }

    @FunctionalInterface
    public interface Shear {
        void shear(ShearingContext context);
    }

    public interface ShearingContext {
        @Nullable net.minecraft.server.level.ServerPlayer getPlayer();

        ItemStack getShears();

        Entity getTargetEntity();

        Level getLevel();

        ShearTarget getTargetType();

        List<ItemStack> getDrops();

        boolean wasSuccessful();

        ShearSource getSource();
    }

    public enum ShearTarget {
        SHEEP,
        MUSHROOM_COW,
        SNOW_GOLEM,
        BOGGED,
        COPPER_GOLEM,
        OTHER
    }

    public enum ShearSource {
        PLAYER,
        DISPENSER,
        AUTOMATION,
        UNKNOWN
    }

    public record SimpleShearingContext(
            @Nullable net.minecraft.server.level.ServerPlayer player,
            ItemStack shears,
            Entity targetEntity,
            Level level,
            ShearTarget targetType,
            List<ItemStack> drops,
            boolean successful,
            ShearSource source
    ) implements ShearingContext {
        public SimpleShearingContext {
            shears = shears.copy();
            drops = drops.stream().map(ItemStack::copy).toList();
        }

        @Override
        public @Nullable net.minecraft.server.level.ServerPlayer getPlayer() {
            return this.player;
        }

        @Override
        public ItemStack getShears() {
            return this.shears.copy();
        }

        @Override
        public Entity getTargetEntity() {
            return this.targetEntity;
        }

        @Override
        public Level getLevel() {
            return this.level;
        }

        @Override
        public ShearTarget getTargetType() {
            return this.targetType;
        }

        @Override
        public List<ItemStack> getDrops() {
            return this.drops.stream().map(ItemStack::copy).toList();
        }

        @Override
        public boolean wasSuccessful() {
            return this.successful;
        }

        @Override
        public ShearSource getSource() {
            return this.source;
        }
    }
}
