package com.iamkaf.amber.api.networking.v1;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * A network channel for sending and receiving packets between client and server.
 * Inspired by Architectury API's networking system.
 * 
 * Usage:
 * <pre>
 * // Create a channel
 * NetworkChannel CHANNEL = NetworkChannel.create(Identifier.fromNamespaceAndPath("mymod", "main"));
 * 
 * // Register a packet type
 * CHANNEL.register(MyPacket.class, MyPacket::encode, MyPacket::decode, MyPacket::handle);
 * 
 * // Send packets
 * CHANNEL.sendToServer(new MyPacket());
 * CHANNEL.sendToPlayer(new MyPacket(), player);
 * </pre>
 */
public interface NetworkChannel {
    
    /**
     * Creates a new network channel with the given identifier.
     * 
     * @param channelId unique identifier for this channel
     * @return a new network channel instance
     */
    static NetworkChannel create(Identifier channelId) {
        return NetworkChannelImpl.create(channelId);
    }
    
    /**
     * Registers a packet type with this channel.
     * 
     * @param <T> the packet type
     * @param packetClass the packet class
     * @param encoder function to encode the packet to a buffer
     * @param decoder function to decode the packet from a buffer
     * @param handler function to handle the received packet
     */
    <T extends Packet<T>> void register(
        Class<T> packetClass,
        PacketEncoder<T> encoder,
        PacketDecoder<T> decoder,
        PacketHandler<T> handler
    );
    
    /**
     * Sends a packet from client to server.
     * Can only be called from the client side.
     * 
     * @param <T> the packet type
     * @param packet the packet to send
     */
    <T extends Packet<T>> void sendToServer(T packet);
    
    /**
     * Sends a packet from server to a specific player.
     * Can only be called from the server side.
     * 
     * @param <T> the packet type
     * @param packet the packet to send
     * @param player the target player
     */
    <T extends Packet<T>> void sendToPlayer(T packet, ServerPlayer player);
    
    /**
     * Sends a packet from server to all players.
     * Can only be called from the server side.
     * 
     * @param <T> the packet type
     * @param packet the packet to send
     */
    <T extends Packet<T>> void sendToAllPlayers(T packet);
    
    /**
     * Sends a packet from server to all players except one.
     * Can only be called from the server side.
     * 
     * @param <T> the packet type
     * @param packet the packet to send
     * @param except the player to exclude
     */
    <T extends Packet<T>> void sendToAllPlayersExcept(T packet, ServerPlayer except);
    
    /**
     * Gets the unique identifier for this channel.
     * 
     * @return the channel identifier
     */
    Identifier getChannelId();
}