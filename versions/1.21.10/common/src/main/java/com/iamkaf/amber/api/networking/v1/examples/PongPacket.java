package com.iamkaf.amber.api.networking.v1.examples;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.networking.v1.Packet;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Example pong packet demonstrating the new networking API.
 * This packet is sent from client to server in response to a ping.
 */
public record PongPacket(long originalTimestamp, long responseTimestamp, String message) implements Packet<PongPacket> {
    
    /**
     * Encodes this packet to the network buffer.
     */
    public static void encode(PongPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarLong(packet.originalTimestamp);
        buffer.writeVarLong(packet.responseTimestamp);
        buffer.writeUtf(packet.message);
    }
    
    /**
     * Decodes a packet from the network buffer.
     */
    public static PongPacket decode(FriendlyByteBuf buffer) {
        long originalTimestamp = buffer.readVarLong();
        long responseTimestamp = buffer.readVarLong();
        String message = buffer.readUtf();
        return new PongPacket(originalTimestamp, responseTimestamp, message);
    }
    
    /**
     * Handles a received pong packet.
     * On server side: calculates latency and logs result
     * On client side: logs an error (pongs should only be received by servers)
     */
    public static void handle(PongPacket packet, com.iamkaf.amber.api.networking.v1.PacketContext context) {
        if (context.isServerSide()) {
            // Server received pong - calculate latency
            context.execute(() -> {
                long currentTime = System.currentTimeMillis();
                long totalRoundTrip = currentTime - packet.originalTimestamp;
                long clientProcessingTime = packet.responseTimestamp - packet.originalTimestamp;
                long networkLatency = totalRoundTrip - clientProcessingTime;
                
                Constants.LOG.info("Server received pong: {}", packet.message);
                Constants.LOG.info("  Total round-trip: {}ms", totalRoundTrip);
                Constants.LOG.info("  Client processing: {}ms", clientProcessingTime);
                Constants.LOG.info("  Network latency: {}ms", networkLatency);
                
                // Here you could record the latency for statistics
                recordLatency(totalRoundTrip, networkLatency);
            });
        } else {
            // Client received pong - this shouldn't happen
            Constants.LOG.error("Client received pong packet - this should not happen!");
        }
    }
    
    private static void recordLatency(long totalLatency, long networkLatency) {
        // This would integrate with your statistics system
        // For now, just log it
        Constants.LOG.info("Recorded latency: {}ms", networkLatency);
    }
}