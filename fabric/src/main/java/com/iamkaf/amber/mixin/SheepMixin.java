package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
//? if >=1.21.2 {
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
//?} else {
/*import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ItemLike;
*///?}
//? if >=26.1
import net.minecraft.world.item.ItemInstance;
//? if >=1.21.5
import net.minecraft.world.entity.animal.sheep.Sheep;
//? if <1.21.5
/*import net.minecraft.world.entity.animal.Sheep;*/
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheep.class)
public abstract class SheepMixin {
    @Unique
    private final List<ItemStack> amber$capturedShearDrops = new ArrayList<>();

    //? if >=1.21.2 {
    @WrapOperation(
            method = "shear",
            at = @At(
                    value = "INVOKE",
                    //? if >=26.1
                    target = "Lnet/minecraft/world/entity/animal/sheep/Sheep;dropFromShearingLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/item/ItemInstance;Ljava/util/function/BiConsumer;)V"
                    //? if <26.1 && >=1.21.5
                    /*target = "Lnet/minecraft/world/entity/animal/sheep/Sheep;dropFromShearingLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/item/ItemStack;Ljava/util/function/BiConsumer;)V"*/
                    //? if <1.21.5
                    /*target = "Lnet/minecraft/world/entity/animal/Sheep;dropFromShearingLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/item/ItemStack;Ljava/util/function/BiConsumer;)V"*/
            )
    )
    private void amber$captureShearDrops(Sheep sheep, ServerLevel level, ResourceKey<LootTable> lootTable,
            //? if >=26.1
            ItemInstance tool,
            //? if <26.1
            /*ItemStack tool,*/
            BiConsumer<ServerLevel, ItemStack> dropConsumer, Operation<Void> original) {
        original.call(sheep, level, lootTable, tool, (BiConsumer<ServerLevel, ItemStack>) (dropLevel, drop) -> {
            amber$capturedShearDrops.add(drop);
            dropConsumer.accept(dropLevel, drop);
        });
    }
    //?} else {
    /*@WrapOperation(
            method = "shear",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/Sheep;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;I)Lnet/minecraft/world/entity/item/ItemEntity;"
            )
    )
    private ItemEntity amber$captureShearDrops(Sheep sheep, ItemLike item, int offsetY, Operation<ItemEntity> original) {
        ItemEntity drop = original.call(sheep, item, offsetY);
        if (drop != null) {
            amber$capturedShearDrops.add(drop.getItem());
        }
        return drop;
    }
    *///?}

    @Inject(
            method = "mobInteract",
            at = @At(
                    value = "INVOKE",
                    //? if >=1.21.5
                    target = "Lnet/minecraft/world/entity/animal/sheep/Sheep;shear(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/world/item/ItemStack;)V",
                    //? if <1.21.5 && >=1.21.2
                    /*target = "Lnet/minecraft/world/entity/animal/Sheep;shear(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/world/item/ItemStack;)V",*/
                    //? if <1.21.2
                    /*target = "Lnet/minecraft/world/entity/animal/Sheep;shear(Lnet/minecraft/sounds/SoundSource;)V",*/
                    shift = At.Shift.BEFORE
            )
    )
    private void amber$beginShearCapture(Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        amber$capturedShearDrops.clear();
    }

    @Inject(
            method = "mobInteract",
            at = @At(
                    value = "INVOKE",
                    //? if >=1.21.5
                    target = "Lnet/minecraft/world/entity/animal/sheep/Sheep;shear(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/world/item/ItemStack;)V",
                    //? if <1.21.5 && >=1.21.2
                    /*target = "Lnet/minecraft/world/entity/animal/Sheep;shear(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/world/item/ItemStack;)V",*/
                    //? if <1.21.2
                    /*target = "Lnet/minecraft/world/entity/animal/Sheep;shear(Lnet/minecraft/sounds/SoundSource;)V",*/
                    shift = At.Shift.AFTER
            )
    )
    private void amber$fireShear(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Sheep sheep = (Sheep) (Object) this;
        ItemStack shears = player.getItemInHand(hand);
        if (!(amber$level(sheep) instanceof ServerLevel level)) {
            return;
        }

        EntityEvent.SHEAR.invoker().shear(
                new EntityEvent.SimpleShearingContext(
                        player instanceof ServerPlayer serverPlayer ? serverPlayer : null,
                        shears,
                        sheep,
                        level,
                        EntityEvent.ShearTarget.SHEEP,
                        amber$capturedShearDrops,
                        true,
                        EntityEvent.ShearSource.PLAYER
                )
        );
        amber$capturedShearDrops.clear();
    }

    private static Level amber$level(Sheep sheep) {
        //? if >=1.20
        return sheep.level();
        //? if <1.20
        /*return sheep.level;*/
    }

    static {
        AmberMod.AMBER_MIXINS.add("SheepMixin");
    }
}
