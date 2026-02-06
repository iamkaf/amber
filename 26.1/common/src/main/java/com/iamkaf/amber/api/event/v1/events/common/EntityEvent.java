package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EntityEvent {
    /**
     * An event that is called when an entity spawns in the world. This is fired when entities are added to the world.
     */
    public static final Event<EntitySpawn> ENTITY_SPAWN = EventFactory.createArrayBacked(
            EntitySpawn.class, callbacks -> (entity, level) -> {
                for (EntitySpawn callback : callbacks) {
                    callback.onEntitySpawn(entity, level);
                }
            }
    );

    /**
     * An event that is called when a living entity dies. This is fired when entities are killed.
     */
    public static final Event<EntityDeath> ENTITY_DEATH = EventFactory.createArrayBacked(
            EntityDeath.class, callbacks -> (entity, source) -> {
                for (EntityDeath callback : callbacks) {
                    callback.onEntityDeath(entity, source);
                }
            }
    );

    /**
     * An event that is called when an entity takes damage. This is fired before damage is applied and can be used
     * to modify or cancel the damage.
     * 
     * <p>Return values:</p>
     * <ul>
     *     <li>PASS - Allow damage to proceed normally</li>
     *     <li>FAIL - Cancel the damage</li>
     *     <li>SUCCESS - Cancel the damage</li>
     *     <li>CONSUME - Cancel the damage</li>
     * </ul>
     */
    public static final Event<EntityDamage> ENTITY_DAMAGE = EventFactory.createArrayBacked(
            EntityDamage.class, callbacks -> (entity, source, amount) -> {
                for (EntityDamage callback : callbacks) {
                    InteractionResult result = callback.onEntityDamage(entity, source, amount);
                    if (result != InteractionResult.PASS) {
                        return result; // Early return for cancellation
                    }
                }
                return InteractionResult.PASS; // Allow damage by default
            }
    );

    /**
     * An event that is called after an entity is damaged. This is fired from {@link LivingEntity#hurt} after damage
     * is applied, or after that damage was blocked by a shield.
     *
     * <p>The base damage taken is the damage initially applied to the entity. Damage taken is the amount of damage the
     * entity actually took, after effects such as shields and extra freezing damage are applied. Damage taken does NOT
     * include damage reduction from armor and enchantments.
     *
     * <p>This event is not fired if the entity was killed by the damage.
     */
    public static final Event<AfterDamage> AFTER_DAMAGE = EventFactory.createArrayBacked(
            AfterDamage.class, callbacks -> (entity, source, baseDamageTaken, damageTaken, blocked) -> {
                for (AfterDamage callback : callbacks) {
                    callback.afterDamage(entity, source, baseDamageTaken, damageTaken, blocked);
                }
            }
    );

    @FunctionalInterface
    public interface EntitySpawn {
        /**
         * Called when an entity spawns in the world.
         *
         * @param entity the entity that spawned
         * @param level  the level/world where the entity spawned
         */
        void onEntitySpawn(Entity entity, Level level);
    }

    @FunctionalInterface
    public interface EntityDeath {
        /**
         * Called when a living entity dies.
         *
         * @param entity the entity that died
         * @param source the source of the damage that killed the entity
         */
        void onEntityDeath(LivingEntity entity, DamageSource source);
    }

    @FunctionalInterface
    public interface EntityDamage {
        /**
         * Called when an entity takes damage, before damage is applied.
         *
         * @param entity the entity that is taking damage
         * @param source the source of the damage
         * @param amount the amount of damage being dealt
         * @return an {@link InteractionResult} indicating whether the damage should be allowed or cancelled
         */
        InteractionResult onEntityDamage(LivingEntity entity, DamageSource source, float amount);
    }

    @FunctionalInterface
    public interface AfterDamage {
        /**
         * Called after a living entity took damage, unless they were killed. The base damage taken is given as damage
         * taken before armor or enchantments are applied, but after other effects like shields are applied.
         *
         * @param entity          the entity that was damaged
         * @param source          the source of the damage
         * @param baseDamageTaken the amount of damage initially dealt
         * @param damageTaken     the amount of damage actually taken by the entity, before armor and enchantment effects
         * @param blocked         whether the damage was blocked by a shield
         */
        void afterDamage(LivingEntity entity, DamageSource source, float baseDamageTaken, float damageTaken,
                boolean blocked);
    }
}
