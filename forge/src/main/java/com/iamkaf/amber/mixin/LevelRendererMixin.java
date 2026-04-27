package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.client.RenderEvents;
//? if <1.21.2
/*import com.mojang.blaze3d.vertex.VertexConsumer;*/
import com.mojang.blaze3d.vertex.PoseStack;
//? if <1.21.9
/*import net.minecraft.client.Camera;*/
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
//? if >=26.1
import net.minecraft.client.renderer.state.level.LevelRenderState;
//? if >=1.21.9 && <26.1
/*import net.minecraft.client.renderer.state.LevelRenderState;*/
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
//? if <1.21.2
/*import net.minecraft.world.entity.Entity;*/
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
 * Mixin to inject into LevelRenderer to support the BLOCK_OUTLINE_RENDER event on Forge.
 * This replaces the RenderHighlightEvent.Block which changed its API in 1.21.9.
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
        //? if >=1.21.2
        method = "renderBlockOutline",
        //? if <1.21.2
        /*method = "renderHitOutline",*/
        at = @At("HEAD"),
        cancellable = true
    )
    private void onRenderBlockOutline(
            //? if <1.21.2 {
            /*PoseStack poseStack,
            VertexConsumer vertexConsumer,
            Entity entity,
            double cameraX,
            double cameraY,
            double cameraZ,
            BlockPos outlinePos,
            BlockState outlineState,
            *///?} else {
            //? if <1.21.9
            /*Camera camera,*/
            MultiBufferSource.BufferSource bufferSource,
            PoseStack poseStack,
            boolean translucentPass,
            //? if >=1.21.9
            LevelRenderState levelRenderState,
            //?}
            CallbackInfo ci
    ) {
        //? if <1.21.2 {
        /*if (!(this.minecraft.hitResult instanceof BlockHitResult blockHitResult)) {
            return;
        }

        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return;
        }

        InteractionResult result = RenderEvents.BLOCK_OUTLINE_RENDER.invoker().onBlockOutlineRender(
                this.minecraft.gameRenderer.getMainCamera(),
                this.minecraft.renderBuffers().bufferSource(),
                poseStack,
                blockHitResult,
                outlinePos,
                outlineState
        );

        if (result != InteractionResult.PASS) {
            ci.cancel();
        }
        *///?} else {
        // Get the block outline render state from levelRenderState
        //? if >=1.21.9 {
        if (levelRenderState.blockOutlineRenderState == null) {
            return;
        }
        //?}

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

        //? if >=1.21.9
        BlockPos pos = levelRenderState.blockOutlineRenderState.pos();
        //? if <1.21.9
        /*BlockPos pos = blockHitResult.getBlockPos();*/
        BlockState state = this.minecraft.level.getBlockState(pos);

        // Fire the Amber BLOCK_OUTLINE_RENDER event with full rendering context
        InteractionResult result = RenderEvents.BLOCK_OUTLINE_RENDER.invoker().onBlockOutlineRender(
            //? if >=1.21.9
            this.minecraft.gameRenderer.getMainCamera(),
            //? if <1.21.9
            /*camera,*/
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
        //?}
    }

    static {
        AmberMod.AMBER_MIXINS.add("LevelRendererMixin");
    }
}
