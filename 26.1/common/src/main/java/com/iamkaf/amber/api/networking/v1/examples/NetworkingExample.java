package com.iamkaf.amber.api.networking.v1.examples;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.networking.v1.NetworkChannel;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * Example demonstrating how to use the new user-friendly networking API.
 * 
 * This shows how simple it is to set up cross-platform networking:
 * 1. Create a channel
 * 2. Register packet types
 * 3. Send packets
 * 
 * Compare this to the old system which required complex platform-specific setup!
 */
public class NetworkingExample {
    
    // Create a network channel - this works on all platforms automatically
    public static final NetworkChannel CHANNEL = NetworkChannel.create(
        Identifier.fromNamespaceAndPath("amber", "example")
    );
    
    /**
     * Initialize the networking example.
     * Call this during mod initialization.
     */
    public static void init() {
        // Register packet types with their encoding, decoding, and handling logic
        // This is all you need - no platform-specific code!
        
        CHANNEL.register(
            PingPacket.class,
            PingPacket::encode,
            PingPacket::decode,
            PingPacket::handle
        );
        
        CHANNEL.register(
            PongPacket.class,
            PongPacket::encode,
            PongPacket::decode,
            PongPacket::handle
        );
        
        Constants.LOG.info("Networking example initialized - ready to ping!");
    }
    
    /**
     * Send a ping to a specific player to measure latency.
     * Call this from the server side.
     */
    public static void pingPlayer(ServerPlayer player) {
        long timestamp = System.currentTimeMillis();
        String message = "Hello from server!";
        
        PingPacket ping = new PingPacket(timestamp, message);
        
        // Send the packet - platform differences are handled automatically
        CHANNEL.sendToPlayer(ping, player);
        
        Constants.LOG.info("Sent ping to {}", player.getName().getString());
    }
    
    /**
     * Send a ping to all players.
     * Call this from the server side.
     */
    public static void pingAllPlayers() {
        long timestamp = System.currentTimeMillis();
        String message = "Broadcast ping from server!";
        
        PingPacket ping = new PingPacket(timestamp, message);
        
        // Send to all players - works the same way
        CHANNEL.sendToAllPlayers(ping);
        
        Constants.LOG.info("Sent broadcast ping to all players");
    }
    
    /**
     * Example of sending a custom packet from client to server.
     * This could be called from a client-side event or command.
     */
    public static void sendCustomMessage() {
        // Example: client wants to send a message to server
        // (This would typically be in response to a GUI action or key press)
        
        // Create any packet type and send it
        long timestamp = System.currentTimeMillis();
        PongPacket message = new PongPacket(timestamp, timestamp, "Hello from client!");
        
        // Send to server - platform differences handled automatically
        CHANNEL.sendToServer(message);
        
        Constants.LOG.info("Sent custom message to server");
    }
}