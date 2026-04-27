package com.iamkaf.amber.networking.forge;

import com.iamkaf.amber.api.networking.v1.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Wrapper for Forge's SimpleChannel packet system.
 * This class wraps our Packet interface to work with Forge's networking.
 */
public class ForgePacketWrapper<T extends Packet<T>> implements CustomPacketPayload {
    
    public final T packet;
    private final CustomPacketPayload.Type<ForgePacketWrapper<T>> type;
    
    public ForgePacketWrapper(T packet, CustomPacketPayload.Type<ForgePacketWrapper<T>> type) {
        this.packet = packet;
        this.type = type;
    }
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return type;
    }
}