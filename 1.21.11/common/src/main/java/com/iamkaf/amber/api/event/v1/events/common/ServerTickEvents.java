package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;

/**
 * Events that occur during the server tick.
 * <p>
 * These events provide hooks into the server's main game loop, allowing mods to execute code
 * at consistent intervals. Server tick events only fire on the logical server side.
 * <p>
 * Equivalent to Fabric's {@code ServerTickEvents}, Forge's {@code TickEvent.ServerTickEvent},
 * and NeoForge's {@code ServerTickEvent}.
 *
 * @since 8.1.0
 */
public final class ServerTickEvents {
    /**
     * An event that is fired at the start of each server tick.
     * <p>
     * This event is fired on the logical server side before any world or entity ticking occurs.
     * It's suitable for preprocessing tasks, updating global state, or preparing data
     * that will be used during the tick.
     * </p>
     * <p>
     * This event is ideal for:
     * </p>
     * <ul>
     *   <li>Processing queued actions from previous ticks</li>
     *   <li>Updating global mod state</li>
     *   <li>Preparing data for world/entity processing</li>
     *   <li>Performing calculations needed for the current tick</li>
     * </ul>
     */
    public static final Event<StartTick> START_SERVER_TICK = EventFactory.createArrayBacked(
            StartTick.class, callbacks -> () -> {
                for (StartTick event : callbacks) {
                    event.onStartTick();
                }
            }
    );
    
    /**
     * An event that is fired at the end of each server tick.
     * <p>
     * This event is fired on the logical server side after all world and entity ticking has completed.
     * It's suitable for cleanup tasks, sending updates to clients, or performing post-processing
     * on data that was modified during the tick.
     * </p>
     * <p>
     * This event is ideal for:
     * </p>
     * <ul>
     *   <li>Cleaning up temporary data</li>
     *   <li>Sending updates to clients</li>
     *   <li>Performing post-processing on modified data</li>
     *   <li>Tracking statistics and metrics</li>
     * </ul>
     */
    public static final Event<EndTick> END_SERVER_TICK = EventFactory.createArrayBacked(
            EndTick.class, callbacks -> () -> {
                for (EndTick event : callbacks) {
                    event.onEndTick();
                }
            }
    );

    private ServerTickEvents() {
    }

    /**
     * Functional interface for handling {@link #START_SERVER_TICK} callbacks.
     */
    @FunctionalInterface
    public interface StartTick {
        /**
         * Called at the start of a server tick.
         * <p>
         * This method is called before any world or entity ticking occurs.
         * </p>
         */
        void onStartTick();
    }

    /**
     * Functional interface for handling {@link #END_SERVER_TICK} callbacks.
     */
    @FunctionalInterface
    public interface EndTick {
        /**
         * Called at the end of a server tick.
         * <p>
         * This method is called after all world and entity ticking has completed.
         * </p>
         */
        void onEndTick();
    }
}