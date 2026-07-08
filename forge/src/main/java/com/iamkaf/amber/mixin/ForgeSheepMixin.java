package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

//? if >=1.21.5
import net.minecraft.world.entity.animal.sheep.Sheep;
//? if <1.21.5 && >=1.17
/*import net.minecraft.world.entity.animal.Sheep;*/
//? if <1.17
/*import net.minecraft.entity.passive.SheepEntity;*/

//? if <26.2 {
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
//?}
//? if >=26.2 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.storage.loot.LootTable;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >=1.17
@Mixin(Sheep.class)
//? if <1.17
/*@Mixin(SheepEntity.class)*/
public abstract class ForgeSheepMixin {
    //? if >=26.2 {
    @Unique
    private final List<ItemStack> amber$capturedShearDrops = new ArrayList<>();

    @Unique
    private ServerPlayer amber$shearingPlayer;

    @Inject(
            method = "mobInteract",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/sheep/Sheep;shear(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/world/item/ItemStack;)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void amber$captureShearingPlayer(Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        amber$shearingPlayer = player instanceof ServerPlayer serverPlayer ? serverPlayer : null;
    }

    @Inject(method = "shear", at = @At("HEAD"))
    private void amber$beginShearCapture(ServerLevel level, SoundSource soundSource, ItemStack tool, CallbackInfo ci) {
        amber$capturedShearDrops.clear();
    }

    @WrapOperation(
            method = "shear",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/sheep/Sheep;dropFromShearingLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/item/ItemInstance;Ljava/util/function/BiConsumer;)V"
            )
    )
    private void amber$captureShearDrops(Sheep sheep, ServerLevel level, ResourceKey<LootTable> lootTable,
            ItemInstance tool, BiConsumer<ServerLevel, ItemStack> dropConsumer, Operation<Void> original) {
        original.call(sheep, level, lootTable, tool,
                (BiConsumer<ServerLevel, ItemStack>) (dropLevel, drop) -> {
                    amber$capturedShearDrops.add(drop);
                    dropConsumer.accept(dropLevel, drop);
                });
    }

    @Inject(method = "shear", at = @At("RETURN"))
    private void amber$fireShear(ServerLevel level, SoundSource soundSource, ItemStack tool, CallbackInfo ci) {
        EntityEvent.SHEAR.invoker().shear(
                new EntityEvent.SimpleShearingContext(
                        amber$shearingPlayer,
                        tool,
                        (Sheep) (Object) this,
                        level,
                        EntityEvent.ShearTarget.SHEEP,
                        amber$capturedShearDrops,
                        !amber$capturedShearDrops.isEmpty(),
                        soundSource == SoundSource.PLAYERS
                                ? EntityEvent.ShearSource.PLAYER
                                : EntityEvent.ShearSource.DISPENSER
                )
        );
        amber$capturedShearDrops.clear();
        amber$shearingPlayer = null;
    }
    //?}

    //? if <26.2 {
    @Inject(
            method = "onSheared",
            at = @At("RETURN")
    )
    private void amber$fireShear(Player player, ItemStack stack, Level level, BlockPos pos, int fortune,
            CallbackInfoReturnable<List<ItemStack>> cir) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        List<ItemStack> drops = cir.getReturnValue();
        EntityEvent.SHEAR.invoker().shear(
                new EntityEvent.SimpleShearingContext(
                        player instanceof ServerPlayer serverPlayer ? serverPlayer : null,
                        stack,
                        //? if >=1.17
                        (Sheep) (Object) this,
                        //? if <1.17
                        /*(SheepEntity) (Object) this,*/
                        serverLevel,
                        EntityEvent.ShearTarget.SHEEP,
                        drops,
                        !drops.isEmpty(),
                        player == null ? EntityEvent.ShearSource.DISPENSER : EntityEvent.ShearSource.PLAYER
                )
        );
    }
    //?}

    static {
        AmberMod.AMBER_MIXINS.add("ForgeSheepMixin");
    }
}
