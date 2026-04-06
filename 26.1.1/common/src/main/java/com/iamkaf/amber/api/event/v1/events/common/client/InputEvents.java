package com.iamkaf.amber.api.event.v1.events.common.client;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.world.InteractionResult;

public class InputEvents {
    /**
     * An event that is called before the mouse wheel is scrolled. This event can be cancelled to prevent the scroll action.
     */
    public static final Event<MouseScrollPre> MOUSE_SCROLL_PRE = EventFactory.createArrayBacked(
            MouseScrollPre.class, callbacks -> (mouseX, mouseY, scrollX, scrollY) -> {
                for (MouseScrollPre callback : callbacks) {
                    InteractionResult result = callback.onMouseScrollPre(mouseX, mouseY, scrollX, scrollY);
                    if (result != InteractionResult.PASS) {
                        return result; // Early return for cancellation
                    }
                }
                return InteractionResult.PASS; // Allow scroll by default
            }
    );

    /**
     * An event that is called after the mouse wheel has been scrolled. This event cannot be cancelled.
     */
    public static final Event<MouseScrollPost> MOUSE_SCROLL_POST = EventFactory.createArrayBacked(
            MouseScrollPost.class, callbacks -> (mouseX, mouseY, scrollX, scrollY) -> {
                for (MouseScrollPost callback : callbacks) {
                    callback.onMouseScrollPost(mouseX, mouseY, scrollX, scrollY);
                }
            }
    );

    @FunctionalInterface
    public interface MouseScrollPre {
        /**
         * Called before the mouse wheel is scrolled.
         *
         * @param mouseX  the X coordinate of the mouse
         * @param mouseY  the Y coordinate of the mouse  
         * @param scrollX the horizontal scroll amount
         * @param scrollY the vertical scroll amount
         * @return an {@link InteractionResult} indicating whether the scroll should be allowed or cancelled
         */
        InteractionResult onMouseScrollPre(double mouseX, double mouseY, double scrollX, double scrollY);
    }

    @FunctionalInterface
    public interface MouseScrollPost {
        /**
         * Called after the mouse wheel has been scrolled.
         *
         * @param mouseX  the X coordinate of the mouse
         * @param mouseY  the Y coordinate of the mouse  
         * @param scrollX the horizontal scroll amount that was applied
         * @param scrollY the vertical scroll amount that was applied
         */
        void onMouseScrollPost(double mouseX, double mouseY, double scrollX, double scrollY);
    }
}