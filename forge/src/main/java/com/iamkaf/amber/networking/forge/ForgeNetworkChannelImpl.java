package com.iamkaf.amber.networking.forge;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.networking.v1.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * Forge implementation of PlatformNetworkChannel.
 * Uses Forge's ChannelBuilder and SimpleChannel networking system for proper cross-platform packet sending.
 */
public class ForgeNetworkChannelImpl implements PlatformNetworkChannel {
    
    private static final int PROTOCOL_VERSION = 1;
    
    private final ResourceLocation channelId;
    private final SimpleChannel channel;
    private final ConcurrentMap<Class<?>, PacketRegistration<? extends Packet<?>>> registrations = new ConcurrentHashMap<>();
    
    public ForgeNetworkChannelImpl(ResourceLocation channelId) {
        this.channelId = channelId;
        this.channel = ChannelBuilder.named(channelId)
            .networkProtocolVersion(PROTOCOL_VERSION)
            .clientAcceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION))
            .serverAcceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION))
            .simpleChannel();
    }
    
    @Override
    public <T extends Packet<T>> void register(
            Class<T> packetClass,
            PacketEncoder<T> encoder,
            PacketDecoder<T> decoder,
            PacketHandler<T> handler
    ) {
        PacketRegistration<T> registration = new PacketRegistration<>(encoder, decoder, handler);
        registrations.put(packetClass, registration);
        
        // Create StreamCodec for the packet
        StreamCodec<FriendlyByteBuf, T> codec = StreamCodec.of(
            (buffer, packet) -> encoder.encode(packet, buffer),
            buffer -> decoder.decode(buffer)
        );
        
        // Register bidirectional packet handling using the deprecated MessageBuilder API
        // This is needed for compatibility with the current Forge version
        channel.messageBuilder(packetClass)
            .decoder(buffer -> decoder.decode(buffer))
            .encoder((packet, buffer) -> encoder.encode(packet, buffer))
            .consumerMainThread((packet, context) -> {
                ServerPlayer sender = context.getSender();
                ForgePacketContext packetContext = new ForgePacketContext(sender == null, sender);
                
                try {
                    handler.handle(packet, packetContext);
                } catch (Exception e) {
                    Constants.LOG.error("Error handling packet: {}", e.getMessage(), e);
                }
            })
            .add();
        
        Constants.LOG.info("Forge: Registered packet {} for channel {}", packetClass.getSimpleName(), channelId);
    }
    
    @Override
    public <T extends Packet<T>> void sendToServer(T packet) {
        if (!isClientSide()) {
            throw new IllegalStateException("sendToServer can only be called from client side");
        }
        
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        Constants.LOG.debug("Forge: Sending {} to server", packet.getClass().getSimpleName());
        
        // Send the packet through the SimpleChannel
        channel.send(packet, PacketDistributor.SERVER.noArg());
    }
    
    @Override
    public <T extends Packet<T>> void sendToPlayer(T packet, ServerPlayer player) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        Constants.LOG.debug("Forge: Sending {} to player {}", packet.getClass().getSimpleName(), player.getName().getString());
        
        // Send the packet to the specific player using PacketDistributor
        channel.send(packet, PacketDistributor.PLAYER.with(player));
    }
    
    @Override
    public <T extends Packet<T>> void sendToAllPlayers(T packet) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        Constants.LOG.debug("Forge: Sending {} to all players", packet.getClass().getSimpleName());
        
        // Send the packet to all connected players
        channel.send(packet, PacketDistributor.ALL.noArg());
    }
    
    @Override
    public <T extends Packet<T>> void sendToAllPlayersExcept(T packet, ServerPlayer except) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        Constants.LOG.debug("Forge: Sending {} to all players except {}", packet.getClass().getSimpleName(), except.getName().getString());
        
        // Send to all players except the specified one
        // We need to iterate through all players and send individually
        if (except.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
                if (!player.equals(except)) {
                    channel.send(packet, PacketDistributor.PLAYER.with(player));
                }
            }
        }
    }
    
    private boolean isClientSide() {
        try {
            return net.minecraftforge.fml.loading.FMLLoader.getDist().isClient();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Internal packet registration data.
     */
    private static class PacketRegistration<T extends Packet<T>> {
        final PacketEncoder<T> encoder;
        final PacketDecoder<T> decoder;
        final PacketHandler<T> handler;
        
        PacketRegistration(PacketEncoder<T> encoder, PacketDecoder<T> decoder, PacketHandler<T> handler) {
            this.encoder = encoder;
            this.decoder = decoder;
            this.handler = handler;
        }
    }
    
}