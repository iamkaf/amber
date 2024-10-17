package com.iamkaf.amber.api.item;

import net.minecraft.world.item.alchemy.PotionBrewing;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Consumer;

public class BrewingHelper {
    private static List<Consumer<PotionBrewing.Builder>> listeners = List.of();

    public static void addListener(Consumer<PotionBrewing.Builder> listener) {
        listeners.add(listener);
    }

    @ApiStatus.Internal()
    public static List<Consumer<PotionBrewing.Builder>> getListeners() {
        return listeners;
    }
}
