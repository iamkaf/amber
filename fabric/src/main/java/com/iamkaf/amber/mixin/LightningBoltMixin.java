package com.iamkaf.amber.mixin;

import com.iamkaf.amber.api.event.v1.events.common.WeatherEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
//? if >=1.16
import net.minecraft.world.entity.LightningBolt;
//? if <1.16
/*import net.minecraft.world.entity.global.LightningBolt;*/
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixin for LightningBolt to provide lightning strike events on Fabric.
 * This intercepts the call to thunderHit() to fire an event and allow for cancellation.
 */
@Mixin(LightningBolt.class)
public abstract class LightningBoltMixin {

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    //? if >=1.16.2
                    target = "Lnet/minecraft/world/entity/Entity;thunderHit(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LightningBolt;)V"
                    //? if <1.16.2 && >=1.16
                    /*target = "Lnet/minecraft/world/entity/Entity;thunderHit(Lnet/minecraft/world/entity/LightningBolt;)V"*/
                    //? if <1.16
                    /*target = "Lnet/minecraft/world/entity/Entity;thunderHit(Lnet/minecraft/world/entity/global/LightningBolt;)V"*/
            )
    )
    //? if >=1.16.2
    private void onLightningStrike(Entity entity, ServerLevel level, LightningBolt lightning) {
    //? if <1.16.2
    /*private void onLightningStrike(Entity entity, LightningBolt lightning) {*/
        // Fire the custom event
        InteractionResult result = WeatherEvents.LIGHTNING_STRIKE.invoker().onLightningStrike(entity, lightning);

        // Only call the original thunderHit method if the event was not cancelled.
        // InteractionResult.PASS means the event was not handled and vanilla behavior should continue.
        if (result == InteractionResult.PASS) {
            //? if >=1.16.2
            entity.thunderHit(level, lightning);
            //? if <1.16.2
            /*entity.thunderHit(lightning);*/
        }
    }
}
