package com.iamkaf.amber.networking.neoforge;

import com.iamkaf.amber.api.networking.v1.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * NeoForge implementation of PlatformNetworkChannel.
 * Uses NeoForge's CustomPacketPayload system with PayloadRegistrar.
 */
public class NeoForgeNetworkChannelImpl implements PlatformNetworkChannel {
    
    private final ResourceLocation channelId;
    private final ConcurrentMap<Class<?>, PacketRegistration<? extends Packet<?>>> registrations = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, PayloadTypePair<?>> packetToPayloadTypes = new ConcurrentHashMap<>();
    private PayloadRegistrar registrar;
    private boolean initialized = false;
    private static final ConcurrentMap<ResourceLocation, Boolean> registeredPayloads = new ConcurrentHashMap<>();
    
    public NeoForgeNetworkChannelImpl(ResourceLocation channelId) {
        this.channelId = channelId;
        // Note: PayloadRegistrar will be set later during RegisterPayloadHandlersEvent
    }
    
    /**
     * Sets the payload registrar for packet registration.
     * Called during the RegisterPayloadHandlersEvent.
     */
    public void setPayloadRegistrar(PayloadRegistrar registrar) {
        this.registrar = registrar;
        this.initialized = true;
        
        // Register any pending packets
        for (var entry : registrations.entrySet()) {
            registerPendingPacket(entry.getKey(), entry.getValue());
        }
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
        
        // Don't register immediately - wait for setPayloadRegistrar to be called
        // This prevents duplicate registration issues
    }
    
