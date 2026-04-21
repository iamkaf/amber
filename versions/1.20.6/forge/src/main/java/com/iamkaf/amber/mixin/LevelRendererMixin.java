package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.client.RenderEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "renderHitOutline", at = @At("HEAD"), cancellable = true)
    private void onRenderBlockOutline(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            Entity entity,
            double cameraX,
            double cameraY,
            double cameraZ,
            BlockPos pos,
            BlockState state,
            CallbackInfo ci
    ) {
        if (!(this.minecraft.hitResult instanceof BlockHitResult blockHitResult)) {
            return;
        }

        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return;
        }

        if (!blockHitResult.getBlockPos().equals(pos)) {
            return;
        }

        if (state.isAir() || !this.minecraft.level.getWorldBorder().isWithinBounds(pos)) {
            return;
        }

        InteractionResult result = RenderEvents.BLOCK_OUTLINE_RENDER.invoker().onBlockOutlineRender(
                this.minecraft.gameRenderer.getMainCamera(),
                this.minecraft.renderBuffers().bufferSource(),
                poseStack,
                blockHitResult,
                pos,
                state
        );

        if (result != InteractionResult.PASS) {
            ci.cancel();
        }
    }

    static {
        AmberMod.AMBER_MIXINS.add("LevelRendererMixin");
    }
}
