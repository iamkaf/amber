//? if >=1.21.11 || >=26.1 {
package com.iamkaf.amber.networking.v1;

import com.iamkaf.amber.api.billboard.v1.Billboard;
import com.iamkaf.amber.api.billboard.v1.Billboards;
import com.iamkaf.amber.api.networking.v1.Packet;
import com.iamkaf.amber.api.networking.v1.PacketDecoder;
import com.iamkaf.amber.api.networking.v1.PacketEncoder;
import com.iamkaf.amber.api.networking.v1.PacketHandler;
import net.minecraft.world.entity.player.Player;

public record ShowBillboardPacket(Billboard billboard) implements Packet<ShowBillboardPacket> {
    public static final PacketEncoder<ShowBillboardPacket> ENCODER =
            (packet, buffer) -> BillboardPacketCodec.encode(packet.billboard, buffer);
    public static final PacketDecoder<ShowBillboardPacket> DECODER =
            buffer -> new ShowBillboardPacket(BillboardPacketCodec.decode(buffer));
    public static final PacketHandler<ShowBillboardPacket> HANDLER = (packet, context) -> {
        Player player = context.getPlayer();
        if (context.isClientSide() && player != null) {
            context.execute(() -> Billboards.show(player, packet.billboard));
        }
    };
}
//?}
