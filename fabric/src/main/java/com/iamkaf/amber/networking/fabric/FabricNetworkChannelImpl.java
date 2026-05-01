package com.iamkaf.amber.networking.fabric;

import com.iamkaf.amber.api.networking.v1.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
//? if >=1.20.5
import net.minecraft.network.codec.StreamCodec;
//? if >=1.20.5
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Fabric implementation of PlatformNetworkChannel.
 * Uses Fabric's networking API to handle cross-platform packet sending.
 */
public class FabricNetworkChannelImpl implements PlatformNetworkChannel {
    private static final AtomicBoolean SERVER_TRACKING_REGISTERED = new AtomicBoolean();
    private static volatile MinecraftServer currentServer;
    
    private final Identifier channelId;
    private final ConcurrentMap<Class<?>, PacketRegistration<? extends Packet<?>>> registrations = new ConcurrentHashMap<>();
    
    public FabricNetworkChannelImpl(Identifier channelId) {
        this.channelId = channelId;
        registerServerTracking();
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
        //? if <1.20.5 {
        throw new UnsupportedOperationException("Amber networking requires Minecraft 1.20.5+ on Fabric");
        //?} else {
        
        // Create packet type for this packet class
        Identifier packetId = id(
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
        //? if >=26.1 {
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.clientboundPlay().register(payloadType, streamCodec);
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.serverboundPlay().register(payloadType, streamCodec);
        //?} else {
        /*net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playS2C().register(payloadType, streamCodec);
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playC2S().register(payloadType, streamCodec);
        *///?}
        
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
        //?}
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
        //? if <1.20.5 {
        throw new UnsupportedOperationException("Amber networking requires Minecraft 1.20.5+ on Fabric");
        //?} else {
        
        Identifier packetId = id(
            channelId.getNamespace(), 
            channelId.getPath() + "/" + packet.getClass().getSimpleName().toLowerCase()
        );
        
        CustomPacketPayload.Type<FabricPacketWrapper<T>> payloadType = 
            new CustomPacketPayload.Type<>(packetId);
        FabricPacketWrapper<T> wrapper = new FabricPacketWrapper<>(packet, payloadType);
        
        FabricClientNetworking.sendToServer(wrapper);
        //?}
    }
    
    @Override
    public <T extends Packet<T>> void sendToPlayer(T packet, ServerPlayer player) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        //? if <1.20.5 {
        throw new UnsupportedOperationException("Amber networking requires Minecraft 1.20.5+ on Fabric");
        //?} else {
        
        Identifier packetId = id(
            channelId.getNamespace(), 
            channelId.getPath() + "/" + packet.getClass().getSimpleName().toLowerCase()
        );
        
        CustomPacketPayload.Type<FabricPacketWrapper<T>> payloadType = 
            new CustomPacketPayload.Type<>(packetId);
        FabricPacketWrapper<T> wrapper = new FabricPacketWrapper<>(packet, payloadType);
        
        ServerPlayNetworking.send(player, wrapper);
        //?}
    }
    
    @Override
    public <T extends Packet<T>> void sendToAllPlayers(T packet) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        //? if <1.20.5 {
        throw new UnsupportedOperationException("Amber networking requires Minecraft 1.20.5+ on Fabric");
        //?} else {
        
        Identifier packetId = id(
            channelId.getNamespace(), 
            channelId.getPath() + "/" + packet.getClass().getSimpleName().toLowerCase()
        );
        
        CustomPacketPayload.Type<FabricPacketWrapper<T>> payloadType = 
            new CustomPacketPayload.Type<>(packetId);
        FabricPacketWrapper<T> wrapper = new FabricPacketWrapper<>(packet, payloadType);
        
        MinecraftServer server = requireCurrentServer();
        for (ServerPlayer player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, wrapper);
        }
        //?}
    }
    
    @Override
    public <T extends Packet<T>> void sendToAllPlayersExcept(T packet, ServerPlayer except) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        //? if <1.20.5 {
        throw new UnsupportedOperationException("Amber networking requires Minecraft 1.20.5+ on Fabric");
        //?} else {
        
        Identifier packetId = id(
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
        //?}
    }
    
    private boolean isClientEnvironment() {
        try {
            return net.fabricmc.api.EnvType.CLIENT.equals(net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType());
        } catch (Exception e) {
            return false;
        }
    }

    private static void registerServerTracking() {
        if (SERVER_TRACKING_REGISTERED.compareAndSet(false, true)) {
            ServerLifecycleEvents.SERVER_STARTED.register(server -> currentServer = server);
            ServerLifecycleEvents.SERVER_STOPPED.register(server -> currentServer = null);
        }
    }

    private static MinecraftServer requireCurrentServer() {
        MinecraftServer server = currentServer;
        if (server == null) {
            throw new IllegalStateException("No active Minecraft server is available for broadcast networking");
        }
        return server;
    }

    private static Identifier id(String namespace, String path) {
        //? if >=1.21
        return Identifier.fromNamespaceAndPath(namespace, path);
        //? if <1.21
        /*return new Identifier(namespace, path);*/
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
    //? if >=1.20.5 {
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
    //?}
}
