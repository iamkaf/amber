package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.iamkaf.amber.event.internal.CommonEventHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseFireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseFireBlock.class)
public abstract class BaseFireBlockMixin {
    @Inject(method = "fireIgnite", at = @At("HEAD"))
    private static void amber$beforeFireIgnite(Entity entity, CallbackInfo ci) {
        CommonEventHooks.pushEntityIgnitionSource(null, ItemStack.EMPTY, EntityEvent.EntityIgnitionSource.FIRE_SPREAD);
    }

    @Inject(method = "fireIgnite", at = @At("RETURN"))
    private static void amber$afterFireIgnite(Entity entity, CallbackInfo ci) {
        CommonEventHooks.popEntityIgnitionSource();
    }

    static {
        AmberMod.AMBER_MIXINS.add("BaseFireBlockMixin");
    }
}
