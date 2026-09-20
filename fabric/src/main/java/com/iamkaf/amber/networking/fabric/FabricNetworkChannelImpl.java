package com.iamkaf.amber.networking.fabric;

import com.iamkaf.amber.api.networking.v1.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
//? if >=1.20.5
import net.minecraft.network.codec.StreamCodec;
//? if >=1.20.5
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FabricNetworkChannelImpl implements PlatformNetworkChannel {
    private static final AtomicBoolean SERVER_TRACKING_REGISTERED = new AtomicBoolean();
    private static volatile MinecraftServer currentServer;
    private final Identifier channelId;
    private final boolean optional;
    private final ConcurrentMap<Class<?>, PacketEncoder<?>> encoders = new ConcurrentHashMap<>();

    public FabricNetworkChannelImpl(Identifier channelId) {
        this(channelId, false);
    }

    public FabricNetworkChannelImpl(Identifier channelId, boolean optional) {
        this.channelId = channelId;
        this.optional = optional;
        if (SERVER_TRACKING_REGISTERED.compareAndSet(false, true)) {
            ServerLifecycleEvents.SERVER_STARTED.register(server -> currentServer = server);
            ServerLifecycleEvents.SERVER_STOPPED.register(server -> currentServer = null);
        }
    }

    private Identifier packetId(Class<?> packetClass) {
        String path = channelId.getPath() + "/" + packetClass.getSimpleName().toLowerCase(Locale.ROOT);
        //? if >=1.21
        return Identifier.fromNamespaceAndPath(channelId.getNamespace(), path);
        //? if <1.21
        /*return new Identifier(channelId.getNamespace(), path);*/
    }

    @Override
    public <T extends Packet<T>> void register(Class<T> packetClass, PacketEncoder<T> encoder,
                                             PacketDecoder<T> decoder, PacketHandler<T> handler) {
        if (encoders.putIfAbsent(packetClass, encoder) != null) {
            throw new IllegalArgumentException("Packet already registered: " + packetClass.getName());
        }
        Identifier packetId = packetId(packetClass);
        //? if >=1.20.5 {
        CustomPacketPayload.Type<FabricPacketWrapper<T>> type = new CustomPacketPayload.Type<>(packetId);
        StreamCodec<FriendlyByteBuf, FabricPacketWrapper<T>> codec = StreamCodec.of(
                (buffer, wrapper) -> encoder.encode(wrapper.packet, buffer),
                buffer -> new FabricPacketWrapper<>(decoder.decode(buffer), type));
        //? if >=26.1 {
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.clientboundPlay().register(type, codec);
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.serverboundPlay().register(type, codec);
        //?} else {
        /*net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playS2C().register(type, codec);
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playC2S().register(type, codec);*/
        //?}
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) ->
                handler.handle(payload.packet, new FabricPacketContext(false, context.player())));
        if (isClientEnvironment()) FabricClientNetworking.registerClientReceiver(type, handler);
        //?} else {
        /*throw new UnsupportedOperationException("Amber networking requires Minecraft 1.20.5+ on Fabric");*/
        //?}
    }

    @Override
    public PeerAvailability serverAvailability() {
        if (!isClientEnvironment() || !FabricClientNetworking.ready()) return PeerAvailability.PENDING;
        if (encoders.isEmpty()) return PeerAvailability.PENDING;
        for (Class<?> type : encoders.keySet()) {
            if (!FabricClientNetworking.canSend(packetId(type))) return PeerAvailability.ABSENT;
        }
        return PeerAvailability.SUPPORTED;
    }

    @Override
    public PeerAvailability playerAvailability(ServerPlayer player) {
        if (encoders.isEmpty()) return PeerAvailability.PENDING;
        for (Class<?> type : encoders.keySet()) {
            if (!ServerPlayNetworking.canSend(player, packetId(type))) return PeerAvailability.ABSENT;
        }
        return PeerAvailability.SUPPORTED;
    }

    @Override
    public <T extends Packet<T>> void sendToServer(T packet) {
        if (!isClientEnvironment()) throw new IllegalStateException("sendToServer requires a client");
        PacketEncoder<T> encoder = encoder(packet);
        if (optional && serverAvailability() != PeerAvailability.SUPPORTED) return;
        //? if >=1.20.5 {
        FabricClientNetworking.sendToServer(new FabricPacketWrapper<>(packet,
                new CustomPacketPayload.Type<>(packetId(packet.getClass()))));
        //?} else {
        /*throw new UnsupportedOperationException("Amber networking requires Minecraft 1.20.5+ on Fabric");*/
        //?}
    }

    @Override
    public <T extends Packet<T>> void sendToPlayer(T packet, ServerPlayer player) {
        PacketEncoder<T> encoder = encoder(packet);
        if (optional && playerAvailability(player) != PeerAvailability.SUPPORTED) return;
        //? if >=1.20.5 {
        ServerPlayNetworking.send(player, new FabricPacketWrapper<>(packet,
                new CustomPacketPayload.Type<>(packetId(packet.getClass()))));
        //?} else {
        /*throw new UnsupportedOperationException("Amber networking requires Minecraft 1.20.5+ on Fabric");*/
        //?}
    }

    @Override
    public <T extends Packet<T>> void sendToAllPlayers(T packet) {
        MinecraftServer server = currentServer;
        if (server == null) throw new IllegalStateException("No active server for broadcast networking");
        for (ServerPlayer player : server.getPlayerList().getPlayers()) sendToPlayer(packet, player);
    }

    @Override
    public <T extends Packet<T>> void sendToAllPlayersExcept(T packet, ServerPlayer except) {
        MinecraftServer server = currentServer;
        if (server == null) throw new IllegalStateException("No active server for broadcast networking");
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (!player.equals(except)) sendToPlayer(packet, player);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Packet<T>> PacketEncoder<T> encoder(T packet) {
        // Registration associates this exact packet class with its encoder.
        PacketEncoder<T> encoder = (PacketEncoder<T>) encoders.get(packet.getClass());
        if (encoder == null) throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        return encoder;
    }

    private static boolean isClientEnvironment() {
        return net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT;
    }

    //? if >=1.20.5 {
    public static class FabricPacketWrapper<T extends Packet<T>> implements CustomPacketPayload {
        public final T packet;
        private final Type<FabricPacketWrapper<T>> type;
        public FabricPacketWrapper(T packet, Type<FabricPacketWrapper<T>> type) {
            this.packet = packet;
            this.type = type;
        }
        @Override
        public Type<? extends CustomPacketPayload> type() { return type; }
    }
    //?}
}
