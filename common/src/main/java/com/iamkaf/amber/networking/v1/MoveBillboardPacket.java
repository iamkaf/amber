//? if >=1.21.11 || >=26.1 {
package com.iamkaf.amber.networking.v1;

import com.iamkaf.amber.api.billboard.v1.BillboardAnchor;
import com.iamkaf.amber.api.billboard.v1.BillboardTransition;
import com.iamkaf.amber.api.billboard.v1.Billboards;
import com.iamkaf.amber.api.networking.v1.Packet;
import com.iamkaf.amber.api.networking.v1.PacketDecoder;
import com.iamkaf.amber.api.networking.v1.PacketEncoder;
import com.iamkaf.amber.api.networking.v1.PacketHandler;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record MoveBillboardPacket(
        UUID billboardId,
        BillboardAnchor destination,
        @Nullable BillboardTransition travel
) implements Packet<MoveBillboardPacket> {
    public static final PacketEncoder<MoveBillboardPacket> ENCODER = (packet, buffer) -> {
        buffer.writeUUID(packet.billboardId);
        BillboardPacketCodec.encodeAnchor(packet.destination, buffer);
        buffer.writeBoolean(packet.travel != null);
        if (packet.travel != null) {
            BillboardPacketCodec.encodeTransition(packet.travel, buffer);
        }
    };
    public static final PacketDecoder<MoveBillboardPacket> DECODER = buffer -> new MoveBillboardPacket(
            buffer.readUUID(),
            BillboardPacketCodec.decodeAnchor(buffer),
            buffer.readBoolean() ? BillboardPacketCodec.decodeTransition(buffer) : null
    );
    public static final PacketHandler<MoveBillboardPacket> HANDLER = (packet, context) -> {
        Player player = context.getPlayer();
        if (context.isClientSide() && player != null) {
            context.execute(() -> {
                if (packet.travel == null) {
                    Billboards.move(player, packet.billboardId, packet.destination);
                } else {
                    Billboards.moveOverTicks(
                            player,
                            packet.billboardId,
                            packet.destination,
                            packet.travel.durationTicks(),
                            packet.travel.easing()
                    );
                }
            });
        }
    };
}
//?}
