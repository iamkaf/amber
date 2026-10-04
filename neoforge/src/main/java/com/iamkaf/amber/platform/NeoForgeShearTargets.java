package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import net.minecraft.world.entity.Entity;
//? if >=1.21.11 {
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.monster.skeleton.Bogged;
//?} else {
/*import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Bogged;
*///?}
//? if >=1.21.9 && <1.21.11
/*import net.minecraft.world.entity.animal.coppergolem.CopperGolem;*/
//? if >=1.21.5
import net.minecraft.world.entity.animal.sheep.Sheep;
//? if <1.21.5
/*import net.minecraft.world.entity.animal.Sheep;*/

/**
 * Classifies a sheared entity for {@link EntityEvent#SHEAR}.
 */
public final class NeoForgeShearTargets {
    private NeoForgeShearTargets() {
    }

    public static EntityEvent.ShearTarget of(Entity entity) {
        if (entity instanceof Sheep) {
            return EntityEvent.ShearTarget.SHEEP;
        }
        if (entity instanceof MushroomCow) {
            return EntityEvent.ShearTarget.MUSHROOM_COW;
        }
        if (entity instanceof SnowGolem) {
            return EntityEvent.ShearTarget.SNOW_GOLEM;
        }
        if (entity instanceof Bogged) {
            return EntityEvent.ShearTarget.BOGGED;
        }
        //? if >=1.21.9 {
        if (entity instanceof CopperGolem) {
            return EntityEvent.ShearTarget.COPPER_GOLEM;
        }
        //?}
        return EntityEvent.ShearTarget.OTHER;
    }
}
