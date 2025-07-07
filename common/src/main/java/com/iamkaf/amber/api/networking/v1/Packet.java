package com.iamkaf.amber.api.networking.v1;

/**
 * Base interface for all network packets.
 * 
 * @param <T> the packet type (self-referencing for type safety)
 */
public interface Packet<T extends Packet<T>> {
    // Marker interface - packets implement encoding/decoding via functional interfaces
}