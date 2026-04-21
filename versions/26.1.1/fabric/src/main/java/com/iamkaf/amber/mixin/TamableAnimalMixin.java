package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.AnimalEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TamableAnimal.class)
public abstract class TamableAnimalMixin {

    @Inject(method = "tame", at = @At("HEAD"), cancellable = true)
    private void onAnimalTame(Player player, CallbackInfo ci) {
        TamableAnimal animal = (TamableAnimal) (Object) this;

        InteractionResult result = AnimalEvents.ANIMAL_TAME.invoker().onAnimalTame(
                animal, player
        );

        if (result != InteractionResult.PASS) {
            ci.cancel();
        }
    }

    static {
        AmberMod.AMBER_MIXINS.add("TamableAnimalMixin");
    }
}
