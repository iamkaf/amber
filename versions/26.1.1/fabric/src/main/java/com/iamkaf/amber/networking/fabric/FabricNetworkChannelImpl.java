package com.iamkaf.amber.networking.fabric;

import com.iamkaf.amber.api.networking.v1.*;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Fabric implementation of PlatformNetworkChannel.
 * Uses Fabric's networking API to handle cross-platform packet sending.
 */
public class FabricNetworkChannelImpl implements PlatformNetworkChannel {
    
    private final Identifier channelId;
    private final ConcurrentMap<Class<?>, PacketRegistration<? extends Packet<?>>> registrations = new ConcurrentHashMap<>();
    
    public FabricNetworkChannelImpl(Identifier channelId) {
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
        Identifier packetId = Identifier.fromNamespaceAndPath(
            channelId.getNamespace(), 
            channelId.getPath() + "/" + packetClass.getSimpleName().toLowerCase()
        );
        
        CustomPacketPayload.Type<FabricPacketWrapper<T>> payloadType = 
            new CustomPacketPayload.Type<>(packetId);
        
        // Create stream codec
        StreamCodec<FriendlyByteBuf, FabricPacketWrapper<T>> streamCodec = 
            StreamCodec.of(
                (buffer, wrapper) -> encoder.encode(wrapper.packet, buffer),
                buffer -> new FabricPacketWrapper<>(decoder.decode(buffer), payloadType)
            );
        
        // First register the payload type
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.clientboundPlay().register(payloadType, streamCodec);
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.serverboundPlay().register(payloadType, streamCodec);
        
        // Then register handlers
        // Register server-side receiver (receives packets from clients)
        ServerPlayNetworking.registerGlobalReceiver(payloadType, (payload, context) -> {
            FabricPacketContext packetContext = new FabricPacketContext(false, context.player());
            handler.handle(payload.packet, packetContext);
        });
        
        // Register client-side receiver (receives packets from server)
        if (isClientEnvironment()) {
            FabricClientNetworking.registerClientReceiver(payloadType, handler);
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
        
        Identifier packetId = Identifier.fromNamespaceAndPath(
            channelId.getNamespace(), 
            channelId.getPath() + "/" + packet.getClass().getSimpleName().toLowerCase()
        );
        
        CustomPacketPayload.Type<FabricPacketWrapper<T>> payloadType = 
            new CustomPacketPayload.Type<>(packetId);
        FabricPacketWrapper<T> wrapper = new FabricPacketWrapper<>(packet, payloadType);
        
        FabricClientNetworking.sendToServer(wrapper);
    }
    
    @Override
    public <T extends Packet<T>> void sendToPlayer(T packet, ServerPlayer player) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        Identifier packetId = Identifier.fromNamespaceAndPath(
            channelId.getNamespace(), 
            channelId.getPath() + "/" + packet.getClass().getSimpleName().toLowerCase()
        );
        
        CustomPacketPayload.Type<FabricPacketWrapper<T>> payloadType = 
            new CustomPacketPayload.Type<>(packetId);
        FabricPacketWrapper<T> wrapper = new FabricPacketWrapper<>(packet, payloadType);
        
        ServerPlayNetworking.send(player, wrapper);
    }
    
    @Override
    public <T extends Packet<T>> void sendToAllPlayers(T packet) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        Identifier packetId = Identifier.fromNamespaceAndPath(
            channelId.getNamespace(), 
            channelId.getPath() + "/" + packet.getClass().getSimpleName().toLowerCase()
        );
        
        CustomPacketPayload.Type<FabricPacketWrapper<T>> payloadType = 
            new CustomPacketPayload.Type<>(packetId);
        FabricPacketWrapper<T> wrapper = new FabricPacketWrapper<>(packet, payloadType);
        
        for (ServerPlayer player : PlayerLookup.all(null)) {
            ServerPlayNetworking.send(player, wrapper);
        }
    }
    
    @Override
    public <T extends Packet<T>> void sendToAllPlayersExcept(T packet, ServerPlayer except) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        Identifier packetId = Identifier.fromNamespaceAndPath(
            channelId.getNamespace(), 
            channelId.getPath() + "/" + packet.getClass().getSimpleName().toLowerCase()
        );
        
        CustomPacketPayload.Type<FabricPacketWrapper<T>> payloadType = 
            new CustomPacketPayload.Type<>(packetId);
        FabricPacketWrapper<T> wrapper = new FabricPacketWrapper<>(packet, payloadType);
        
        for (ServerPlayer player : PlayerLookup.all(((net.minecraft.server.level.ServerLevel)except.level()).getServer())) {
            if (!player.equals(except)) {
                ServerPlayNetworking.send(player, wrapper);
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
    
    /**
     * Wrapper for Fabric's CustomPacketPayload system.
     */
    public static class FabricPacketWrapper<T extends Packet<T>> implements CustomPacketPayload {
        public final T packet;
        private final Type<FabricPacketWrapper<T>> type;
        
        public FabricPacketWrapper(T packet, Type<FabricPacketWrapper<T>> type) {
            this.packet = packet;
            this.type = type;
        }
        
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return type;
        }
    }
}