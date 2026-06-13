package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.client.RenderEvents;
//? if <1.21.2 && >=1.15
/*import com.mojang.blaze3d.vertex.VertexConsumer;*/
//? if >=1.15
import com.mojang.blaze3d.vertex.PoseStack;
//? if <1.21.9
/*import net.minecraft.client.Camera;*/
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
//? if >=26.2-rc-2
import net.minecraft.client.renderer.SubmitNodeCollector;
//? if >=1.15 && <26.2-rc-2
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(
        //? if >=26.2-rc-2
        method = "submitBlockOutline",
        //? if >=1.21.2 && <26.2-rc-2
        method = "renderBlockOutline",
        //? if <1.21.2
        /*method = "renderHitOutline",*/
        at = @At("HEAD"),
        cancellable = true
    )
    private void onRenderBlockOutline(
            //? if <1.15 {
            /*Object poseStack,
            Object vertexConsumer,
            Entity entity,
            double cameraX,
            double cameraY,
            double cameraZ,
            BlockPos outlinePos,
            BlockState outlineState,
            *///?} else if <1.21.2 {
            /*PoseStack poseStack,
            VertexConsumer vertexConsumer,
            Entity entity,
            double cameraX,
            double cameraY,
            double cameraZ,
            BlockPos outlinePos,
            BlockState outlineState,
            *///?} else if >=26.2-rc-2 {
            PoseStack poseStack,
            SubmitNodeCollector bufferSource,
            LevelRenderState levelRenderState,
            //?} else {
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
        Minecraft minecraft = Minecraft.getInstance();

        //? if <1.21.2 {
        /*//? if >=1.15 {
        if (!(minecraft.hitResult instanceof BlockHitResult blockHitResult)) {
            return;
        }

        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return;
        }

        InteractionResult result = RenderEvents.BLOCK_OUTLINE_RENDER.invoker().onBlockOutlineRender(
                minecraft.gameRenderer.getMainCamera(),
                minecraft.renderBuffers().bufferSource(),
                (PoseStack) poseStack,
                blockHitResult,
                outlinePos,
                outlineState
        );

        if (result != InteractionResult.PASS) {
            ci.cancel();
        }
        //?}
        *///?} else {
        //? if >=26.2-rc-2 {
        if (levelRenderState.blockOutlineRenderState == null) {
            return;
        }
        //?} else if >=1.21.9 {
        if (levelRenderState.blockOutlineRenderState == null) {
            return;
        }
        //?}

        //? if >=26.1 && <26.2-rc-2 {
        if (levelRenderState.blockOutlineRenderState.isTranslucent() != translucentPass) {
            return;
        }
        //?}
        //? if <26.1 {
        /*if (translucentPass) {
            return;
        }
        *///?}

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

        InteractionResult result = RenderEvents.BLOCK_OUTLINE_RENDER.invoker().onBlockOutlineRender(
                //? if >=26.2-rc-2
                minecraft.gameRenderer.mainCamera(),
                //? if >=1.21.9 && <26.2-rc-2
                /*minecraft.gameRenderer.getMainCamera(),*/
                //? if <1.21.9
                /*camera,*/
                bufferSource,
                poseStack,
                blockHitResult,
                pos,
                state
        );

        if (result != InteractionResult.PASS) {
            ci.cancel();
        }
        //?}
    }

    static {
        AmberMod.AMBER_MIXINS.add("LevelRendererMixin");
    }
}
