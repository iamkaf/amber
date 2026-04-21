package com.iamkaf.amber.api.event.v1.events.common.client;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.ApiStatus;

public class HudEvents {
    @ApiStatus.Experimental
    public static final Event<RenderHud> RENDER_HUD = EventFactory.createArrayBacked(
        RenderHud.class, (listeners) -> (context, tickDelta) -> {
            for (RenderHud event : listeners) {
                event.onHudRender(context, tickDelta);
            }
        }
    );

    @FunctionalInterface
    public interface RenderHud {
        void onHudRender(PoseStack poseStack, float tickDelta);
    }
}
