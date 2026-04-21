package com.iamkaf.amber.networking.forge;

import com.iamkaf.amber.api.networking.v1.PacketContext;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

/**
 * Forge implementation of PacketContext for the networking API.
 */
public class ForgePacketContext implements PacketContext {
    
    private final boolean isClientSide;
    private final Player player;
    
    public ForgePacketContext(boolean isClientSide, Player player) {
        this.isClientSide = isClientSide;
        this.player = player;
    }
    
    @Override
    public boolean isClientSide() {
        return isClientSide;
    }
    
    @Override
    public Player getPlayer() {
        return player;
    }
    
    @Override
    public void execute(Runnable task) {
        // For demonstration purposes, execute immediately
        // In a real implementation, this would ensure main thread execution
        task.run();
    }
}