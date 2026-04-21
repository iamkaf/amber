package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.networking.v1.PlatformNetworkChannel;
import com.iamkaf.amber.networking.fabric.FabricNetworkChannelImpl;
import com.iamkaf.amber.platform.services.INetworkingService;
import net.minecraft.resources.ResourceLocation;

/**
 * Fabric implementation of the networking service.
 */
public class FabricNetworkingService implements INetworkingService {
    
    @Override
    public PlatformNetworkChannel createChannel(ResourceLocation channelId) {
        return new FabricNetworkChannelImpl(channelId);
    }
}