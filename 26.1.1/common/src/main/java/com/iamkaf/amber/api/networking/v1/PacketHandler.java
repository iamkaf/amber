package com.iamkaf.amber.api.networking.v1;

/**
 * Functional interface for handling received packets.
 * 
 * @param <T> the packet type
 */
@FunctionalInterface
public interface PacketHandler<T extends Packet<T>> {
    
    /**
     * Handles a received packet with the given context.
     * 
     * @param packet the received packet
     * @param context the packet context (client/server info, thread safety, etc.)
     */
    void handle(T packet, PacketContext context);
}