package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.networking.v1.PlatformNetworkChannel;
import com.iamkaf.amber.networking.forge.ForgeNetworkChannelImpl;
import com.iamkaf.amber.platform.services.INetworkingService;
import net.minecraft.resources.ResourceLocation;

/**
 * Forge implementation of the networking service.
 */
public class ForgeNetworkingService implements INetworkingService {
    
    @Override
    public PlatformNetworkChannel createChannel(ResourceLocation channelId) {
        return new ForgeNetworkChannelImpl(channelId);
    }
}