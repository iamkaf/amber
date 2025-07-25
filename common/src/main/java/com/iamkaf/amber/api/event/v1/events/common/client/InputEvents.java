package com.iamkaf.amber.api.event.v1.events.common.client;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.world.InteractionResult;

public class InputEvents {
    /**
     * An event that is called when the mouse wheel is scrolled. This event can be cancelled to prevent the scroll action.
     */
    public static final Event<MouseScroll> MOUSE_SCROLL = EventFactory.createArrayBacked(
            MouseScroll.class, callbacks -> (mouseX, mouseY, scrollX, scrollY) -> {
                for (MouseScroll callback : callbacks) {
                    InteractionResult result = callback.onMouseScroll(mouseX, mouseY, scrollX, scrollY);
                    if (result != InteractionResult.PASS) {
                        return result; // Early return for cancellation
                    }
                }
                return InteractionResult.PASS; // Allow scroll by default
            }
    );

    @FunctionalInterface
    public interface MouseScroll {
        /**
         * Called when the mouse wheel is scrolled.
         *
         * @param mouseX  the X coordinate of the mouse
         * @param mouseY  the Y coordinate of the mouse  
         * @param scrollX the horizontal scroll amount
         * @param scrollY the vertical scroll amount
         * @return an {@link InteractionResult} indicating whether the scroll should be allowed or cancelled
         */
        InteractionResult onMouseScroll(double mouseX, double mouseY, double scrollX, double scrollY);
    }
}