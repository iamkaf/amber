package com.iamkaf.amber.mixin;

import com.iamkaf.amber.platform.FabricDefaultItemComponentBridge;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltInRegistries.class)
public class BuiltInRegistriesMixin {
    @Inject(method = "freeze", at = @At("HEAD"))
    private static void amber$modifyDefaultItemComponents(CallbackInfo ci) {
        FabricDefaultItemComponentBridge.modifyItemComponents();
    }
}
