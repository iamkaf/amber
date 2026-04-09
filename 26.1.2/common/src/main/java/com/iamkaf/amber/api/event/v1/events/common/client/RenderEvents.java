package com.iamkaf.amber.api.event.v1.events.common.client;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import com.mojang.blaze3d.vertex.PoseStack;

public class RenderEvents {
    /**
     * An event that is called when a block outline is about to be rendered. This event can be cancelled to prevent
     * the outline from being rendered, or used to customize the outline rendering.
     *
     * <p><b>Note for 1.21.9:</b> Rendering systems changed significantly in 1.21.9:
     * <ul>
     *   <li><b>NeoForge:</b> Fully supported. Uses {@code ExtractBlockOutlineRenderStateEvent} with {@code CustomBlockOutlineRenderer} bridge.</li>
     *   <li><b>Fabric:</b> Fully supported. Mixin injects into {@code LevelRenderer#renderBlockOutline} with full rendering context.</li>
     *   <li><b>Forge:</b> Not yet available for 1.21.9.</li>
     * </ul>
     *
     * @see <a href="https://fabricmc.net/2025/09/23/1219.html">Fabric 1.21.9 Release Notes</a>
     * @see <a href="https://neoforged.net/news/21.9release/">NeoForge 1.21.9 Release Notes</a>
     */
    public static final Event<BlockOutlineRender> BLOCK_OUTLINE_RENDER = EventFactory.createArrayBacked(
            BlockOutlineRender.class, callbacks -> (camera, bufferSource, poseStack, hitResult, pos, state) -> {
                for (BlockOutlineRender callback : callbacks) {
                    InteractionResult result = callback.onBlockOutlineRender(camera, bufferSource, poseStack, hitResult, pos, state);
                    if (result != InteractionResult.PASS) {
                        return result; // Early return for cancellation
                    }
                }
                return InteractionResult.PASS; // Allow rendering by default
            }
    );

    @FunctionalInterface
    public interface BlockOutlineRender {
        /**
         * Called when a block outline is about to be rendered.
         *
         * @param camera       the camera being used for rendering
         * @param bufferSource the buffer source for rendering
         * @param poseStack    the pose stack for transformations
         * @param hitResult    the block hit result containing the targeted block
         * @param pos          the position of the block being outlined
         * @param state        the state of the block being outlined
         * @return an {@link InteractionResult} indicating whether the outline should be rendered or cancelled
         */
        InteractionResult onBlockOutlineRender(Camera camera, MultiBufferSource bufferSource, PoseStack poseStack, 
                                             BlockHitResult hitResult, BlockPos pos, BlockState state);
    }
}