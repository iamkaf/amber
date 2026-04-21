package com.iamkaf.amber.networking.v1;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.networking.v1.Packet;
import com.iamkaf.amber.api.networking.v1.PacketContext;
import com.iamkaf.amber.api.networking.v1.PacketDecoder;
import com.iamkaf.amber.api.networking.v1.PacketEncoder;
import com.iamkaf.amber.api.networking.v1.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Pong packet for Amber's networking diagnostics.
 * Sent from client to server in response to a PingPacket.
 */
public record PongPacket(long originalTimestamp, long responseTimestamp, String message) implements Packet<PongPacket> {
    
    public static final PacketEncoder<PongPacket> ENCODER = (packet, buffer) -> {
        buffer.writeVarLong(packet.originalTimestamp);
        buffer.writeVarLong(packet.responseTimestamp);
        buffer.writeUtf(packet.message);
    };
    
    public static final PacketDecoder<PongPacket> DECODER = buffer -> {
        long originalTimestamp = buffer.readVarLong();
        long responseTimestamp = buffer.readVarLong();
        String message = buffer.readUtf();
        return new PongPacket(originalTimestamp, responseTimestamp, message);
    };
    
    public static final PacketHandler<PongPacket> HANDLER = (packet, context) -> {
        if (!context.isClientSide()) {
            // Handle on server side: record latency metrics
            context.execute(() -> {
                long currentTime = System.currentTimeMillis();
                long totalRoundTrip = currentTime - packet.originalTimestamp();
                long clientProcessingTime = packet.responseTimestamp() - packet.originalTimestamp();
                long networkLatency = totalRoundTrip - clientProcessingTime;
                
                Constants.LOG.debug("Amber received pong: {}", packet.message());
                Constants.LOG.debug("  Total round-trip time: {}ms", totalRoundTrip);
                Constants.LOG.debug("  Client processing time: {}ms", clientProcessingTime);
                Constants.LOG.debug("  Estimated network latency: {}ms", networkLatency);
                
                // Store latency metrics
                AmberNetworking.recordLatency(totalRoundTrip, networkLatency);
            });
        } else {
            Constants.LOG.warn("PongPacket received on client side - this should not happen");
        }
    };
}