package com.iamkaf.amber.networking.forge;

import com.iamkaf.amber.api.networking.v1.Packet;
import com.iamkaf.amber.api.networking.v1.PacketHandler;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraftforge.fml.loading.FMLLoader;

/**
 * Client-only networking functionality for Forge.
 * This class is separated to avoid loading client-only classes on the server.
 */
public class ForgeClientNetworking {
    
    /**
     * Register a client-side packet receiver.
     * This method should only be called on the client side.
     */
    public static <T extends Packet<T>> void registerClientReceiver(
            CustomPacketPayload.Type<ForgePacketWrapper<T>> payloadType,
            PacketHandler<T> handler
    ) {
        if (!FMLLoader.getDist().isClient()) {
            throw new IllegalStateException("Client networking can only be registered on the client side");
        }
        
        // Client-side registration will be handled in the main channel implementation
        // This is a placeholder for potential future client-specific networking needs
    }
    
    /**
     * Send a packet to the server.
     * This method should only be called on the client side.
     */
    public static <T extends Packet<T>> void sendToServer(ForgePacketWrapper<T> wrapper) {
        if (!FMLLoader.getDist().isClient()) {
            throw new IllegalStateException("Packets can only be sent to server from the client side");
        }
        
        // The actual sending will be handled by the SimpleChannel in the main implementation
        // This is a placeholder for the actual sending logic
    }
}