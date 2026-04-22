package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.networking.v1.PlatformNetworkChannel;
import com.iamkaf.amber.networking.fabric.FabricNetworkChannelImpl;
import com.iamkaf.amber.platform.services.INetworkingService;
//? if <1.21.11 {
/*import net.minecraft.resources.ResourceLocation;*/
//?} else {
import net.minecraft.resources.Identifier;
//?}

/**
 * Fabric implementation of the networking service.
 */
public class FabricNetworkingService implements INetworkingService {
    
    @Override
    //? if <1.21.11 {
    /*public PlatformNetworkChannel createChannel(ResourceLocation channelId) {*/
    //?} else {
    public PlatformNetworkChannel createChannel(Identifier channelId) {
    //?}
        return new FabricNetworkChannelImpl(channelId);
    }
}
