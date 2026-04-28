package com.iamkaf.amber.networking.forge;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.networking.v1.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
//? if >=1.20.2 {
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;
//?} else {
/*import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;*/
//?}
import net.minecraftforge.network.PacketDistributor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * Forge implementation of PlatformNetworkChannel.
 * Uses Forge's ChannelBuilder and SimpleChannel networking system for proper cross-platform packet sending.
 */
public class ForgeNetworkChannelImpl implements PlatformNetworkChannel {
    
    private static final int PROTOCOL_VERSION = 1;
    
    private final Identifier channelId;
    private final SimpleChannel channel;
    private final ConcurrentMap<Class<?>, PacketRegistration<? extends Packet<?>>> registrations = new ConcurrentHashMap<>();
    
    public ForgeNetworkChannelImpl(Identifier channelId) {
        this.channelId = channelId;
        //? if >=1.20.2 {
        this.channel = ChannelBuilder.named(channelId)
            .networkProtocolVersion(PROTOCOL_VERSION)
            .clientAcceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION))
            .serverAcceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION))
            .simpleChannel();
        //?} else {
        /*String protocolVersion = Integer.toString(PROTOCOL_VERSION);
        this.channel = NetworkRegistry.ChannelBuilder.named(channelId)
            .networkProtocolVersion(() -> protocolVersion)
            .clientAcceptedVersions(protocolVersion::equals)
            .serverAcceptedVersions(protocolVersion::equals)
            .simpleChannel();*/
        //?}
    }
    
    @Override
    public <T extends Packet<T>> void register(
            Class<T> packetClass,
            PacketEncoder<T> encoder,
            PacketDecoder<T> decoder,
            PacketHandler<T> handler
    ) {
        PacketRegistration<T> registration = new PacketRegistration<>(encoder, decoder, handler);
        int discriminator = registrations.size();
        registrations.put(packetClass, registration);
        
        // Register bidirectional packet handling using the deprecated MessageBuilder API
        // This is needed for compatibility with the current Forge version
        channel.messageBuilder(packetClass, discriminator)
            .decoder(buffer -> decoder.decode(buffer))
            .encoder((packet, buffer) -> encoder.encode(packet, buffer))
            .consumerMainThread((packet, context) -> {
                //? if >=1.20.2
                ServerPlayer sender = context.getSender();
                //? if <1.20.2
                /*ServerPlayer sender = context.get().getSender();*/
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
        //? if >=1.20.2
        channel.send(packet, PacketDistributor.SERVER.noArg());
        //? if <1.20.2
        /*channel.send(PacketDistributor.SERVER.noArg(), packet);*/
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
        //? if >=1.20.2
        channel.send(packet, PacketDistributor.PLAYER.with(player));
        //? if <1.20.2
        /*channel.send(PacketDistributor.PLAYER.with(() -> player), packet);*/
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
        //? if >=1.20.2
        channel.send(packet, PacketDistributor.ALL.noArg());
        //? if <1.20.2
        /*channel.send(PacketDistributor.ALL.noArg(), packet);*/
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
        //? if >=1.20
        if (except.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
        //? if <1.20
        /*if (except.level instanceof net.minecraft.server.level.ServerLevel serverLevel) {*/
            for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
                if (!player.equals(except)) {
                    //? if >=1.20.2
                    channel.send(packet, PacketDistributor.PLAYER.with(player));
                    //? if <1.20.2
                    /*channel.send(PacketDistributor.PLAYER.with(() -> player), packet);*/
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
