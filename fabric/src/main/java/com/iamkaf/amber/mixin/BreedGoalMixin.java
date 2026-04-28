package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.AnimalEvents;
//? if >=1.17
import net.minecraft.world.entity.AgeableMob;
//? if <1.17
/*import net.minecraft.world.entity.AgableMob;*/
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BreedGoal.class)
public abstract class BreedGoalMixin {
    @Shadow
    @Final
    protected Animal animal;

    @Shadow
    protected Animal partner;

    @Inject(
            method = "breed",
            at = @At(
                    value = "INVOKE",
                    //? if >=1.16.2
                    target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"
                    //? if <1.16.2
                    /*target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"*/
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    //? if >=1.17
    private void onAnimalBreed(CallbackInfo ci, AgeableMob baby) {
    //? if <1.17
    /*private void onAnimalBreed(CallbackInfo ci, AgableMob baby) {*/
        AnimalEvents.ANIMAL_BREED.invoker().onAnimalBreed(animal, partner, baby);
    }

    static {
        AmberMod.AMBER_MIXINS.add("BreedGoalMixin");
    }
}
