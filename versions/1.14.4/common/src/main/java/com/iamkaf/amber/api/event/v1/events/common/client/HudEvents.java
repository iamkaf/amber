package com.iamkaf.amber.api.event.v1.events.common.client;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import org.jetbrains.annotations.ApiStatus;

public class HudEvents {
    @ApiStatus.Experimental
    public static final Event<RenderHud> RENDER_HUD = EventFactory.createArrayBacked(
        RenderHud.class, (listeners) -> (tickDelta) -> {
            for (RenderHud event : listeners) {
                event.onHudRender(tickDelta);
            }
        }
    );

    @FunctionalInterface
    public interface RenderHud {
        void onHudRender(float tickDelta);
    }
}
