package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.IShearable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShearsItem.class)
public abstract class NeoForgeShearsItemMixin {
    @WrapOperation(
            method = "interactLivingEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/common/IShearable;onSheared(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Ljava/util/List;"
            )
    )
    private List<ItemStack> amber$onPlayerShear(
            IShearable shearable,
            Player player,
            ItemStack stack,
            Level level,
            BlockPos pos,
            Operation<List<ItemStack>> original
    ) {
        List<ItemStack> drops = original.call(shearable, player, stack, level, pos);

        if (!level.isClientSide() && shearable instanceof Entity entity) {
            EntityEvent.SHEAR.invoker().shear(
                    new EntityEvent.SimpleShearingContext(
                            player instanceof net.minecraft.server.level.ServerPlayer serverPlayer ? serverPlayer : null,
                            stack,
                            entity,
                            level,
                            amber$getShearTarget(entity),
                            drops,
                            !drops.isEmpty(),
                            EntityEvent.ShearSource.PLAYER
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
        AmberMod.AMBER_MIXINS.add("NeoForgeShearsItemMixin");
    }
}
