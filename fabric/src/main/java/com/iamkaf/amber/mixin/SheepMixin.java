package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
//? if >=1.21.2
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//? if >=1.21.2
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//? if >=1.21.2
import java.util.ArrayList;
//? if >=1.21.2
import java.util.function.BiConsumer;
import java.util.List;
//? if >=1.21.2
import net.minecraft.resources.ResourceKey;
//? if >=1.18 {
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
//? if <1.21.2
/*import net.minecraft.world.item.DyeColor;*/
//? if <1.21.2
/*import net.minecraft.world.item.Items;*/
//? if >=26.1
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
//? if >=1.21.2
import net.minecraft.world.level.storage.loot.LootTable;
//?}
//? if >=1.21.5
import net.minecraft.world.entity.animal.sheep.Sheep;
//? if <1.21.5
/*import net.minecraft.world.entity.animal.Sheep;*/
import org.spongepowered.asm.mixin.Mixin;
//? if >=1.21.2
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
//? if >=1.18
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheep.class)
public abstract class SheepMixin {
    //? if >=1.21.2 {
    @Unique
    private final List<ItemStack> amber$capturedShearDrops = new ArrayList<>();

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
            amber$capturedShearDrops.add(drop.copy());
            dropConsumer.accept(dropLevel, drop);
        });
    }

    @Inject(
            method = "mobInteract",
            at = @At(
                    value = "INVOKE",
                    //? if >=1.21.5
                    target = "Lnet/minecraft/world/entity/animal/sheep/Sheep;shear(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/world/item/ItemStack;)V",
                    //? if <1.21.5
                    /*target = "Lnet/minecraft/world/entity/animal/Sheep;shear(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/world/item/ItemStack;)V",*/
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
                    //? if <1.21.5
                    /*target = "Lnet/minecraft/world/entity/animal/Sheep;shear(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/world/item/ItemStack;)V",*/
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
    //?}

    //? if <1.21.2 {
    /*@Inject(
            method = "mobInteract",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/Sheep;shear(Lnet/minecraft/sounds/SoundSource;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void amber$fireLegacyShear(Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
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
                        List.of(new ItemStack(amber$woolItem(sheep.getColor()))),
                        true,
                        EntityEvent.ShearSource.PLAYER
                )
        );
    }

    private static net.minecraft.world.item.Item amber$woolItem(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_WOOL;
            case ORANGE -> Items.ORANGE_WOOL;
            case MAGENTA -> Items.MAGENTA_WOOL;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_WOOL;
            case YELLOW -> Items.YELLOW_WOOL;
            case LIME -> Items.LIME_WOOL;
            case PINK -> Items.PINK_WOOL;
            case GRAY -> Items.GRAY_WOOL;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_WOOL;
            case CYAN -> Items.CYAN_WOOL;
            case PURPLE -> Items.PURPLE_WOOL;
            case BLUE -> Items.BLUE_WOOL;
            case BROWN -> Items.BROWN_WOOL;
            case GREEN -> Items.GREEN_WOOL;
            case RED -> Items.RED_WOOL;
            case BLACK -> Items.BLACK_WOOL;
        };
    }
    *///?}

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
