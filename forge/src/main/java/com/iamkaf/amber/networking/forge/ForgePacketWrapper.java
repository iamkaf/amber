package com.iamkaf.amber.networking.forge;

import com.iamkaf.amber.api.networking.v1.Packet;
//? if >1.20.4 {
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?}

/**
 * Wrapper for Forge's SimpleChannel packet system.
 * This class wraps our Packet interface to work with Forge's networking.
 */
//? if <=1.20.4 {
/*public class ForgePacketWrapper<T extends Packet<T>> {*/
//?} else {
public class ForgePacketWrapper<T extends Packet<T>> implements CustomPacketPayload {
//?}
    
    public final T packet;
    //? if >1.20.4 {
    private final CustomPacketPayload.Type<ForgePacketWrapper<T>> type;
    //?}
    
    //? if <=1.20.4 {
    /*public ForgePacketWrapper(T packet) {*/
    //?} else {
    public ForgePacketWrapper(T packet, CustomPacketPayload.Type<ForgePacketWrapper<T>> type) {
    //?}
        this.packet = packet;
        //? if >1.20.4 {
        this.type = type;
        //?}
    }
    
    //? if >1.20.4 {
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return type;
    }
    //?}
}