    @SuppressWarnings("unchecked")
    private <T extends Packet<T>> void registerPendingPacket(Class<?> packetClass, PacketRegistration<?> registration) {
        PacketRegistration<T> typedRegistration = (PacketRegistration<T>) registration;
        @SuppressWarnings("unchecked")
        Class<T> typedPacketClass = (Class<T>) packetClass;
        
        // Create separate packet types for each direction
        ResourceLocation c2sPacketId = ResourceLocation.fromNamespaceAndPath(
            channelId.getNamespace(), 
            channelId.getPath() + "/" + packetClass.getSimpleName().toLowerCase() + "_c2s"
        );
        ResourceLocation s2cPacketId = ResourceLocation.fromNamespaceAndPath(
            channelId.getNamespace(), 
            channelId.getPath() + "/" + packetClass.getSimpleName().toLowerCase() + "_s2c"
        );
        
        // Check if these payloads have already been registered
        if (registeredPayloads.putIfAbsent(c2sPacketId, true) != null || 
            registeredPayloads.putIfAbsent(s2cPacketId, true) != null) {
            // Already registered, skip
            return;
        }
        
        CustomPacketPayload.Type<NeoForgePacketWrapper<T>> c2sPayloadType = 
            new CustomPacketPayload.Type<>(c2sPacketId);
        CustomPacketPayload.Type<NeoForgePacketWrapper<T>> s2cPayloadType = 
            new CustomPacketPayload.Type<>(s2cPacketId);
        
        // Create stream codec
        StreamCodec<FriendlyByteBuf, NeoForgePacketWrapper<T>> c2sStreamCodec = 
            StreamCodec.of(
                (buffer, wrapper) -> typedRegistration.encoder.encode(wrapper.packet, buffer),
                buffer -> new NeoForgePacketWrapper<>(typedRegistration.decoder.decode(buffer), c2sPayloadType)
            );
        StreamCodec<FriendlyByteBuf, NeoForgePacketWrapper<T>> s2cStreamCodec = 
            StreamCodec.of(
                (buffer, wrapper) -> typedRegistration.encoder.encode(wrapper.packet, buffer),
                buffer -> new NeoForgePacketWrapper<>(typedRegistration.decoder.decode(buffer), s2cPayloadType)
            );
        
        // Register client-to-server communication
        registrar.playToServer(
            c2sPayloadType,
            c2sStreamCodec,
            (payload, context) -> {
                NeoForgePacketContext packetContext = new NeoForgePacketContext(false, context.player());
                typedRegistration.handler.handle(payload.packet, packetContext);
            }
        );
        
        // Register server-to-client communication
        registrar.playToClient(
            s2cPayloadType,
            s2cStreamCodec,
            (payload, context) -> {
                NeoForgePacketContext packetContext = new NeoForgePacketContext(true, context.player());
                typedRegistration.handler.handle(payload.packet, packetContext);
            }
        );
        
        // Store both payload types for later use in sending
        packetToPayloadTypes.put(packetClass, new PayloadTypePair<>(c2sPayloadType, s2cPayloadType));
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
        
        @SuppressWarnings("unchecked")
        PayloadTypePair<T> payloadTypes = (PayloadTypePair<T>) packetToPayloadTypes.get(packet.getClass());
        if (payloadTypes == null) {
            throw new IllegalArgumentException("Payload types not found for packet: " + packet.getClass().getName());
        }
        
        // Use the client-to-server payload type
        NeoForgePacketWrapper<T> wrapper = new NeoForgePacketWrapper<>(packet, payloadTypes.c2sType);
        
        // Send to server using client connection
        if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient()) {
            net.minecraft.client.Minecraft.getInstance().getConnection().send(wrapper);
        } else {
            throw new IllegalStateException("sendToServer can only be called from client side");
        }
    }
    
    @Override
    public <T extends Packet<T>> void sendToPlayer(T packet, ServerPlayer player) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        @SuppressWarnings("unchecked")
        PayloadTypePair<T> payloadTypes = (PayloadTypePair<T>) packetToPayloadTypes.get(packet.getClass());
        if (payloadTypes == null) {
            throw new IllegalArgumentException("Payload types not found for packet: " + packet.getClass().getName());
        }
        
        // Use the server-to-client payload type
        NeoForgePacketWrapper<T> wrapper = new NeoForgePacketWrapper<>(packet, payloadTypes.s2cType);
        
        // Send to specific player using their connection
        player.connection.send(wrapper);
    }
    
    @Override
    public <T extends Packet<T>> void sendToAllPlayers(T packet) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        @SuppressWarnings("unchecked")
        PayloadTypePair<T> payloadTypes = (PayloadTypePair<T>) packetToPayloadTypes.get(packet.getClass());
        if (payloadTypes == null) {
            throw new IllegalArgumentException("Payload types not found for packet: " + packet.getClass().getName());
        }
        
        // Use the server-to-client payload type
        NeoForgePacketWrapper<T> wrapper = new NeoForgePacketWrapper<>(packet, payloadTypes.s2cType);
        
        // Send to all players by iterating through player list
        // We need a server reference for this
        throw new UnsupportedOperationException("sendToAllPlayers requires server access - not yet implemented");
    }
    
    @Override
    public <T extends Packet<T>> void sendToAllPlayersExcept(T packet, ServerPlayer except) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
        
        @SuppressWarnings("unchecked")
        PayloadTypePair<T> payloadTypes = (PayloadTypePair<T>) packetToPayloadTypes.get(packet.getClass());
        if (payloadTypes == null) {
            throw new IllegalArgumentException("Payload types not found for packet: " + packet.getClass().getName());
        }
        
        // Use the server-to-client payload type
        NeoForgePacketWrapper<T> wrapper = new NeoForgePacketWrapper<>(packet, payloadTypes.s2cType);
        
        // Send to all players on the server, excluding the specified player
        if (except.getServer() != null) {
            for (ServerPlayer player : except.getServer().getPlayerList().getPlayers()) {
                if (!player.equals(except)) {
                    // Send to specific player using their connection
                    player.connection.send(wrapper);
                }
            }
        }
    }
    
    private boolean isClientSide() {
        try {
            return net.neoforged.fml.loading.FMLEnvironment.dist.isClient();
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
     * Wrapper for NeoForge's CustomPacketPayload system.
     */
    public static class NeoForgePacketWrapper<T extends Packet<T>> implements CustomPacketPayload {
        public final T packet;
        private final Type<NeoForgePacketWrapper<T>> type;
        
        public NeoForgePacketWrapper(T packet, Type<NeoForgePacketWrapper<T>> type) {
            this.packet = packet;
            this.type = type;
        }
        
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return type;
        }
    }
    
    /**
     * Holds both client-to-server and server-to-client payload types for bidirectional communication.
     */
    private static class PayloadTypePair<T extends Packet<T>> {
        final CustomPacketPayload.Type<NeoForgePacketWrapper<T>> c2sType;
        final CustomPacketPayload.Type<NeoForgePacketWrapper<T>> s2cType;
        
        PayloadTypePair(CustomPacketPayload.Type<NeoForgePacketWrapper<T>> c2sType, 
                       CustomPacketPayload.Type<NeoForgePacketWrapper<T>> s2cType) {
            this.c2sType = c2sType;
            this.s2cType = s2cType;
        }
    }
}