package com.iamkaf.amber.networking.v1;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.networking.v1.Packet;
import com.iamkaf.amber.api.networking.v1.PacketContext;
import com.iamkaf.amber.api.networking.v1.PacketDecoder;
import com.iamkaf.amber.api.networking.v1.PacketEncoder;
import com.iamkaf.amber.api.networking.v1.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Ping packet for Amber's networking diagnostics.
 * Sent from server to client to measure latency and connectivity.
 *
 * @param timestamp the timestamp when the ping was sent
 * @param message the message payload for debugging purposes
 */
public record PingPacket(long timestamp, String message) implements Packet<PingPacket> {
    
    public static final PacketEncoder<PingPacket> ENCODER = (packet, buffer) -> {
        buffer.writeVarLong(packet.timestamp);
        buffer.writeUtf(packet.message);
    };
    
    public static final PacketDecoder<PingPacket> DECODER = buffer -> {
        long timestamp = buffer.readVarLong();
        String message = buffer.readUtf();
        return new PingPacket(timestamp, message);
    };
    
    public static final PacketHandler<PingPacket> HANDLER = (packet, context) -> {
        if (context.isClientSide()) {
            // Handle on client side: respond with pong
            context.execute(() -> {
                Constants.LOG.debug("Amber received ping: {} at {}", packet.message(), packet.timestamp());
                
                // Calculate response time and send pong
                long currentTime = System.currentTimeMillis();
                long responseTime = currentTime - packet.timestamp();
                
                try {
                    PongPacket pongResponse = new PongPacket(
                        packet.timestamp(), 
                        currentTime, 
                        "Pong: " + packet.message()
                    );
                    
                    AmberNetworking.sendToServer(pongResponse);
                    
                    Constants.LOG.debug("Amber sent pong response (response time: {}ms)", responseTime);
                } catch (Exception e) {
                    Constants.LOG.error("Failed to send pong response", e);
                }
            });
        } else {
            Constants.LOG.warn("PingPacket received on server side - this should not happen");
        }
    };
}