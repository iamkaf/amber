//? if <1.21.5 {
/*package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.neoforged.neoforge.client.ClientCommandHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/^*
 * Runs client commands from chat click events. NeoForge routes only typed commands to client
 * commands until 21.5, where it adds the same check to the click path.
 ^/
@Mixin(ClientPacketListener.class)
public class ClientCommandClickMixin {
    @Inject(method = "sendUnsignedCommand", at = @At("HEAD"), cancellable = true)
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
