package com.iamkaf.amber.api.player;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class FeedbackHelper {
    public static void message(Player player, Component component) {
        player.sendSystemMessage(component);
    }

    public static void actionBarMessage(Player player, Component component) {
        player.displayClientMessage(component, true);
    }
}
