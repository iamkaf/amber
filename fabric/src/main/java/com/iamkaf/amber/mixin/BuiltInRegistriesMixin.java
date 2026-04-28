package com.iamkaf.amber.mixin;

import com.iamkaf.amber.platform.FabricDefaultItemComponentBridge;
//? if >=1.20.5
import net.minecraft.core.registries.BuiltInRegistries;
//? if <1.20.5
/*import net.minecraft.core.Registry;*/
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=1.20.5
@Mixin(BuiltInRegistries.class)
//? if <1.20.5
/*@Mixin(Registry.class)*/
public class BuiltInRegistriesMixin {
    //? if >=1.20.5 {
    @Inject(method = "freeze", at = @At("HEAD"))
    private static void amber$modifyDefaultItemComponents(CallbackInfo ci) {
        FabricDefaultItemComponentBridge.modifyItemComponents();
    }
    //?}
}
