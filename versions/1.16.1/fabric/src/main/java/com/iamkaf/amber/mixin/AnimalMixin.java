package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.AnimalEvents;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Animal.class)
public abstract class AnimalMixin {

    @Inject(method = "spawnChildFromBreeding", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onAnimalBreed(Level level, Animal partner, CallbackInfo ci, AgableMob baby) {
        Animal parentA = (Animal) (Object) this;
        AnimalEvents.ANIMAL_BREED.invoker().onAnimalBreed(parentA, partner, baby);
    }

    static {
        AmberMod.AMBER_MIXINS.add("AnimalMixin");
    }
}
