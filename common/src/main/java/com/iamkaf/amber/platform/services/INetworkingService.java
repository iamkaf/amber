package com.iamkaf.amber.platform.services;

import com.iamkaf.amber.api.networking.v1.PlatformNetworkChannel;
//? if <1.21.11 {
/*import net.minecraft.resources.ResourceLocation;*/
//?} else {
import net.minecraft.resources.Identifier;
//?}

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
    //? if <1.21.11 {
    /*PlatformNetworkChannel createChannel(ResourceLocation channelId);*/
    //?} else {
    PlatformNetworkChannel createChannel(Identifier channelId);
    //?}
}
