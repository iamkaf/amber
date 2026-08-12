//? if >=1.21.11 || >=26.1 {
package com.iamkaf.amber.api.billboard.v1;

import com.iamkaf.amber.client.billboard.ClientBillboards;
import com.iamkaf.amber.networking.v1.AmberNetworking;
import com.iamkaf.amber.networking.v1.HideBillboardPacket;
import com.iamkaf.amber.networking.v1.MoveBillboardPacket;
import com.iamkaf.amber.networking.v1.ScaleBillboardPacket;
import com.iamkaf.amber.networking.v1.ShowBillboardPacket;
import com.iamkaf.amber.util.Env;
import com.iamkaf.amber.util.EnvExecutor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.UUID;

/**
 * Side-safe commands for displaying in-world billboards to one player.
 *
 * <p>The same call may be made from common code. A local client player updates the renderer
 * immediately; a server player receives the same operation through Amber's internal network
 * channel.</p>
 */
public final class Billboards {
    private Billboards() {
    }

    public static BillboardDispatch show(Player viewer, Billboard billboard) {
        Objects.requireNonNull(viewer, "viewer");
        Objects.requireNonNull(billboard, "billboard");
        if (viewer instanceof ServerPlayer serverPlayer) {
            if (isUnavailable(serverPlayer)) {
                return BillboardDispatch.IGNORED;
            }
            AmberNetworking.CHANNEL.sendToPlayer(new ShowBillboardPacket(billboard), serverPlayer);
            return BillboardDispatch.CLIENTBOUND_PACKET;
        }

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> ClientBillboards.show(viewer, billboard));
        return BillboardDispatch.IMMEDIATE;
    }

    public static BillboardDispatch hide(Player viewer, Billboard billboard) {
        Objects.requireNonNull(billboard, "billboard");
        return hide(viewer, billboard.id());
    }

    public static BillboardDispatch hide(Player viewer, UUID billboardId) {
        Objects.requireNonNull(viewer, "viewer");
        Objects.requireNonNull(billboardId, "billboardId");
        if (viewer instanceof ServerPlayer serverPlayer) {
            if (isUnavailable(serverPlayer)) {
                return BillboardDispatch.IGNORED;
            }
            AmberNetworking.CHANNEL.sendToPlayer(new HideBillboardPacket(billboardId), serverPlayer);
            return BillboardDispatch.CLIENTBOUND_PACKET;
        }

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> ClientBillboards.hide(viewer, billboardId));
        return BillboardDispatch.IMMEDIATE;
    }

    /** Instantly moves a visible billboard to an absolute world position. */
    public static BillboardDispatch move(Player viewer, Billboard billboard, Vec3 position) {
        Objects.requireNonNull(billboard, "billboard");
        return move(viewer, billboard.id(), position);
    }

    /** Instantly moves a visible billboard identity to an absolute world position. */
    public static BillboardDispatch move(Player viewer, UUID billboardId, Vec3 position) {
        return move(viewer, billboardId, BillboardAnchor.world(position));
    }

    /** Instantly changes the anchor of a visible billboard. */
    public static BillboardDispatch move(Player viewer, UUID billboardId, BillboardAnchor anchor) {
        return updateAnchor(viewer, billboardId, anchor, null);
    }

    /** Moves a visible billboard to an absolute world position over presentation-time ticks. */
    public static BillboardDispatch moveOverTicks(
            Player viewer,
            Billboard billboard,
            Vec3 position,
            int ticks,
            BillboardAnimation.Easing easing
    ) {
        Objects.requireNonNull(billboard, "billboard");
        return moveOverTicks(viewer, billboard.id(), position, ticks, easing);
    }

    /** Moves a visible billboard identity to an absolute world position over presentation-time ticks. */
    public static BillboardDispatch moveOverTicks(
            Player viewer,
            UUID billboardId,
            Vec3 position,
            int ticks,
            BillboardAnimation.Easing easing
    ) {
        return moveOverTicks(viewer, billboardId, BillboardAnchor.world(position), ticks, easing);
    }

    /** Changes a visible billboard's anchor over presentation-time ticks. */
    public static BillboardDispatch moveOverTicks(
            Player viewer,
            UUID billboardId,
            BillboardAnchor destination,
            int ticks,
            BillboardAnimation.Easing easing
    ) {
        return updateAnchor(viewer, billboardId, destination, new BillboardTransition(ticks, easing));
    }

    /** Instantly binds a visible billboard to an entity with a world-axis offset. */
    public static BillboardDispatch bind(Player viewer, Billboard billboard, Entity entity, Vec3 offset) {
        Objects.requireNonNull(billboard, "billboard");
        return bind(viewer, billboard.id(), entity, offset);
    }

    /** Instantly binds a visible billboard identity to an entity with a world-axis offset. */
    public static BillboardDispatch bind(Player viewer, UUID billboardId, Entity entity, Vec3 offset) {
        return move(viewer, billboardId, BillboardAnchor.entity(entity, offset));
    }

    /** Smoothly transfers a visible billboard to an entity-relative anchor. */
    public static BillboardDispatch bindOverTicks(
            Player viewer,
            Billboard billboard,
            Entity entity,
            Vec3 offset,
            int ticks,
            BillboardAnimation.Easing easing
    ) {
        Objects.requireNonNull(billboard, "billboard");
        return bindOverTicks(viewer, billboard.id(), entity, offset, ticks, easing);
    }

    /** Smoothly transfers a visible billboard identity to an entity-relative anchor. */
    public static BillboardDispatch bindOverTicks(
            Player viewer,
            UUID billboardId,
            Entity entity,
            Vec3 offset,
            int ticks,
            BillboardAnimation.Easing easing
    ) {
        return moveOverTicks(viewer, billboardId, BillboardAnchor.entity(entity, offset), ticks, easing);
    }

    /** Instantly changes a visible billboard to a uniform transform scale. */
    public static BillboardDispatch scale(Player viewer, Billboard billboard, double scale) {
        Objects.requireNonNull(billboard, "billboard");
        return scale(viewer, billboard.id(), scale);
    }

    /** Instantly changes a visible billboard identity to a uniform transform scale. */
    public static BillboardDispatch scale(Player viewer, UUID billboardId, double scale) {
        return scale(viewer, billboardId, new Vec3(scale, scale, scale));
    }

    /** Instantly changes a visible billboard to a local-axis transform scale. */
    public static BillboardDispatch scale(Player viewer, Billboard billboard, Vec3 scale) {
        Objects.requireNonNull(billboard, "billboard");
        return scale(viewer, billboard.id(), scale);
    }

    /** Instantly changes a visible billboard identity to a local-axis transform scale. */
    public static BillboardDispatch scale(Player viewer, UUID billboardId, Vec3 scale) {
        return updateScale(viewer, billboardId, scale, null);
    }

    /** Animates a visible billboard to a uniform transform scale. */
    public static BillboardDispatch scaleOverTicks(
            Player viewer,
            Billboard billboard,
            double scale,
            int ticks,
            BillboardAnimation.Easing easing
    ) {
        Objects.requireNonNull(billboard, "billboard");
        return scaleOverTicks(viewer, billboard.id(), scale, ticks, easing);
    }

    /** Animates a visible billboard identity to a uniform transform scale. */
    public static BillboardDispatch scaleOverTicks(
            Player viewer,
            UUID billboardId,
            double scale,
            int ticks,
            BillboardAnimation.Easing easing
    ) {
        return scaleOverTicks(viewer, billboardId, new Vec3(scale, scale, scale), ticks, easing);
    }

    /** Animates a visible billboard to a local-axis squash/stretch scale. */
    public static BillboardDispatch scaleOverTicks(
            Player viewer,
            Billboard billboard,
            Vec3 scale,
            int ticks,
            BillboardAnimation.Easing easing
    ) {
        Objects.requireNonNull(billboard, "billboard");
        return scaleOverTicks(viewer, billboard.id(), scale, ticks, easing);
    }

    /** Animates a visible billboard identity to a local-axis squash/stretch scale. */
    public static BillboardDispatch scaleOverTicks(
            Player viewer,
            UUID billboardId,
            Vec3 scale,
            int ticks,
            BillboardAnimation.Easing easing
    ) {
        return updateScale(viewer, billboardId, scale, new BillboardTransition(ticks, easing));
    }

    private static BillboardDispatch updateAnchor(
            Player viewer,
            UUID billboardId,
            BillboardAnchor anchor,
            BillboardTransition travel
    ) {
        Objects.requireNonNull(viewer, "viewer");
        Objects.requireNonNull(billboardId, "billboardId");
        Objects.requireNonNull(anchor, "anchor");
        if (viewer instanceof ServerPlayer serverPlayer) {
            if (isUnavailable(serverPlayer)) {
                return BillboardDispatch.IGNORED;
            }
            AmberNetworking.CHANNEL.sendToPlayer(new MoveBillboardPacket(billboardId, anchor, travel), serverPlayer);
            return BillboardDispatch.CLIENTBOUND_PACKET;
        }

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> ClientBillboards.move(viewer, billboardId, anchor, travel));
        return BillboardDispatch.IMMEDIATE;
    }

    private static BillboardDispatch updateScale(
            Player viewer,
            UUID billboardId,
            Vec3 scale,
            BillboardTransition transition
    ) {
        Objects.requireNonNull(viewer, "viewer");
        Objects.requireNonNull(billboardId, "billboardId");
        requireScale(scale);
        if (viewer instanceof ServerPlayer serverPlayer) {
            if (isUnavailable(serverPlayer)) {
                return BillboardDispatch.IGNORED;
            }
            AmberNetworking.CHANNEL.sendToPlayer(
                    new ScaleBillboardPacket(billboardId, scale, transition),
                    serverPlayer
            );
            return BillboardDispatch.CLIENTBOUND_PACKET;
        }

        EnvExecutor.runInEnv(
                Env.CLIENT,
                () -> () -> ClientBillboards.scale(viewer, billboardId, scale, transition)
        );
        return BillboardDispatch.IMMEDIATE;
    }

    private static void requireScale(Vec3 scale) {
        Objects.requireNonNull(scale, "scale");
        if (!Double.isFinite(scale.x) || !Double.isFinite(scale.y) || !Double.isFinite(scale.z)
                || scale.x < 0.0D || scale.y < 0.0D || scale.z < 0.0D) {
            throw new IllegalArgumentException("scale must be finite and non-negative");
        }
    }

    private static boolean isUnavailable(ServerPlayer player) {
        return player.hasDisconnected() || player.isRemoved();
    }
}
//?}
