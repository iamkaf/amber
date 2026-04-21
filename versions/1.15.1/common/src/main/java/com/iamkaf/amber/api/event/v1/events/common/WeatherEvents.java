package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.global.LightningBolt;

public final class WeatherEvents {
    public static final Event<LightningStrike> LIGHTNING_STRIKE = EventFactory.createArrayBacked(
            LightningStrike.class, callbacks -> (entity, lightning) -> {
                for (LightningStrike callback : callbacks) {
                    InteractionResult result = callback.onLightningStrike(entity, lightning);
                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }
                return InteractionResult.PASS;
            }
    );

    private WeatherEvents() {
    }

    @FunctionalInterface
    public interface LightningStrike {
        InteractionResult onLightningStrike(Entity entity, LightningBolt lightning);
    }
}
