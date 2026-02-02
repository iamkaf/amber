package com.iamkaf.mochila.network;

import com.iamkaf.amber.api.networking.v1.NetworkChannel;
import com.iamkaf.mochila.Constants;

public class MochilaNetworking {
    public static final NetworkChannel CHANNEL = NetworkChannel.create(Constants.resource("main"));

    public static void init() {
        CHANNEL.register(
                OpenBackpackPacket.class,
                OpenBackpackPacket::encode,
                OpenBackpackPacket::decode,
                OpenBackpackPacket::handle
        );
        CHANNEL.register(
                OpenEnderBackpackPacket.class,
                OpenEnderBackpackPacket::encode,
                OpenEnderBackpackPacket::decode,
                OpenEnderBackpackPacket::handle
        );
        CHANNEL.register(
                ChangeBackpackModePacket.class,
                ChangeBackpackModePacket::encode,
                ChangeBackpackModePacket::decode,
                ChangeBackpackModePacket::handle
        );
    }
}
