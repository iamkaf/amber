package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.client.HudEvents;
//? if >=1.21
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
//? if >=26.1
import net.minecraft.client.gui.GuiGraphicsExtractor;
//? if <26.1
/*import net.minecraft.client.gui.GuiGraphics;*/
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Final
    @Shadow
    private Minecraft minecraft;

    /**
     * This method is called after the HUD is rendered, allowing us to inject custom rendering logic.
     * We check if the current screen is not a LevelLoadingScreen to avoid conflicts with loading screens.
     * This matches the Fabric code for HudRenderCallback, which is deprecated. I'll change to a more robust
     * solution if Forge ever provides a better way to handle HUD rendering events, or I roll my own.
     *
     * @param guiGraphics   the GUI graphics instance used for rendering
     * @param deltaTracker  the HUD tick counter
     * @param ci            the callback info
     */
    //? if >=26.1
    @Inject(method = "extractRenderState", at = @At("RETURN"))
    //? if <26.1
    /*@Inject(method = "render", at = @At("RETURN"), remap = false)*/
    //? if >=26.1
    public void amber$render(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
    //? if <26.1 && >=1.21
    /*public void amber$render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {*/
    //? if <1.21
    /*public void amber$render(GuiGraphics guiGraphics, float deltaTracker, CallbackInfo ci) {*/
        // this check mirrors the vanilla check
        if (this.minecraft.screen == null || !(this.minecraft.screen instanceof LevelLoadingScreen)) {
            HudEvents.RENDER_HUD.invoker().onHudRender(guiGraphics, deltaTracker);
        }
    }

    static {
        AmberMod.AMBER_MIXINS.add("GuiMixin");
    }
}
