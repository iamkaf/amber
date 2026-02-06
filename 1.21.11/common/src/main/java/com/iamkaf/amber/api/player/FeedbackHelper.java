package com.iamkaf.amber.api.player;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * @deprecated Use {@link com.iamkaf.amber.api.functions.v1.PlayerFunctions} instead.
 * This class will be removed in Amber 10.0
 */
@Deprecated
public class FeedbackHelper {
    public static void message(Player player, Component component) {
        player.displayClientMessage(component, false);
    }

    public static void actionBarMessage(Player player, Component component) {
        player.displayClientMessage(component, true);
    }
}
