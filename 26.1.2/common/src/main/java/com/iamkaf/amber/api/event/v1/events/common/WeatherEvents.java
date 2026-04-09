package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;

/**
 * Events that occur during weather and environmental phenomena.
 * <p>
 * These events provide hooks into weather-related activities and environmental effects.
 * Weather events only fire on the logical server side.
 * <p>
 * Equivalent to NeoForge's event.
 *
 * @since 8.1.0
 */
public final class WeatherEvents {
    /**
     * An event that is fired when an entity is struck by lightning.
     * <p>
     * This event is fired on the logical server side when lightning strikes an entity.
     * This event is cancellable and can be used to prevent the lightning damage.
     * </p>
     * <p>
     * This event is ideal for:
     * </p>
     * <ul>
     *   <li>Preventing lightning damage to certain entities</li>
     *   <li>Adding special effects when entities are struck by lightning</li>
     *   <li>Creating custom lightning behavior</li>
     *   <li>Tracking lightning strikes for statistics</li>
     * </ul>
     */
    public static final Event<LightningStrike> LIGHTNING_STRIKE = EventFactory.createArrayBacked(
            LightningStrike.class, callbacks -> (entity, lightning) -> {
                for (LightningStrike callback : callbacks) {
                    InteractionResult result = callback.onLightningStrike(entity, lightning);
                    if (result != InteractionResult.PASS) {
                        return result; // Early return for cancellation
                    }
                }
                return InteractionResult.PASS; // Allow lightning strike by default
            }
    );

    private WeatherEvents() {
    }

    /**
     * Functional interface for handling {@link #LIGHTNING_STRIKE} callbacks.
     */
    @FunctionalInterface
    public interface LightningStrike {
        /**
         * Called when an entity is struck by lightning.
         * <p>
         * This method is called when lightning strikes an entity.
         * </p>
         *
         * @param entity    the entity that was struck by lightning
         * @param lightning the lightning bolt that struck the entity
         * @return an {@link InteractionResult} indicating whether the lightning strike should be allowed or cancelled
         */
        InteractionResult onLightningStrike(Entity entity, LightningBolt lightning);
    }
}