package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.client.HudEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Final
    @Shadow
    private Minecraft minecraft;

    @Inject(method = "render", at = @org.spongepowered.asm.mixin.injection.At("TAIL"), remap = false)
    public void amber$render(PoseStack poseStack, float partialTick, CallbackInfo ci) {
        if (this.minecraft.screen == null || !(this.minecraft.screen instanceof ReceivingLevelScreen)) {
            HudEvents.RENDER_HUD.invoker().onHudRender(poseStack, partialTick);
        }
    }

    static {
        AmberMod.AMBER_MIXINS.add("GuiMixin");
    }
}
