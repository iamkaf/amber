package com.iamkaf.amber.api.networking.v1;

import net.minecraft.network.FriendlyByteBuf;

/**
 * Functional interface for encoding packets to network buffers.
 * 
 * @param <T> the packet type
 */
@FunctionalInterface
public interface PacketEncoder<T extends Packet<T>> {
    
    /**
     * Encodes the packet data to the network buffer.
     * 
     * @param packet the packet to encode
     * @param buffer the buffer to write to
     */
    void encode(T packet, FriendlyByteBuf buffer);
}