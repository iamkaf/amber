package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.networking.v1.PlatformNetworkChannel;
import com.iamkaf.amber.networking.neoforge.NeoForgeNetworkChannelImpl;
import com.iamkaf.amber.platform.services.INetworkingService;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayList;
import java.util.List;

/**
 * NeoForge implementation of the networking service.
 */
public class NeoForgeNetworkingService implements INetworkingService {
    
    private final List<NeoForgeNetworkChannelImpl> channels = new ArrayList<>();
    private PayloadRegistrar payloadRegistrar;
    
    @Override
    public synchronized PlatformNetworkChannel createChannel(Identifier channelId) {
        NeoForgeNetworkChannelImpl channel = new NeoForgeNetworkChannelImpl(channelId);
        channels.add(channel);
        if (payloadRegistrar != null) {
            channel.setPayloadRegistrar(payloadRegistrar);
        }
        return channel;
    }
    
    /**
     * Sets the payload registrar for all created channels.
     * Called during the RegisterPayloadHandlersEvent.
     */
    public synchronized void setPayloadRegistrar(PayloadRegistrar registrar) {
        payloadRegistrar = registrar;
        for (NeoForgeNetworkChannelImpl channel : channels) {
            channel.setPayloadRegistrar(registrar);
        }
    }
}
