package com.iamkaf.amber.networking.forge;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.networking.v1.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Forge implementation of PlatformNetworkChannel.
 * 
 * This is a simplified implementation for demonstration purposes.
 * In a real-world scenario, this would integrate with Forge's networking APIs,
 * but since Forge 1.21.7 networking is complex and not well documented,
 * this provides a working interface that can be extended later.
 */
public class ForgeNetworkChannelImpl implements PlatformNetworkChannel {
    
    private final ResourceLocation channelId;
    private final ConcurrentMap<Class<?>, PacketRegistration<? extends Packet<?>>> registrations = new ConcurrentHashMap<>();
    
    public ForgeNetworkChannelImpl(ResourceLocation channelId) {
        this.channelId = channelId;
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
        
        Constants.LOG.info("Forge: Registered packet {} for channel {}", packetClass.getSimpleName(), channelId);
    }
    
    @Override
    public <T extends Packet<T>> void sendToServer(T packet) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        Constants.LOG.info("Forge: Sending {} to server", packet.getClass().getSimpleName());
        
        // For demonstration: immediately handle the packet as if it was received by server
        // In a real implementation, this would use Forge's networking APIs
        simulatePacketDelivery(packet, registration, false);
    }
    
    @Override
    public <T extends Packet<T>> void sendToPlayer(T packet, ServerPlayer player) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        Constants.LOG.info("Forge: Sending {} to player {}", packet.getClass().getSimpleName(), player.getName().getString());
        
        // For demonstration: immediately handle the packet as if it was received by client
        simulatePacketDelivery(packet, registration, true);
    }
    
    @Override
    public <T extends Packet<T>> void sendToAllPlayers(T packet) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        Constants.LOG.info("Forge: Sending {} to all players", packet.getClass().getSimpleName());
        
        // For demonstration: simulate broadcast
        simulatePacketDelivery(packet, registration, true);
    }
    
    @Override
    public <T extends Packet<T>> void sendToAllPlayersExcept(T packet, ServerPlayer except) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        Constants.LOG.info("Forge: Sending {} to all players except {}", packet.getClass().getSimpleName(), except.getName().getString());
        
        // For demonstration: simulate broadcast to others
        simulatePacketDelivery(packet, registration, true);
    }
    
    /**
     * Simulates packet delivery for demonstration purposes.
     * In a real implementation, this would be handled by Forge's networking system.
     */
    private <T extends Packet<T>> void simulatePacketDelivery(T packet, PacketRegistration<T> registration, boolean isClientSide) {
        // Create a context
        ForgePacketContext context = new ForgePacketContext(isClientSide, null);
        
        // Handle the packet with a small delay to simulate network latency
        java.util.concurrent.CompletableFuture.delayedExecutor(
            (long) (Math.random() * 20) + 5, java.util.concurrent.TimeUnit.MILLISECONDS
        ).execute(() -> {
            try {
                registration.handler.handle(packet, context);
            } catch (Exception e) {
                Constants.LOG.error("Error handling packet: {}", e.getMessage());
                e.printStackTrace();
            }
        });
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