package com.iamkaf.amber.api.item;

import net.minecraft.world.item.alchemy.PotionBrewing;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @deprecated This helper will be replaced by a versioned alternative in a future release.
 */
@Deprecated
public class BrewingHelper {
    private static final List<Consumer<PotionBrewing>> listeners = new ArrayList<>();

    public static void addListener(Consumer<PotionBrewing> listener) {
        listeners.add(listener);
    }

    @ApiStatus.Internal()
    public static List<Consumer<PotionBrewing>> getListeners() {
        return listeners;
    }
}
