package com.iamkaf.amber.mixin;

import com.iamkaf.amber.api.event.v1.events.common.WeatherEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.global.LightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightningBolt.class)
public abstract class LightningBoltMixin {

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;thunderHit(Lnet/minecraft/world/entity/global/LightningBolt;)V"
            )
    )
    private void onLightningStrike(Entity entity, LightningBolt lightning) {
        InteractionResult result = WeatherEvents.LIGHTNING_STRIKE.invoker().onLightningStrike(entity, lightning);
        if (result == InteractionResult.PASS) {
            entity.thunderHit(lightning);
        }
    }
}
