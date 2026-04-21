package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.event.internal.CommonEventHooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.coppergolem.CopperGolem;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.monster.Bogged;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Sheep.class, MushroomCow.class, SnowGolem.class, Bogged.class, CopperGolem.class})
public abstract class ShearableEntityMixin {
    @Inject(method = "shear", at = @At("HEAD"))
    private void amber$beforeShear(ServerLevel level, SoundSource source, ItemStack shears, CallbackInfo ci) {
        CommonEventHooks.beginShearCapture((Entity) (Object) this);
    }

    @Inject(method = "shear", at = @At("RETURN"))
    private void amber$afterShear(ServerLevel level, SoundSource source, ItemStack shears, CallbackInfo ci) {
        CommonEventHooks.finishShearCapture((Entity) (Object) this, level);
    }

    static {
        AmberMod.AMBER_MIXINS.add("ShearableEntityMixin");
    }
}
