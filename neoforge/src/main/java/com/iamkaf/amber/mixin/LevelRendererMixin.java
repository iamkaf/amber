package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.client.RenderEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
//? if >=26.1
import net.minecraft.client.renderer.state.level.LevelRenderState;
//? if <26.1
/*import net.minecraft.client.renderer.state.LevelRenderState;*/
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to inject into LevelRenderer to support the BLOCK_OUTLINE_RENDER event on NeoForge.
 * This replaces the ExtractBlockOutlineRenderStateEvent which was not working correctly.
 *
 * Injects into renderBlockOutline to provide full access to PoseStack and MultiBufferSource
 * during the actual render phase.
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    /**
     * Inject into renderBlockOutline at HEAD to fire event with full rendering context.
     * This matches the Fabric implementation for cross-platform consistency.
     */
    @Inject(
        method = "renderBlockOutline",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onRenderBlockOutline(
            MultiBufferSource.BufferSource bufferSource,
            PoseStack poseStack,
            boolean translucentPass,
            LevelRenderState levelRenderState,
            CallbackInfo ci
    ) {
        // Get the block outline render state from levelRenderState
        if (levelRenderState.blockOutlineRenderState == null) {
            return;
        }

        //? if >=26.1 {
        if (levelRenderState.blockOutlineRenderState.isTranslucent() != translucentPass) {
            return;
        }
        //?} else {
        /*if (translucentPass) {
            return;
        }
        *///?}

        // Check if we have a block hit result
        if (!(this.minecraft.hitResult instanceof BlockHitResult blockHitResult)) {
            return;
        }

        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return;
        }

        BlockPos pos = levelRenderState.blockOutlineRenderState.pos();
        BlockState state = this.minecraft.level.getBlockState(pos);

        // Fire the Amber BLOCK_OUTLINE_RENDER event with full rendering context
        InteractionResult result = RenderEvents.BLOCK_OUTLINE_RENDER.invoker().onBlockOutlineRender(
            this.minecraft.gameRenderer.getMainCamera(),
            bufferSource,
            poseStack,
            blockHitResult,
            pos,
            state
        );

        // Cancel vanilla rendering if event was not PASS
        if (result != InteractionResult.PASS) {
            ci.cancel();
        }
    }

    static {
        AmberMod.AMBER_MIXINS.add("LevelRendererMixin");
    }
}
