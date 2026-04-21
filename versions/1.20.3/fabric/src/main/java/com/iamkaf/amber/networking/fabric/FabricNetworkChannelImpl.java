package com.iamkaf.amber.networking.fabric;

import com.iamkaf.amber.api.networking.v1.*;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Fabric implementation of PlatformNetworkChannel.
 * Uses Fabric's networking API to handle cross-platform packet sending.
 */
public class FabricNetworkChannelImpl implements PlatformNetworkChannel {
    
    private final ResourceLocation channelId;
    private final ConcurrentMap<Class<?>, PacketRegistration<? extends Packet<?>>> registrations = new ConcurrentHashMap<>();
    
    public FabricNetworkChannelImpl(ResourceLocation channelId) {
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
        
        // Create packet type for this packet class
        ResourceLocation packetId = new ResourceLocation(
            channelId.getNamespace(),
            channelId.getPath() + "/" + packetClass.getSimpleName().toLowerCase()
        );

        ServerPlayNetworking.registerGlobalReceiver(packetId, (server, player, networkHandler, buffer, responseSender) -> {
            T packet = decoder.decode(buffer);
            FabricPacketContext packetContext = new FabricPacketContext(false, player);
            packetContext.execute(() -> handler.handle(packet, packetContext));
        });

        if (isClientEnvironment()) {
            FabricClientNetworking.registerClientReceiver(packetId, decoder, handler);
        }
    }
    
    
    @Override
    public <T extends Packet<T>> void sendToServer(T packet) {
        if (!isClientEnvironment()) {
            throw new IllegalStateException("sendToServer can only be called from client side");
        }
        
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        ResourceLocation packetId = new ResourceLocation(
            channelId.getNamespace(),
            channelId.getPath() + "/" + packet.getClass().getSimpleName().toLowerCase()
        );

        FabricClientNetworking.sendToServer(packetId, packet, registration.encoder);
    }
    
    @Override
    public <T extends Packet<T>> void sendToPlayer(T packet, ServerPlayer player) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        ResourceLocation packetId = new ResourceLocation(
            channelId.getNamespace(),
            channelId.getPath() + "/" + packet.getClass().getSimpleName().toLowerCase()
        );

        FriendlyByteBuf buffer = PacketByteBufs.create();
        registration.encoder.encode(packet, buffer);
        ServerPlayNetworking.send(player, packetId, buffer);
    }
    
    @Override
    public <T extends Packet<T>> void sendToAllPlayers(T packet) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        ResourceLocation packetId = new ResourceLocation(
            channelId.getNamespace(),
            channelId.getPath() + "/" + packet.getClass().getSimpleName().toLowerCase()
        );

        for (ServerPlayer player : PlayerLookup.all(null)) {
            FriendlyByteBuf buffer = PacketByteBufs.create();
            registration.encoder.encode(packet, buffer);
            ServerPlayNetworking.send(player, packetId, buffer);
        }
    }
    
    @Override
    public <T extends Packet<T>> void sendToAllPlayersExcept(T packet, ServerPlayer except) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        ResourceLocation packetId = new ResourceLocation(
            channelId.getNamespace(),
            channelId.getPath() + "/" + packet.getClass().getSimpleName().toLowerCase()
        );

        for (ServerPlayer player : PlayerLookup.all(except.getServer())) {
            if (!player.equals(except)) {
                FriendlyByteBuf buffer = PacketByteBufs.create();
                registration.encoder.encode(packet, buffer);
                ServerPlayNetworking.send(player, packetId, buffer);
            }
        }
    }
    
    private boolean isClientEnvironment() {
        try {
            return net.fabricmc.api.EnvType.CLIENT.equals(net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType());
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
