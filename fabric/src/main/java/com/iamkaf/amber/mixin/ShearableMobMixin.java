package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.event.EntityShearing;
import net.minecraft.world.entity.Entity;
//? if >=1.21.11 {
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.monster.skeleton.Bogged;
//?} else {
/*import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.SnowGolem;
*///?}
//? if >=1.20.5 && <1.21.11
/*import net.minecraft.world.entity.monster.Bogged;*/
//? if >=1.21.9 && <1.21.11
/*import net.minecraft.world.entity.animal.coppergolem.CopperGolem;*/
//? if >=26.2
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
//? if >=1.21.5
import net.minecraft.world.entity.animal.sheep.Sheep;
//? if <1.21.5
/*import net.minecraft.world.entity.animal.Sheep;*/
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Reports the vanilla shearable mobs. Players and dispensers both shear them through {@code shear}.
 */
//? if >=26.2
@Mixin({Sheep.class, MushroomCow.class, SnowGolem.class, Bogged.class, CopperGolem.class, SulfurCube.class})
//? if >=1.21.9 && <26.2
/*@Mixin({Sheep.class, MushroomCow.class, SnowGolem.class, Bogged.class, CopperGolem.class})*/
//? if >=1.20.5 && <1.21.9
/*@Mixin({Sheep.class, MushroomCow.class, SnowGolem.class, Bogged.class})*/
//? if <1.20.5
/*@Mixin({Sheep.class, MushroomCow.class, SnowGolem.class})*/
public abstract class ShearableMobMixin {
    @Inject(method = "shear", at = @At("HEAD"))
    private void amber$reportShear(CallbackInfo ci) {
        EntityShearing.sheared((Entity) (Object) this);
    }

    static {
        AmberMod.AMBER_MIXINS.add("ShearableMobMixin");
    }
}
