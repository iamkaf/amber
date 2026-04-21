package com.iamkaf.amber.api.networking.v1;

import net.minecraft.server.level.ServerPlayer;

/**
 * Platform-specific network channel implementation.
 * This is implemented by each mod loader (Fabric, Forge, NeoForge).
 */
public interface PlatformNetworkChannel {
    
    /**
     * Registers a packet type with the platform's networking system.
     */
    <T extends Packet<T>> void register(
        Class<T> packetClass,
        PacketEncoder<T> encoder,
        PacketDecoder<T> decoder,
        PacketHandler<T> handler
    );
    
    /**
     * Sends a packet from client to server.
     */
    <T extends Packet<T>> void sendToServer(T packet);
    
    /**
     * Sends a packet from server to a specific player.
     */
    <T extends Packet<T>> void sendToPlayer(T packet, ServerPlayer player);
    
    /**
     * Sends a packet from server to all players.
     */
    <T extends Packet<T>> void sendToAllPlayers(T packet);
    
    /**
     * Sends a packet from server to all players except one.
     */
    <T extends Packet<T>> void sendToAllPlayersExcept(T packet, ServerPlayer except);
}