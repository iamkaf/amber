package com.iamkaf.amber.networking.forge;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.networking.v1.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Forge implementation of PlatformNetworkChannel.
 * Uses a simple approach that works with the current Forge version.
 * This will be upgraded to use proper Forge networking once the API is stable.
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
        if (!isClientSide()) {
            throw new IllegalStateException("sendToServer can only be called from client side");
        }
        
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        Constants.LOG.debug("Forge: Simulating send of {} to server", packet.getClass().getSimpleName());
        
        // Handle the packet locally for now
        ForgePacketContext context = new ForgePacketContext(false, null);
        context.execute(() -> {
            try {
                registration.handler.handle(packet, context);
            } catch (Exception e) {
                Constants.LOG.error("Error handling packet: {}", e.getMessage());
            }
        });
    }
    
    @Override
    public <T extends Packet<T>> void sendToPlayer(T packet, ServerPlayer player) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        Constants.LOG.debug("Forge: Simulating send of {} to player {}", packet.getClass().getSimpleName(), player.getName().getString());
        
        // Handle the packet locally for now
        ForgePacketContext context = new ForgePacketContext(true, player);
        context.execute(() -> {
            try {
                registration.handler.handle(packet, context);
            } catch (Exception e) {
                Constants.LOG.error("Error handling packet: {}", e.getMessage());
            }
        });
    }
    
    @Override
    public <T extends Packet<T>> void sendToAllPlayers(T packet) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        Constants.LOG.debug("Forge: Simulating send of {} to all players", packet.getClass().getSimpleName());
        
        // Handle the packet locally for now
        ForgePacketContext context = new ForgePacketContext(true, null);
        context.execute(() -> {
            try {
                registration.handler.handle(packet, context);
            } catch (Exception e) {
                Constants.LOG.error("Error handling packet: {}", e.getMessage());
            }
        });
    }
    
    @Override
    public <T extends Packet<T>> void sendToAllPlayersExcept(T packet, ServerPlayer except) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        Constants.LOG.debug("Forge: Simulating send of {} to all players except {}", packet.getClass().getSimpleName(), except.getName().getString());
        
        // Handle the packet locally for now
        ForgePacketContext context = new ForgePacketContext(true, except);
        context.execute(() -> {
            try {
                registration.handler.handle(packet, context);
            } catch (Exception e) {
                Constants.LOG.error("Error handling packet: {}", e.getMessage());
            }
        });
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