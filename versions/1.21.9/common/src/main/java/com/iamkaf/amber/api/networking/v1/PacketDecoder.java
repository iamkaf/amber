package com.iamkaf.amber.api.networking.v1;

import net.minecraft.network.FriendlyByteBuf;

/**
 * Functional interface for decoding packets from network buffers.
 * 
 * @param <T> the packet type
 */
@FunctionalInterface
public interface PacketDecoder<T extends Packet<T>> {
    
    /**
     * Decodes packet data from the network buffer.
     * 
     * @param buffer the buffer to read from
     * @return the decoded packet
     */
    T decode(FriendlyByteBuf buffer);
}