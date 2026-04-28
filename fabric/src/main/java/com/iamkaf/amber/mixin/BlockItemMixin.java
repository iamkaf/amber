package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.BlockEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
//? if >=1.16.2
import net.minecraft.world.item.context.BlockPlaceContext;
//? if <1.16.2
/*import net.minecraft.world.item.BlockPlaceContext;*/
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(method = "place",
            at = @At(value = "INVOKE",
                    //? if >=1.16.2
                    target = "Lnet/minecraft/world/item/BlockItem;placeBlock(Lnet/minecraft/world/item/context/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z"),
                    //? if <1.16.2
                    /*target = "Lnet/minecraft/world/item/BlockItem;placeBlock(Lnet/minecraft/world/item/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z"),*/
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true)
    private void onPlace(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir,
                         BlockPlaceContext var2, BlockState var3) {
        // Only fire on server side to match Forge/NeoForge behavior
        if (var2.getLevel().isClientSide()) {
            return;
        }

        // Fire the unified BLOCK_PLACE event
        InteractionResult result = BlockEvents.BLOCK_PLACE.invoker().onBlockPlace(
            var2.getLevel(),
            var2.getPlayer(),
            var2.getClickedPos(),
            var3,
            var2.getItemInHand()
        );

        if (result != InteractionResult.PASS) {
            // Cancel the placement
            cir.setReturnValue(result);
        }
    }

    static {
        AmberMod.AMBER_MIXINS.add("BlockItemMixin");
    }
}
