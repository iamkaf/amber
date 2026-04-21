package com.iamkaf.amber.networking.forge;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.networking.v1.Packet;
import com.iamkaf.amber.api.networking.v1.PacketDecoder;
import com.iamkaf.amber.api.networking.v1.PacketEncoder;
import com.iamkaf.amber.api.networking.v1.PacketHandler;
import com.iamkaf.amber.api.networking.v1.PlatformNetworkChannel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 1.20.1 still uses NetworkRegistry.newSimpleChannel and the old simple package.
 */
public class ForgeNetworkChannelImpl implements PlatformNetworkChannel {

    private static final String PROTOCOL_VERSION = "1";

    private final ResourceLocation channelId;
    private final SimpleChannel channel;
    private final AtomicInteger nextMessageId = new AtomicInteger();
    private final ConcurrentMap<Class<?>, PacketRegistration<? extends Packet<?>>> registrations = new ConcurrentHashMap<>();

    public ForgeNetworkChannelImpl(ResourceLocation channelId) {
        this.channelId = channelId;
        this.channel = NetworkRegistry.newSimpleChannel(
            channelId,
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
        );
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

        channel.messageBuilder(packetClass, nextMessageId.getAndIncrement())
            .decoder(decoder::decode)
            .encoder(encoder::encode)
            .consumerMainThread((packet, contextSupplier) -> {
                ServerPlayer sender = contextSupplier.get().getSender();
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

        ensureRegistered(packet);
        Constants.LOG.debug("Forge: Sending {} to server", packet.getClass().getSimpleName());
        channel.send(PacketDistributor.SERVER.noArg(), packet);
    }

    @Override
    public <T extends Packet<T>> void sendToPlayer(T packet, ServerPlayer player) {
        ensureRegistered(packet);
        Constants.LOG.debug("Forge: Sending {} to player {}", packet.getClass().getSimpleName(), player.getName().getString());
        channel.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    @Override
    public <T extends Packet<T>> void sendToAllPlayers(T packet) {
        ensureRegistered(packet);
        Constants.LOG.debug("Forge: Sending {} to all players", packet.getClass().getSimpleName());
        channel.send(PacketDistributor.ALL.noArg(), packet);
    }

    @Override
    public <T extends Packet<T>> void sendToAllPlayersExcept(T packet, ServerPlayer except) {
        ensureRegistered(packet);
        Constants.LOG.debug("Forge: Sending {} to all players except {}", packet.getClass().getSimpleName(), except.getName().getString());

        {
            net.minecraft.server.level.ServerLevel serverLevel = except.getLevel();
            for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
                if (!player.equals(except)) {
                    channel.send(PacketDistributor.PLAYER.with(() -> player), packet);
                }
            }
        }
    }

    private <T extends Packet<T>> void ensureRegistered(T packet) {
        @SuppressWarnings("unchecked")
        PacketRegistration<T> registration = (PacketRegistration<T>) registrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalArgumentException("Packet not registered: " + packet.getClass().getName());
        }
    }

    private boolean isClientSide() {
        try {
            return net.minecraftforge.fml.loading.FMLLoader.getDist().isClient();
        } catch (Exception e) {
            return false;
        }
    }

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
