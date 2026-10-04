//? if <1.19.2 {
/*package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/^*
 * Lets entity interaction listeners cancel a plain right click. Fabric API checks its use entity
 * callback only for clicks at a hit position until Minecraft 1.19.2, and vanilla falls back to a
 * plain interaction when that one is cancelled.
 ^/
@Mixin(targets = "net.minecraft.server.network.ServerGamePacketListenerImpl$1")
public abstract class ServerEntityInteractionMixin {
    @Shadow
    @Final
    Entity val$target;

    @Shadow
    @Final
    ServerGamePacketListenerImpl field_28963;

    @Inject(method = "onInteraction(Lnet/minecraft/world/InteractionHand;)V", at = @At("HEAD"), cancellable = true)
    private void amber$cancelInteraction(InteractionHand hand, CallbackInfo ci) {
        ServerPlayer player = field_28963.player;
        if (PlayerEvents.ENTITY_INTERACT.invoker().interact(player, player.getLevel(), hand, val$target) != InteractionResult.PASS) {
            ci.cancel();
        }
    }

    static {
        AmberMod.AMBER_MIXINS.add("ServerEntityInteractionMixin");
    }
}
*///?}
