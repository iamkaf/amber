package com.iamkaf.amber.api.networking.v1;

import com.iamkaf.amber.platform.Services;
//? if <1.21.11 {
/*import net.minecraft.resources.ResourceLocation;*/
//?} else {
import net.minecraft.resources.Identifier;
//?}
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Internal implementation of NetworkChannel.
 * Delegates to platform-specific networking services.
 */
class NetworkChannelImpl implements NetworkChannel {
    
    //? if <1.21.11 {
    /*private static final ConcurrentMap<ResourceLocation, NetworkChannelImpl> CHANNELS = new ConcurrentHashMap<>();*/
    //?} else {
    private static final ConcurrentMap<Identifier, NetworkChannelImpl> CHANNELS = new ConcurrentHashMap<>();
    //?}
    
    //? if <1.21.11 {
    /*private final ResourceLocation channelId;*/
    //?} else {
    private final Identifier channelId;
    //?}
    private final PlatformNetworkChannel platformChannel;
    
    //? if <1.21.11 {
    /*private NetworkChannelImpl(ResourceLocation channelId) {*/
    //?} else {
    private NetworkChannelImpl(Identifier channelId) {
    //?}
        this.channelId = channelId;
        this.platformChannel = Services.NETWORKING.createChannel(channelId);
    }
    
    //? if <1.21.11 {
    /*static NetworkChannel create(ResourceLocation channelId) {*/
    //?} else {
    static NetworkChannel create(Identifier channelId) {
    //?}
        return CHANNELS.computeIfAbsent(channelId, NetworkChannelImpl::new);
    }
    
    @Override
    public <T extends Packet<T>> void register(
            Class<T> packetClass,
            PacketEncoder<T> encoder,
            PacketDecoder<T> decoder,
            PacketHandler<T> handler
    ) {
        platformChannel.register(packetClass, encoder, decoder, handler);
    }
    
    @Override
    public <T extends Packet<T>> void sendToServer(T packet) {
        platformChannel.sendToServer(packet);
    }
    
    @Override
    public <T extends Packet<T>> void sendToPlayer(T packet, ServerPlayer player) {
        platformChannel.sendToPlayer(packet, player);
    }
    
    @Override
    public <T extends Packet<T>> void sendToAllPlayers(T packet) {
        platformChannel.sendToAllPlayers(packet);
    }
    
    @Override
    public <T extends Packet<T>> void sendToAllPlayersExcept(T packet, ServerPlayer except) {
        platformChannel.sendToAllPlayersExcept(packet, except);
    }
    
    @Override
    //? if <1.21.11 {
    /*public ResourceLocation getChannelId() {*/
    //?} else {
    public Identifier getChannelId() {
    //?}
        return channelId;
    }
}
