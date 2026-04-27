package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.iamkaf.amber.api.event.v1.events.common.WorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for ServerLevel to provide world save events on Fabric.
 */
@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    static {
        AmberMod.AMBER_MIXINS.add("ServerLevelMixin");
    }

    @Inject(method = "save", at = @At("HEAD"), require = 1)
    private void onSave(CallbackInfo ci) {
        ServerLevel level = (ServerLevel) (Object) this;
        MinecraftServer server = level.getServer();
        if (server != null) {
            WorldEvents.WORLD_SAVE.invoker().onWorldSave(server, level);
        }
    }

    @Inject(method = "addFreshEntity", at = @At("HEAD"))
    private void onEntitySpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        // Fire the entity spawn event when entities are added to the world
        EntityEvent.ENTITY_SPAWN.invoker().onEntitySpawn(entity, (ServerLevel) (Object) this);
    }
}