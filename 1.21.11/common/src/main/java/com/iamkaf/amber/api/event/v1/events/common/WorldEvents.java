package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelAccessor;

/**
 * Events that occur during world/level lifecycle operations.
 * <p>
 * These events provide hooks into the world loading, unloading, and saving processes.
 * World events only fire on the logical server side for server levels.
 * <p>
 * Equivalent to Fabric's {@code ServerWorldEvents}, Forge's {@code LevelEvent},
 * and NeoForge's {@code LevelEvent}.
 *
 * @since 8.1.0
 */
public final class WorldEvents {
    /**
     * An event that is fired when a world/level is loaded.
     * <p>
     * This event is fired on the logical server side when a world is loaded into memory.
     * This happens during server startup for the overworld, and when dimensions are loaded.
     * </p>
     * <p>
     * This event is ideal for:
     * </p>
     * <ul>
     *   <li>Initializing world-specific data structures</li>
     *   <li>Loading world-specific configuration</li>
     *   <li>Setting up world-specific caches</li>
     *   <li>Registering world-specific event handlers</li>
     * </ul>
     */
    public static final Event<WorldLoad> WORLD_LOAD = EventFactory.createArrayBacked(
            WorldLoad.class, callbacks -> (server, level) -> {
                for (WorldLoad callback : callbacks) {
                    callback.onWorldLoad(server, level);
                }
            }
    );
    
    /**
     * An event that is fired when a world/level is unloaded.
     * <p>
     * This event is fired on the logical server side when a world is unloaded from memory.
     * This happens during server shutdown for the overworld, and when dimensions are unloaded.
     * </p>
     * <p>
     * This event is ideal for:
     * </p>
     * <ul>
     *   <li>Cleaning up world-specific data structures</li>
     *   <li>Saving world-specific data that isn't automatically saved</li>
     *   <li>Unregistering world-specific event handlers</li>
     *   <li>Clearing world-specific caches</li>
     * </ul>
     */
    public static final Event<WorldUnload> WORLD_UNLOAD = EventFactory.createArrayBacked(
            WorldUnload.class, callbacks -> (server, level) -> {
                for (WorldUnload callback : callbacks) {
                    callback.onWorldUnload(server, level);
                }
            }
    );
    
    /**
     * An event that is fired when a world/level is saved.
     * <p>
     * This event is fired on the logical server side when a world is saved to disk.
     * This happens during auto-save intervals and manual saves.
     * </p>
     * <p>
     * <b>Note:</b> This event is not available on Fabric and will not fire there.
     * </p>
     * <p>
     * This event is ideal for:
     * </p>
     * <ul>
     *   <li>Saving additional world-specific data</li>
     *   <li>Performing cleanup before save</li>
     *   <li>Updating world statistics</li>
     *   <li>Validating world data before save</li>
     * </ul>
     */
    public static final Event<WorldSave> WORLD_SAVE = EventFactory.createArrayBacked(
            WorldSave.class, callbacks -> (server, level) -> {
                for (WorldSave callback : callbacks) {
                    callback.onWorldSave(server, level);
                }
            }
    );

    private WorldEvents() {
    }

    /**
     * Functional interface for handling {@link #WORLD_LOAD} callbacks.
     */
    @FunctionalInterface
    public interface WorldLoad {
        /**
         * Called when a world is loaded.
         * <p>
         * This method is called when a world is loaded into memory.
         * </p>
         *
         * @param server the server instance
         * @param level  the level/world that was loaded
         */
        void onWorldLoad(MinecraftServer server, LevelAccessor level);
    }

    /**
     * Functional interface for handling {@link #WORLD_UNLOAD} callbacks.
     */
    @FunctionalInterface
    public interface WorldUnload {
        /**
         * Called when a world is unloaded.
         * <p>
         * This method is called when a world is unloaded from memory.
         * </p>
         *
         * @param server the server instance
         * @param level  the level/world that was unloaded
         */
        void onWorldUnload(MinecraftServer server, LevelAccessor level);
    }

    /**
     * Functional interface for handling {@link #WORLD_SAVE} callbacks.
     */
    @FunctionalInterface
    public interface WorldSave {
        /**
         * Called when a world is saved.
         * <p>
         * This method is called when a world is saved to disk.
         * </p>
         *
         * @param server the server instance
         * @param level  the level/world that was saved
         */
        void onWorldSave(MinecraftServer server, LevelAccessor level);
    }
}