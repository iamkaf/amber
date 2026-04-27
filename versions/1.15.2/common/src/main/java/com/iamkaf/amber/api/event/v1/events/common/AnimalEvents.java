package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;

public class AnimalEvents {
    public static final Event<AnimalTame> ANIMAL_TAME = EventFactory.createArrayBacked(
            AnimalTame.class, callbacks -> (animal, player) -> {
                for (AnimalTame callback : callbacks) {
                    InteractionResult result = callback.onAnimalTame(animal, player);
                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }
                return InteractionResult.PASS;
            }
    );

    public static final Event<AnimalBreed> ANIMAL_BREED = EventFactory.createArrayBacked(
            AnimalBreed.class, callbacks -> (parentA, parentB, baby) -> {
                for (AnimalBreed callback : callbacks) {
                    callback.onAnimalBreed(parentA, parentB, baby);
                }
            }
    );

    @FunctionalInterface
    public interface AnimalTame {
        InteractionResult onAnimalTame(LivingEntity animal, Player player);
    }

    @FunctionalInterface
    public interface AnimalBreed {
        void onAnimalBreed(Animal parentA, Animal parentB, AgableMob baby);
    }
}
