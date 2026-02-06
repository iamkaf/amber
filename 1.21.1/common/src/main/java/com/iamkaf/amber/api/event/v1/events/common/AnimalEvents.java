package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;

/**
 * Events related to animal and mob interactions in Minecraft.
 */
public class AnimalEvents {

    /**
     * An event that is fired when an animal is being tamed.
     * <p>
     * This event is fired before the animal is tamed and can be used to modify or cancel the taming.
     * </p>
     * <p>
     * <b>Fabric Note:</b> On Fabric, rapid clicking may allow tamable animals (e.g., wolves) to enter
     * sitting pose before taming completes. This is due to timing of vanilla interaction handlers.
     * Forge and NeoForge handle this correctly via native events.
     * </p>
     *
     * <p>Return values:</p>
     * <ul>
     *     <li>PASS - Allow taming to proceed normally</li>
     *     <li>FAIL - Cancel the taming</li>
     *     <li>SUCCESS - Cancel vanilla taming (custom taming applied)</li>
     *     <li>CONSUME - Cancel vanilla taming (custom taming applied)</li>
     * </ul>
     */
    public static final Event<AnimalTame> ANIMAL_TAME = EventFactory.createArrayBacked(
            AnimalTame.class, callbacks -> (animal, player) -> {
                for (AnimalTame callback : callbacks) {
                    InteractionResult result = callback.onAnimalTame(animal, player);
                    if (result != InteractionResult.PASS) {
                        return result; // Early return for cancellation or custom behavior
                    }
                }
                return InteractionResult.PASS; // Allow taming by default
            }
    );

    /**
     * An event that is fired when a baby animal is spawned from breeding.
     * <p>
     * This event is informational and fires after the baby animal is created.
     * It cannot be cancelled.
     * </p>
     * <p>
     * <b>Note:</b> On Fabric and NeoForge, this event is implemented via Mixins as there are no
     * native events for animal breeding. On Forge, this uses the native {@code BabyEntitySpawnEvent}.
     * </p>
     */
    public static final Event<AnimalBreed> ANIMAL_BREED = EventFactory.createArrayBacked(
            AnimalBreed.class, callbacks -> (parentA, parentB, baby) -> {
                for (AnimalBreed callback : callbacks) {
                    callback.onAnimalBreed(parentA, parentB, baby);
                }
            }
    );

    /**
     * Functional interface for handling {@link #ANIMAL_TAME} callbacks.
     */
    @FunctionalInterface
    public interface AnimalTame {
        /**
         * Called when an animal is being tamed.
         *
         * @param animal the animal being tamed
         * @param player the player taming the animal
         * @return an {@link InteractionResult} indicating whether the taming should proceed
         */
        InteractionResult onAnimalTame(LivingEntity animal, Player player);
    }

    /**
     * Functional interface for handling {@link #ANIMAL_BREED} callbacks.
     */
    @FunctionalInterface
    public interface AnimalBreed {
        /**
         * Called when a baby animal is spawned from breeding.
         *
         * @param parentA the first parent animal
         * @param parentB the second parent animal
         * @param baby    the baby animal that was spawned
         */
        void onAnimalBreed(Animal parentA, Animal parentB, AgeableMob baby);
    }
}
