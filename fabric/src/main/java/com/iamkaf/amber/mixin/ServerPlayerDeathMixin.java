package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
//? if <1.19.2 {
/*import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?}
//? if >=1.19.2
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayer.class)
public class ServerPlayerDeathMixin {
    //? if <1.19.2 {
    /*@Inject(
            method = "die",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V"
            )
    )
    private void amber$firePlayerDeath(DamageSource source, CallbackInfo ci) {
        EntityEvent.ENTITY_DEATH.invoker().onEntityDeath((ServerPlayer) (Object) this, source);
    }
    *///?}

    static {
        AmberMod.AMBER_MIXINS.add("ServerPlayerDeathMixin");
    }
}
