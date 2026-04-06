package com.iamkaf.amber.networking.v1;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.networking.v1.NetworkChannel;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Internal networking manager for Amber's networking diagnostics.
 * Handles ping-pong latency measurement and other internal networking tasks.
 */
public class AmberNetworking {
    
    // Network channel for internal Amber networking
    public static final NetworkChannel CHANNEL = NetworkChannel.create(
        Identifier.fromNamespaceAndPath(Constants.MOD_ID, "internal")
    );
    
    // Latency tracking
    private static final AtomicLong totalPings = new AtomicLong(0);
    private static final AtomicLong totalLatency = new AtomicLong(0);
    
    private static boolean initialized = false;
    
    /**
     * Initialize Amber's internal networking system.
     * This should be called during Amber's initialization.
     * Safe to call multiple times.
     */
    public static void initialize() {
        if (initialized) {
            Constants.LOG.debug("Amber networking v2 already initialized");
            return;
        }
        
        try {
            // Register ping packet (server -> client)
            CHANNEL.register(
                PingPacket.class,
                PingPacket.ENCODER,
                PingPacket.DECODER,
                PingPacket.HANDLER
            );
            
            // Register pong packet (client -> server)
            CHANNEL.register(
                PongPacket.class,
                PongPacket.ENCODER,
                PongPacket.DECODER,
                PongPacket.HANDLER
            );
            
            initialized = true;
            Constants.LOG.info("Amber internal networking initialized");
            
        } catch (Exception e) {
            Constants.LOG.error("Failed to initialize Amber internal networking", e);
        }
    }
    
    /**
     * Send a ping to a player to measure latency.
     * This is used internally by Amber for network diagnostics.
     * 
     * @param player The player to ping
     * @param reason Reason for the ping (for logging)
     */
    public static void pingPlayer(ServerPlayer player, String reason) {
        if (!initialized) {
            Constants.LOG.warn("Amber networking not initialized, cannot ping player");
            return;
        }
        
        try {
            long timestamp = System.currentTimeMillis();
            String message = "Amber ping: " + reason;
            PingPacket pingPacket = new PingPacket(timestamp, message);
            
            CHANNEL.sendToPlayer(pingPacket, player);
            
            Constants.LOG.debug("Sent ping to player {}: {} (timestamp: {})", 
                               player.getName().getString(), reason, timestamp);
            
        } catch (Exception e) {
            Constants.LOG.error("Failed to send ping to player {}", player.getName().getString(), e);
        }
    }
    
    /**
     * Send a pong response to the server.
     * This is used internally by the ping packet handler.
     * 
     * @param pongPacket The pong packet to send
     */
    public static void sendToServer(PongPacket pongPacket) {
        if (!initialized) {
            Constants.LOG.warn("Amber networking not initialized, cannot send pong");
            return;
        }
        
        try {
            CHANNEL.sendToServer(pongPacket);
        } catch (Exception e) {
            Constants.LOG.error("Failed to send pong to server", e);
        }
    }
    
    /**
     * Send a welcome ping to a player when they join.
     * This helps establish connectivity and measure initial latency.
     * 
     * @param player The player who joined
     */
    public static void sendWelcomePing(ServerPlayer player) {
        pingPlayer(player, "welcome");
    }
    
    /**
     * Test network connectivity to a player.
     * 
     * @param player The player to test
     */
    public static void testConnectivity(ServerPlayer player) {
        pingPlayer(player, "connectivity_test");
    }
    
    /**
     * Record latency measurement from a ping-pong cycle.
     * Used internally by the pong packet handler.
     * 
     * @param totalLatency Total round-trip time in milliseconds
     * @param networkLatency Estimated network latency in milliseconds
     */
    public static void recordLatency(long totalLatency, long networkLatency) {
        totalPings.incrementAndGet();
        AmberNetworking.totalLatency.addAndGet(networkLatency);
        
        Constants.LOG.debug("Recorded latency: total={}ms, network={}ms", totalLatency, networkLatency);
    }
    
    /**
     * Get the average network latency across all recorded ping-pong cycles.
     * 
     * @return Average latency in milliseconds, or -1 if no data available
     */
    public static long getAverageLatency() {
        long pings = totalPings.get();
        if (pings == 0) {
            return -1;
        }
        return totalLatency.get() / pings;
    }
    
    /**
     * Get the total number of ping-pong cycles completed.
     * 
     * @return Number of completed ping-pong cycles
     */
    public static long getTotalPings() {
        return totalPings.get();
    }
    
    /**
     * Check if the internal networking system is initialized.
     * 
     * @return true if initialized, false otherwise
     */
    public static boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Reset latency statistics.
     * Useful for testing or diagnostics.
     */
    public static void resetStats() {
        totalPings.set(0);
        totalLatency.set(0);
        Constants.LOG.debug("Amber networking statistics reset");
    }
}