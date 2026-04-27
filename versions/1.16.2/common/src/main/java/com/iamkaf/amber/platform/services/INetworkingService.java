package com.iamkaf.amber.platform.services;

import com.iamkaf.amber.api.networking.v1.PlatformNetworkChannel;
import net.minecraft.resources.ResourceLocation;

/**
 * Platform service for creating networking channels.
 * Each mod loader provides its own implementation.
 */
public interface INetworkingService {
    
    /**
     * Creates a platform-specific network channel.
     * 
     * @param channelId the unique identifier for the channel
     * @return a platform-specific network channel implementation
     */
    PlatformNetworkChannel createChannel(ResourceLocation channelId);
}