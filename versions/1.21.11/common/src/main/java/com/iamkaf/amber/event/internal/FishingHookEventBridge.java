package com.iamkaf.amber.event.internal;

import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;

public interface FishingHookEventBridge {
    void amber$fireStop(PlayerEvents.FishingStopReason reason, boolean wasSuccessful);
}
