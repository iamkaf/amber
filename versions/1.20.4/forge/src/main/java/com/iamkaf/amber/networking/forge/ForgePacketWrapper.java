package com.iamkaf.amber.networking.forge;

import com.iamkaf.amber.api.networking.v1.Packet;

/**
 * Legacy placeholder for the pre-payload Forge networking band.
 * 1.20.4 uses SimpleChannel directly and does not need a CustomPacketPayload wrapper.
 */
public class ForgePacketWrapper<T extends Packet<T>> {
    public final T packet;

    public ForgePacketWrapper(T packet) {
        this.packet = packet;
    }
}
