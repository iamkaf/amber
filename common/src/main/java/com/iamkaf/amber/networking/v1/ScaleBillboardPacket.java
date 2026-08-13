//? if >=1.21.11 || >=26.1 {
package com.iamkaf.amber.networking.v1;

import com.iamkaf.amber.api.billboard.v1.BillboardTransition;
import com.iamkaf.amber.api.billboard.v1.Billboards;
import com.iamkaf.amber.api.networking.v1.Packet;
import com.iamkaf.amber.api.networking.v1.PacketDecoder;
import com.iamkaf.amber.api.networking.v1.PacketEncoder;
import com.iamkaf.amber.api.networking.v1.PacketHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record ScaleBillboardPacket(
        UUID billboardId,
        Vec3 destination,
        @Nullable BillboardTransition transition
) implements Packet<ScaleBillboardPacket> {
    public static final PacketEncoder<ScaleBillboardPacket> ENCODER = (packet, buffer) -> {
        buffer.writeUUID(packet.billboardId);
        buffer.writeDouble(packet.destination.x);
        buffer.writeDouble(packet.destination.y);
        buffer.writeDouble(packet.destination.z);
        buffer.writeBoolean(packet.transition != null);
        if (packet.transition != null) {
            BillboardPacketCodec.encodeTransition(packet.transition, buffer);
        }
    };
    public static final PacketDecoder<ScaleBillboardPacket> DECODER = buffer -> new ScaleBillboardPacket(
            buffer.readUUID(),
            new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
            buffer.readBoolean() ? BillboardPacketCodec.decodeTransition(buffer) : null
    );
    public static final PacketHandler<ScaleBillboardPacket> HANDLER = (packet, context) -> {
        Player player = context.getPlayer();
        if (context.isClientSide() && player != null) {
            context.execute(() -> {
                if (packet.transition == null) {
                    Billboards.scale(player, packet.billboardId, packet.destination);
                } else {
                    Billboards.scaleOverTicks(
                            player,
                            packet.billboardId,
                            packet.destination,
                            packet.transition.durationTicks(),
                            packet.transition.easing()
                    );
                }
            });
        }
    };
}
//?}
