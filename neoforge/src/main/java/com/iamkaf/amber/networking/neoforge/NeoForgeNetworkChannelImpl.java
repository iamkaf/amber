package com.iamkaf.amber.networking.neoforge;

import com.iamkaf.amber.api.networking.v1.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * NeoForge implementation of PlatformNetworkChannel.
 * Uses NeoForge's CustomPacketPayload system with PayloadRegistrar.
 */
public class NeoForgeNetworkChannelImpl implements PlatformNetworkChannel {
    private static final Set<PayloadIds> REGISTERED_PAYLOADS = ConcurrentHashMap.newKeySet();

    private final Identifier channelId;
    private final ConcurrentMap<Class<?>, PacketRegistration<? extends Packet<?>>> registrations = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, PayloadTypePair<?>> packetToPayloadTypes = new ConcurrentHashMap<>();
    private PayloadRegistrar registrar;

    public NeoForgeNetworkChannelImpl(Identifier channelId) {
        this.channelId = channelId;
    }

    /**
     * Sets the payload registrar for packet registration.
     * Called during the RegisterPayloadHandlersEvent.
     */
    public synchronized void setPayloadRegistrar(PayloadRegistrar registrar) {
        this.registrar = registrar;

        for (var entry : registrations.entrySet()) {
            registerPendingPacket(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public synchronized <T extends Packet<T>> void register(
            Class<T> packetClass,
            PacketEncoder<T> encoder,
            PacketDecoder<T> decoder,
            PacketHandler<T> handler
    ) {
        PacketRegistration<T> registration = new PacketRegistration<>(encoder, decoder, handler);
        registrations.put(packetClass, registration);

        if (registrar != null) {
            registerPendingPacket(packetClass, registration);
        }
    }
    
    @SuppressWarnings("unchecked")
    private <T extends Packet<T>> void registerPendingPacket(Class<?> packetClass, PacketRegistration<?> registration) {
        PacketRegistration<T> typedRegistration = (PacketRegistration<T>) registration;

        Identifier c2sPacketId = Identifier.fromNamespaceAndPath(
            channelId.getNamespace(), 
            channelId.getPath() + "/" + packetClass.getSimpleName().toLowerCase(Locale.ROOT) + "_c2s"
        );
        Identifier s2cPacketId = Identifier.fromNamespaceAndPath(
            channelId.getNamespace(), 
            channelId.getPath() + "/" + packetClass.getSimpleName().toLowerCase(Locale.ROOT) + "_s2c"
        );

        if (!REGISTERED_PAYLOADS.add(new PayloadIds(c2sPacketId, s2cPacketId))) {
            return;
        }
        
        CustomPacketPayload.Type<NeoForgePacketWrapper<T>> c2sPayloadType = 
            new CustomPacketPayload.Type<>(c2sPacketId);
        CustomPacketPayload.Type<NeoForgePacketWrapper<T>> s2cPayloadType = 
            new CustomPacketPayload.Type<>(s2cPacketId);
        
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
        
        registrar.playToServer(
            c2sPayloadType,
            c2sStreamCodec,
            (payload, context) -> {
                NeoForgePacketContext packetContext = new NeoForgePacketContext(false, context.player());
                typedRegistration.handler.handle(payload.packet, packetContext);
            }
        );
        
        registrar.playToClient(
            s2cPayloadType,
            s2cStreamCodec,
            (payload, context) -> {
                NeoForgePacketContext packetContext = new NeoForgePacketContext(true, context.player());
                typedRegistration.handler.handle(payload.packet, packetContext);
            }
        );
        
        packetToPayloadTypes.put(packetClass, new PayloadTypePair<>(c2sPayloadType, s2cPayloadType));
    }
    
    @Override
    public <T extends Packet<T>> void sendToServer(T packet) {
        if (!isClientSide()) {
            throw new IllegalStateException("sendToServer can only be called from client side");
        }
        
        PayloadTypePair<T> payloadTypes = payloadTypes(packet);
        NeoForgePacketWrapper<T> wrapper = new NeoForgePacketWrapper<>(packet, payloadTypes.c2sType);

        //? if >=1.21.9
        if (net.neoforged.fml.loading.FMLEnvironment.getDist().isClient()) {
        //? if <1.21.9
        /*if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient()) {*/
            net.minecraft.client.Minecraft.getInstance().getConnection().send(wrapper);
        } else {
            throw new IllegalStateException("sendToServer can only be called from client side");
        }
    }
    
    @Override
    public <T extends Packet<T>> void sendToPlayer(T packet, ServerPlayer player) {
        PayloadTypePair<T> payloadTypes = payloadTypes(packet);
        NeoForgePacketWrapper<T> wrapper = new NeoForgePacketWrapper<>(packet, payloadTypes.s2cType);
        player.connection.send(wrapper);
    }
    
    @Override
    public <T extends Packet<T>> void sendToAllPlayers(T packet) {
        PayloadTypePair<T> payloadTypes = payloadTypes(packet);
        NeoForgePacketWrapper<T> wrapper = new NeoForgePacketWrapper<>(packet, payloadTypes.s2cType);
        PacketDistributor.sendToAllPlayers(wrapper);
    }
    
    @Override
    public <T extends Packet<T>> void sendToAllPlayersExcept(T packet, ServerPlayer except) {
        PayloadTypePair<T> payloadTypes = payloadTypes(packet);
        NeoForgePacketWrapper<T> wrapper = new NeoForgePacketWrapper<>(packet, payloadTypes.s2cType);

        if (except.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
                if (!player.equals(except)) {
                    player.connection.send(wrapper);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Packet<T>> PayloadTypePair<T> payloadTypes(T packet) {
        PayloadTypePair<T> payloadTypes = (PayloadTypePair<T>) packetToPayloadTypes.get(packet.getClass());
        if (payloadTypes == null) {
            throw new IllegalArgumentException("Packet is not registered: " + packet.getClass().getName());
        }
        return payloadTypes;
    }

    private boolean isClientSide() {
        try {
            //? if >=1.21.9
            return net.neoforged.fml.loading.FMLEnvironment.getDist().isClient();
            //? if <1.21.9
            /*return net.neoforged.fml.loading.FMLEnvironment.dist.isClient();*/
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

    private record PayloadIds(Identifier clientToServer, Identifier serverToClient) {
    }
}
