//? if >=1.21.11 || >=26.1 {
package com.iamkaf.amber.networking.v1;

import com.iamkaf.amber.api.billboard.v1.Billboards;
import com.iamkaf.amber.api.networking.v1.Packet;
import com.iamkaf.amber.api.networking.v1.PacketDecoder;
import com.iamkaf.amber.api.networking.v1.PacketEncoder;
import com.iamkaf.amber.api.networking.v1.PacketHandler;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record HideBillboardPacket(UUID billboardId) implements Packet<HideBillboardPacket> {
    public static final PacketEncoder<HideBillboardPacket> ENCODER =
            (packet, buffer) -> buffer.writeUUID(packet.billboardId);
    public static final PacketDecoder<HideBillboardPacket> DECODER =
            buffer -> new HideBillboardPacket(buffer.readUUID());
    public static final PacketHandler<HideBillboardPacket> HANDLER = (packet, context) -> {
        Player player = context.getPlayer();
        if (context.isClientSide() && player != null) {
            context.execute(() -> Billboards.hide(player, packet.billboardId));
        }
    };
}
//?}
