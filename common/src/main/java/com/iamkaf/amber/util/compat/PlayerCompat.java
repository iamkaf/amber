package com.iamkaf.amber.util.compat;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public final class PlayerCompat {
    private PlayerCompat() {
    }

    public static void displayClientMessage(Player player, Component message, boolean actionBar) {
        player.displayClientMessage(message, actionBar);
    }
}
