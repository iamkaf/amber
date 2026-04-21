package com.iamkaf.amber.api.networking.v1;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Context information for packet handling.
 * Provides thread-safe execution and access to the receiving player.
 */
public interface PacketContext {
    
    /**
     * Checks if this packet is being handled on the client side.
     * 
     * @return true if on client side
     */
    boolean isClientSide();
    
    /**
     * Checks if this packet is being handled on the server side.
     * 
     * @return true if on server side
     */
    default boolean isServerSide() {
        return !isClientSide();
    }
    
    /**
     * Gets the player who sent or is receiving this packet.
     * On client side, this is the local player.
     * On server side, this is the player who sent the packet.
     * 
     * @return the player, or null if not available
     */
    Player getPlayer();
    
    /**
     * Gets the server player if on server side.
     * Convenience method for server-side packet handling.
     * 
     * @return the server player, or null if on client side
     */
    default ServerPlayer getServerPlayer() {
        Player player = getPlayer();
        return player instanceof ServerPlayer serverPlayer ? serverPlayer : null;
    }
    
    /**
     * Executes a task on the appropriate thread for this context.
     * This ensures thread safety when modifying game state.
     * 
     * @param task the task to execute
     */
    void execute(Runnable task);
}