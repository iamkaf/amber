package com.iamkaf.amber.api.event.v1.events.common.client;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class RenderEvents {
    public static final Event<BlockOutlineRender> BLOCK_OUTLINE_RENDER = EventFactory.createArrayBacked(
            BlockOutlineRender.class, callbacks -> (camera, hitResult, pos, state) -> {
                for (BlockOutlineRender callback : callbacks) {
                    InteractionResult result = callback.onBlockOutlineRender(camera, hitResult, pos, state);
                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }
                return InteractionResult.PASS;
            }
    );

    @FunctionalInterface
    public interface BlockOutlineRender {
        InteractionResult onBlockOutlineRender(Camera camera, BlockHitResult hitResult, BlockPos pos, BlockState state);
    }
}
