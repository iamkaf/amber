//? if >=1.19.1 && <1.21.6 {
/*package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import net.minecraftforge.client.ClientCommandHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >=1.19.3
import net.minecraft.client.multiplayer.ClientPacketListener;
//? if <1.19.3
//import net.minecraft.client.player.LocalPlayer;

/^*
 * Runs client commands from chat click events. Forge routes only typed commands to client commands
 * until 1.21.6, where it adds the same check to the click path.
 ^/
//? if >=1.19.3
@Mixin(ClientPacketListener.class)
//? if <1.19.3
//@Mixin(LocalPlayer.class)
public class ClientCommandClickMixin {
    //? if >=1.19.3
    @Inject(method = "sendUnsignedCommand", at = @At("HEAD"), cancellable = true)
    //? if <1.19.3
    //@Inject(method = "commandUnsigned", at = @At("HEAD"), cancellable = true)
    private void amber$runClientCommand(String command, CallbackInfoReturnable<Boolean> cir) {
        if (ClientCommandHandler.runCommand(command)) {
            cir.setReturnValue(true);
        }
    }

    static {
        AmberMod.AMBER_MIXINS.add("ClientCommandClickMixin");
    }
}
*///?}
