package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.networking.v1.PlatformNetworkChannel;
import com.iamkaf.amber.networking.forge.ForgeNetworkChannelImpl;
import com.iamkaf.amber.platform.services.INetworkingService;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Forge implementation of the networking service.
 */
public class ForgeNetworkingService implements INetworkingService {
    
    private final List<ForgeNetworkChannelImpl> channels = new ArrayList<>();
    
    @Override
    public PlatformNetworkChannel createChannel(Identifier channelId) {
        ForgeNetworkChannelImpl channel = new ForgeNetworkChannelImpl(channelId);
        channels.add(channel);
        return channel;
    }
    
    /**
     * Gets all created channels for potential future networking setup.
     */
    public List<ForgeNetworkChannelImpl> getChannels() {
        return new ArrayList<>(channels);
    }
}