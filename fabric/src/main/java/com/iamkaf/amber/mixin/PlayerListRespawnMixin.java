package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
//? if <1.16
/*import net.minecraft.world.level.dimension.DimensionType;*/
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public abstract class PlayerListRespawnMixin {
    //? if >=1.16
    @Inject(method = "respawn", at = @At("RETURN"))
    private void onPlayerRespawn(ServerPlayer oldPlayer, boolean alive, CallbackInfoReturnable<ServerPlayer> cir) {
        PlayerEvents.PLAYER_RESPAWN.invoker().onPlayerRespawn(oldPlayer, cir.getReturnValue(), alive);
    }

    //? if <1.16
    /*@Inject(method = "respawn", at = @At("RETURN"))
    private void onPlayerRespawn(ServerPlayer oldPlayer, DimensionType dimension, boolean alive, CallbackInfoReturnable<ServerPlayer> cir) {
        PlayerEvents.PLAYER_RESPAWN.invoker().onPlayerRespawn(oldPlayer, cir.getReturnValue(), alive);
    }*/

    static {
        AmberMod.AMBER_MIXINS.add("PlayerListRespawnMixin");
    }
}
