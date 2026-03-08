package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.IShearable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShearsDispenseItemBehavior.class)
public abstract class NeoForgeShearsDispenseItemBehaviorMixin {
    @Redirect(
            method = "tryShearEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/common/IShearable;onSheared(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Ljava/util/List;"
            )
    )
    private static List<ItemStack> amber$onDispenseShear(
            IShearable shearable,
            Player player,
            ItemStack stack,
            Level level,
            BlockPos pos
    ) {
        List<ItemStack> drops = shearable.onSheared(player, stack, level, pos);

        if (shearable instanceof Entity entity) {
            EntityEvent.SHEAR.invoker().shear(
                    new EntityEvent.SimpleShearingContext(
                            null,
                            stack,
                            entity,
                            level,
                            amber$getShearTarget(entity),
                            drops,
                            !drops.isEmpty(),
                            EntityEvent.ShearSource.DISPENSER
                    )
            );
        }

        return drops;
    }

    private static EntityEvent.ShearTarget amber$getShearTarget(Entity entity) {
        return switch (entity.getClass().getName()) {
            case "net.minecraft.world.entity.animal.sheep.Sheep" -> EntityEvent.ShearTarget.SHEEP;
            case "net.minecraft.world.entity.animal.cow.MushroomCow" -> EntityEvent.ShearTarget.MUSHROOM_COW;
            case "net.minecraft.world.entity.animal.golem.SnowGolem" -> EntityEvent.ShearTarget.SNOW_GOLEM;
            case "net.minecraft.world.entity.monster.skeleton.Bogged" -> EntityEvent.ShearTarget.BOGGED;
            case "net.minecraft.world.entity.animal.golem.CopperGolem" -> EntityEvent.ShearTarget.COPPER_GOLEM;
            default -> EntityEvent.ShearTarget.OTHER;
        };
    }

    static {
        AmberMod.AMBER_MIXINS.add("NeoForgeShearsDispenseItemBehaviorMixin");
    }
}
