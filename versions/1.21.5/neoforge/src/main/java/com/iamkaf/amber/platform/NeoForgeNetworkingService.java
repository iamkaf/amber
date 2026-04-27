package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.networking.v1.PlatformNetworkChannel;
import com.iamkaf.amber.networking.neoforge.NeoForgeNetworkChannelImpl;
import com.iamkaf.amber.platform.services.INetworkingService;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayList;
import java.util.List;

/**
 * NeoForge implementation of the networking service.
 */
public class NeoForgeNetworkingService implements INetworkingService {
    
    private final List<NeoForgeNetworkChannelImpl> channels = new ArrayList<>();
    
    @Override
    public PlatformNetworkChannel createChannel(ResourceLocation channelId) {
        NeoForgeNetworkChannelImpl channel = new NeoForgeNetworkChannelImpl(channelId);
        channels.add(channel);
        return channel;
    }
    
    /**
     * Sets the payload registrar for all created channels.
     * Called during the RegisterPayloadHandlersEvent.
     */
    public void setPayloadRegistrar(PayloadRegistrar registrar) {
        for (NeoForgeNetworkChannelImpl channel : channels) {
            channel.setPayloadRegistrar(registrar);
        }
    }
}