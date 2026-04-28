package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.AnimalEvents;
import net.minecraft.server.level.ServerLevel;
//? if >=1.17
import net.minecraft.world.entity.AgeableMob;
//? if <1.17
/*import net.minecraft.world.entity.AgableMob;*/
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Animal.class)
public abstract class AnimalMixin {

    @Inject(method = "spawnChildFromBreeding", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    //? if >=1.17
    private void onAnimalBreed(ServerLevel level, Animal partner, CallbackInfo ci, AgeableMob baby) {
    //? if <1.17
    /*private void onAnimalBreed(ServerLevel level, Animal partner, CallbackInfo ci, AgableMob baby) {*/
        Animal parentA = (Animal) (Object) this;

        AnimalEvents.ANIMAL_BREED.invoker().onAnimalBreed(
                parentA, partner, baby
        );
    }

    static {
        AmberMod.AMBER_MIXINS.add("AnimalMixin");
    }
}
