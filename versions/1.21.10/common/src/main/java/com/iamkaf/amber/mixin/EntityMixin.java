package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.iamkaf.amber.event.internal.CommonEventHooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract int getRemainingFireTicks();

    @Shadow
    public abstract Level level();

    @Inject(method = "lavaIgnite", at = @At("HEAD"))
    private void amber$beforeLavaIgnite(CallbackInfo ci) {
        CommonEventHooks.pushEntityIgnitionSource(null, ItemStack.EMPTY, EntityEvent.EntityIgnitionSource.LAVA);
    }

    @Inject(method = "lavaIgnite", at = @At("RETURN"))
    private void amber$afterLavaIgnite(CallbackInfo ci) {
        CommonEventHooks.popEntityIgnitionSource();
    }

    @Inject(method = "thunderHit", at = @At("HEAD"))
    private void amber$beforeThunderHit(ServerLevel level, LightningBolt lightning, CallbackInfo ci) {
        CommonEventHooks.pushEntityIgnitionSource(null, ItemStack.EMPTY, EntityEvent.EntityIgnitionSource.LIGHTNING);
    }

    @Inject(method = "thunderHit", at = @At("RETURN"))
    private void amber$afterThunderHit(ServerLevel level, LightningBolt lightning, CallbackInfo ci) {
        CommonEventHooks.popEntityIgnitionSource();
    }

    @Inject(method = "igniteForTicks", at = @At("HEAD"))
    private void amber$onIgniteForTicks(int ticks, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (!entity.level().isClientSide() && ticks > 0 && this.getRemainingFireTicks() <= 0
                && CommonEventHooks.shouldFireEntityIgnitionOnIgnite()) {
            CommonEventHooks.fireEntityIgnite(entity);
        }
    }

    static {
        AmberMod.AMBER_MIXINS.add("EntityMixin");
    }
}
