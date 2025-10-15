package com.iamkaf.amber.api.event.v1.events.common.examples;

import com.iamkaf.amber.api.event.v1.events.common.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example class demonstrating how to use the server tick events.
 * This example shows how to register listeners for both START_SERVER_TICK and END_SERVER_TICK events.
 */
public class ServerTickExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerTickExample.class);
    private static int tickCounter = 0;
    
    /**
     * Register the server tick event listeners.
     * Call this method during mod initialization to start receiving server tick events.
     */
    public static void register() {
        // Register a listener for the start of server tick
        ServerTickEvents.START_SERVER_TICK.register(() -> {
            tickCounter++;
            
            // Log every 100 ticks to avoid spamming the console
            if (tickCounter % 100 == 0) {
                LOGGER.info("Server tick start! Tick count: {}", tickCounter);
            }
            
            // You can perform any logic here that should run at the start of each server tick
            // For example: checking player positions, updating world data, etc.
        });
        
        // Register a listener for the end of server tick
        ServerTickEvents.END_SERVER_TICK.register(() -> {
            // Log every 100 ticks to avoid spamming the console
            if (tickCounter % 100 == 0) {
                LOGGER.info("Server tick end! Tick count: {}", tickCounter);
            }
            
            // You can perform any logic here that should run at the end of each server tick
            // For example: cleaning up temporary data, sending updates to clients, etc.
        });
        
        LOGGER.info("Server tick event listeners registered!");
    }
    
    /**
     * Get the current tick counter value.
     * @return The number of server ticks that have occurred since registration
     */
    public static int getTickCounter() {
        return tickCounter;
    }
    
    /**
     * Reset the tick counter to zero.
     */
    public static void resetTickCounter() {
        tickCounter = 0;
    }
}