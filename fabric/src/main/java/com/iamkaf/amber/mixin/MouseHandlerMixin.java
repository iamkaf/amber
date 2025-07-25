package com.iamkaf.amber.mixin;

import com.iamkaf.amber.api.event.v1.events.common.client.InputEvents;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    
    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long windowPointer, double scrollX, double scrollY, CallbackInfo ci) {
        MouseHandler mouseHandler = (MouseHandler) (Object) this;
        
        // Get the current mouse position
        double mouseX = mouseHandler.xpos();
        double mouseY = mouseHandler.ypos();
        
        // Fire the Amber mouse scroll event
        InteractionResult result = InputEvents.MOUSE_SCROLL.invoker().onMouseScroll(mouseX, mouseY, scrollX, scrollY);
        
        // Cancel the original scroll event if the Amber event was cancelled
        if (result != InteractionResult.PASS) {
            ci.cancel();
        }
    }
}