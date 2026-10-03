package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.client.RenderEvents;
import com.iamkaf.amber.client.billboard.BillboardDraw;
import com.iamkaf.amber.client.billboard.ClientBillboards;
//? if <1.19.3
/*import com.mojang.math.Matrix4f;*/
//? if <1.21.2
/*import com.mojang.blaze3d.vertex.VertexConsumer;*/
import com.mojang.blaze3d.vertex.PoseStack;
//? if <1.21.9
/*import net.minecraft.client.Camera;*/
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
//? if <1.20.5 {
/*import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
*///?}
//? if >=1.21.9
import net.minecraft.client.renderer.SubmitNodeCollector;
//? if <26.2
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
//? if >=1.19.3 && <1.20.5
/*import org.joml.Matrix4f;*/
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

    // Forge before 1.19 reports block outlines through its highlight event instead.
    //? if >=1.19 && <26.2 {
    @Shadow
    @Final
    private Minecraft minecraft;
    //?}

    //? if >=1.19 {
    /**
     * Inject into renderBlockOutline at HEAD to fire event with full rendering context.
     * This matches the Fabric implementation for cross-platform consistency.
     */
    @Inject(
        //? if >=26.2
        method = "submitBlockOutline",
        //? if >=1.21.2 && <26.2
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
            *///?} else if >=26.2 {
            PoseStack poseStack,
            SubmitNodeCollector bufferSource,
            LevelRenderState levelRenderState,
            //?} else {
            //? if <1.21.9
            /*Camera camera,*/
            MultiBufferSource.BufferSource bufferSource,
            PoseStack poseStack,
            boolean translucentPass,
            //? if <1.21.9
            float partialTick,
            //? if >=1.21.9
            LevelRenderState levelRenderState,
            //?}
            CallbackInfo ci
    ) {
        //? if <1.21.2 {
        /*if (!(hitResult(this.minecraft) instanceof BlockHitResult blockHitResult)) {
            return;
        }

        if (hitResultType(blockHitResult) == HitResult.Type.MISS) {
            return;
        }

        InteractionResult result = RenderEvents.BLOCK_OUTLINE_RENDER.invoker().onBlockOutlineRender(
                mainCamera(this.minecraft),
                bufferSource(this.minecraft),
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
        //? if >=26.2 {
        if (levelRenderState.blockOutlineRenderState == null) {
            return;
        }
        //?} else if >=1.21.9 {
        if (levelRenderState.blockOutlineRenderState == null) {
            return;
        }
        //?}

        //? if >=26.1 && <26.2 {
        if (levelRenderState.blockOutlineRenderState.isTranslucent() != translucentPass) {
            return;
        }
        //?} else if <26.1 {
        /*if (translucentPass) {
            return;
        }
        *///?}

        //? if >=26.2
        Minecraft minecraft = Minecraft.getInstance();
        //? if <26.2
        /*Minecraft minecraft = this.minecraft;*/

        // Check if we have a block hit result
        if (!(minecraft.hitResult instanceof BlockHitResult blockHitResult)) {
            return;
        }

        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return;
        }

        //? if >=1.21.9
        BlockPos pos = levelRenderState.blockOutlineRenderState.pos();
        //? if <1.21.9
        /*BlockPos pos = blockHitResult.getBlockPos();*/
        BlockState state = minecraft.level.getBlockState(pos);

        // Fire the Amber BLOCK_OUTLINE_RENDER event with full rendering context
        InteractionResult result = RenderEvents.BLOCK_OUTLINE_RENDER.invoker().onBlockOutlineRender(
            //? if >=26.2
            minecraft.gameRenderer.mainCamera(),
            //? if >=1.21.9 && <26.2
            /*minecraft.gameRenderer.getMainCamera(),*/
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
    //?}

    //? if >=1.21.9 {
    @Inject(method = "submitEntities", at = @At("TAIL"))
    private void amber$submitBillboards(
            PoseStack poseStack,
            LevelRenderState levelRenderState,
            SubmitNodeCollector output,
            CallbackInfo ci
    ) {
        ClientBillboards.render(poseStack, new BillboardDraw(output, levelRenderState.cameraRenderState));
    }
    //?} else if >=1.20.5 {
    /*// Entities draw with a fresh pose stack here; the camera rotation is in the model-view matrix.
    //? if >=1.21.2
    @Inject(method = "renderEntities", at = @At("TAIL"))
    //? if <1.21.2
    /^@Inject(method = "renderLevel", at = @At(value = "CONSTANT", args = "stringValue=blockentities"))^/
    private void amber$renderBillboards(CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        BillboardDraw draw = new BillboardDraw(minecraft.renderBuffers().bufferSource(), minecraft.gameRenderer.getMainCamera());
        ClientBillboards.render(new PoseStack(), draw);
        // Flush before translucent terrain; vanilla does this itself from 1.21.2.
        //? if <1.21.2
        /^minecraft.renderBuffers().bufferSource().endLastBatch();^/
    }
    *///?} else {
    /*// After the entity pass and before block entities, like vanilla name tags.
    @Inject(method = "renderLevel", at = @At(value = "CONSTANT", args = "stringValue=blockentities"))
    private void amber$renderBillboards(
            PoseStack poseStack,
            float partialTick,
            long finishNanoTime,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightTexture lightTexture,
            Matrix4f projection,
            CallbackInfo ci
    ) {
        ClientBillboards.render(poseStack, new BillboardDraw(Minecraft.getInstance().renderBuffers().bufferSource(), camera));
        // Flush before translucent terrain so water and glass don't hide the last batch.
        Minecraft.getInstance().renderBuffers().bufferSource().endLastBatch();
    }
    *///?}

    //? if <1.21.2 {
    /*private static Camera mainCamera(Minecraft minecraft) {
        return minecraft.gameRenderer.getMainCamera();
    }

    private static MultiBufferSource bufferSource(Minecraft minecraft) {
        return minecraft.renderBuffers().bufferSource();
    }

    private static HitResult hitResult(Minecraft minecraft) {
        return minecraft.hitResult;
    }

    private static HitResult.Type hitResultType(HitResult hitResult) {
        return hitResult.getType();
    }
    *///?}

    static {
        AmberMod.AMBER_MIXINS.add("LevelRendererMixin");
    }
}
