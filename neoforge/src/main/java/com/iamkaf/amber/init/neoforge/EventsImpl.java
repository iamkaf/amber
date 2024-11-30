package com.iamkaf.amber.init.neoforge;

import com.iamkaf.amber.Amber;
import com.iamkaf.amber.api.item.BrewingHelper;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@EventBusSubscriber(modid = Amber.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class EventsImpl {
    public static void init() {
        // touch
    }

    @SubscribeEvent
    public static void onBrewingRecipeRegister(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        var listeners = BrewingHelper.getListeners();
        for (var listener : listeners) {
            listener.accept(builder);
        }
    }
}
