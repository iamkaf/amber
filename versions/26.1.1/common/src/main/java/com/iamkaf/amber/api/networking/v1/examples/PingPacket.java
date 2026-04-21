package com.iamkaf.amber.api.networking.v1.examples;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.networking.v1.Packet;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Example ping packet demonstrating the new networking API.
 * This packet is sent from server to client to measure latency.
 */
public record PingPacket(long timestamp, String message) implements Packet<PingPacket> {
    
    /**
     * Encodes this packet to the network buffer.
     */
    public static void encode(PingPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarLong(packet.timestamp);
        buffer.writeUtf(packet.message);
    }
    
    /**
     * Decodes a packet from the network buffer.
     */
    public static PingPacket decode(FriendlyByteBuf buffer) {
        long timestamp = buffer.readVarLong();
        String message = buffer.readUtf();
        return new PingPacket(timestamp, message);
    }
    
    /**
     * Handles a received ping packet.
     * On client side: responds with a pong using the NetworkingExample channel
     * On server side: logs an error (pings should only be received by clients)
     */
    public static void handle(PingPacket packet, com.iamkaf.amber.api.networking.v1.PacketContext context) {
        if (context.isClientSide()) {
            // Client received ping - respond with pong
            context.execute(() -> {
                long responseTime = System.currentTimeMillis();
                PongPacket response = new PongPacket(packet.timestamp, responseTime, "Pong: " + packet.message);
                
                // Send pong back to server using the NetworkingExample channel
                // In a real implementation, you'd have your own channel reference
                NetworkingExample.CHANNEL.sendToServer(response);
                
                Constants.LOG.info("Client received ping: {} at {}", packet.message, packet.timestamp);
                Constants.LOG.info("Responding with pong after {}ms", (responseTime - packet.timestamp));
            });
        } else {
            // Server received ping - this shouldn't happen
            Constants.LOG.error("Server received ping packet - this should not happen!");
        }
    }
}