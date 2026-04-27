package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.event.internal.CommonEventHooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelItemCaptureMixin {
    @Inject(method = "addFreshEntity", at = @At("HEAD"))
    private void amber$onAddFreshEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        CommonEventHooks.captureShearDrop(entity);
    }

    static {
        AmberMod.AMBER_MIXINS.add("ServerLevelItemCaptureMixin");
    }
}
