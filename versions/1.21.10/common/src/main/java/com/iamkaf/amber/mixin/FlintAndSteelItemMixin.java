package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.BlockEvents;
import com.iamkaf.amber.event.internal.CommonEventHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlintAndSteelItem.class)
public abstract class FlintAndSteelItemMixin {
    @Inject(method = "useOn", at = @At("RETURN"))
    private void amber$onUseOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        if (level.isClientSide() || !cir.getReturnValue().consumesAction()) {
            return;
        }

        ItemStack ignitionItem = new ItemStack((Item) (Object) this);
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);
        if (this.amber$isIgnitedState(clickedState)) {
            CommonEventHooks.fireBlockIgnite(
                    level,
                    clickedPos,
                    clickedState,
                    context.getPlayer(),
                    ignitionItem,
                    BlockEvents.BlockIgnitionSource.FLINT_AND_STEEL
            );
            return;
        }

        BlockPos adjacentPos = clickedPos.relative(context.getClickedFace());
        BlockState adjacentState = level.getBlockState(adjacentPos);
        if (this.amber$isIgnitedState(adjacentState)) {
            CommonEventHooks.fireBlockIgnite(
                    level,
                    adjacentPos,
                    adjacentState,
                    context.getPlayer(),
                    ignitionItem,
                    BlockEvents.BlockIgnitionSource.FLINT_AND_STEEL
            );
        }
    }

    private boolean amber$isIgnitedState(BlockState state) {
        return state.getBlock() instanceof BaseFireBlock
                || state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT);
    }

    static {
        AmberMod.AMBER_MIXINS.add("FlintAndSteelItemMixin");
    }
}
