package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(method = "addFreshEntity", at = @At("HEAD"))
    private void onEntitySpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        // Fire the entity spawn event when entities are added to the world
        EntityEvent.ENTITY_SPAWN.invoker().onEntitySpawn(entity, (ServerLevel) (Object) this);
    }

    static {
        AmberMod.AMBER_MIXINS.add("ServerLevelMixin");
    }
}