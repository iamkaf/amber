package com.iamkaf.amber.client.billboard;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.billboard.v1.Billboard;
import com.iamkaf.amber.api.billboard.v1.BillboardAnchor;
import com.iamkaf.amber.api.billboard.v1.BillboardContent;
import com.iamkaf.amber.api.billboard.v1.BillboardDepthMode;
import com.iamkaf.amber.api.billboard.v1.BillboardTransition;
import com.iamkaf.amber.api.billboard.v1.Billboards;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Client-only storage and render submission for Amber billboards. */
public final class ClientBillboards {
    private static final double NANOS_PER_TICK = 50_000_000.0D;
    private static final Map<UUID, ActiveBillboard> ACTIVE = new ConcurrentHashMap<>();
    private static ClientLevel trackedLevel;
    private static Player trackedViewer;
    private static boolean activeCountWarningLogged;
    private static boolean capacityWarningLogged;

    private ClientBillboards() {
    }

    /** Returns the number of billboards retained for the current client world and player. */
    public static int activeCount() {
        return ACTIVE.size();
    }

    /** Returns the retained billboard for diagnostic tooling, or {@code null} when absent. */
    public static @Nullable Billboard activeBillboard(UUID billboardId) {
        ActiveBillboard active = ACTIVE.get(billboardId);
        return active == null ? null : active.billboard();
    }

    public static void show(Player viewer, Billboard billboard) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (!isActiveViewer(viewer, minecraft, level)) {
            return;
        }
        if (level != trackedLevel || viewer != trackedViewer) {
            clear();
            trackedLevel = level;
            trackedViewer = viewer;
        }
        double startsAt = animationTime();
        if (!ACTIVE.containsKey(billboard.id()) && !reserveCapacity(startsAt)) {
            return;
        }
        double expiresAt = billboard.durationTicks() == Billboard.PERSISTENT
                ? Double.POSITIVE_INFINITY
                : startsAt + billboard.durationTicks();
        ACTIVE.put(billboard.id(), new ActiveBillboard(billboard, startsAt, expiresAt, billboard.anchor()));
        warnAboutActiveCount();
    }

    public static void hide(Player viewer, UUID billboardId) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!isActiveViewer(viewer, minecraft, minecraft.level)) {
            return;
        }
        ACTIVE.remove(billboardId);
        resetCapacityWarningsIfRecovered();
    }

    public static void move(
            Player viewer,
            UUID billboardId,
            BillboardAnchor destination,
            BillboardTransition travel
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (!isActiveViewer(viewer, minecraft, level) || level != trackedLevel || viewer != trackedViewer) {
            return;
        }
        ActiveBillboard active = ACTIVE.get(billboardId);
        if (active == null) {
            return;
        }
        double now = animationTime();
        if (travel == null) {
            active.moveImmediately(destination);
            return;
        }
        BillboardAnchor source = active.travelSource(level, now);
        active.travel(source, destination, now, travel);
    }

    public static void scale(
            Player viewer,
            UUID billboardId,
            Vec3 destination,
            BillboardTransition transition
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (!isActiveViewer(viewer, minecraft, level) || level != trackedLevel || viewer != trackedViewer) {
            return;
        }
        ActiveBillboard active = ACTIVE.get(billboardId);
        if (active == null) {
            return;
        }
        double now = animationTime();
        if (transition == null) {
            active.scaleImmediately(destination);
        } else {
            active.scale(destination, now, transition);
        }
    }

    public static void render(PoseStack poseStack, BillboardDraw draw) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        Player viewer = minecraft.player;
        if (level == null || viewer == null) {
            clear();
            return;
        }
        if (level != trackedLevel || viewer != trackedViewer) {
            clear();
            trackedLevel = level;
            trackedViewer = viewer;
            return;
        }

        double renderTime = animationTime();
        ACTIVE.values().removeIf(active -> active.expiresAt() <= renderTime);
        resetCapacityWarningsIfRecovered();
        for (ActiveBillboard active : ACTIVE.values()) {
            submit(active, level, renderTime, poseStack, draw);
        }
    }

    private static double animationTime() {
        return System.nanoTime() / NANOS_PER_TICK;
    }

    private static Vec3 multiply(Vec3 left, Vec3 right) {
        return new Vec3(left.x * right.x, left.y * right.y, left.z * right.z);
    }

    private static void clear() {
        ACTIVE.clear();
        BillboardDraw.forgetEntities();
        trackedLevel = null;
        trackedViewer = null;
        activeCountWarningLogged = false;
        capacityWarningLogged = false;
    }

    private static boolean reserveCapacity(double now) {
        if (ACTIVE.size() < Billboards.MAX_ACTIVE_BILLBOARDS) {
            return true;
        }
        if (capacityWarningLogged) {
            return false;
        }

        ACTIVE.values().removeIf(active -> active.expiresAt() <= now);
        resetCapacityWarningsIfRecovered();
        if (ACTIVE.size() < Billboards.MAX_ACTIVE_BILLBOARDS) {
            return true;
        }

        if (!capacityWarningLogged) {
            logCapacityWarning();
            capacityWarningLogged = true;
        }
        return false;
    }

    private static void warnAboutActiveCount() {
        int activeCount = ACTIVE.size();
        if (!activeCountWarningLogged && activeCount >= Billboards.ACTIVE_BILLBOARD_WARNING_THRESHOLD) {
            logActiveCountWarning(activeCount);
            activeCountWarningLogged = true;
        }
    }

    private static void logActiveCountWarning(int activeCount) {
        Constants.LOG.warn(
                "Amber is tracking {} active billboards on the client. Performance may degrade as the count approaches the limit of {}.",
                activeCount,
                Billboards.MAX_ACTIVE_BILLBOARDS
        );
    }

    private static void logCapacityWarning() {
        Constants.LOG.warn(
                "Amber reached the client limit of {} active billboards. Skipping additional distinct billboards until capacity becomes available.",
                Billboards.MAX_ACTIVE_BILLBOARDS
        );
    }

    private static void resetCapacityWarningsIfRecovered() {
        int activeCount = ACTIVE.size();
        if (activeCount < Billboards.ACTIVE_BILLBOARD_WARNING_THRESHOLD) {
            activeCountWarningLogged = false;
        }
        if (activeCount < Billboards.MAX_ACTIVE_BILLBOARDS) {
            capacityWarningLogged = false;
        }
    }

    private static void submit(ActiveBillboard active, ClientLevel level, double renderTime, PoseStack poseStack, BillboardDraw draw) {
        Billboard billboard = active.billboard();
        double progress = billboard.durationTicks() == Billboard.PERSISTENT
                ? 0.0D
                : (renderTime - active.startsAt()) / billboard.durationTicks();
        Vec3 anchoredPosition = active.resolvePosition(level, renderTime);
        if (anchoredPosition == null) {
            return;
        }
        Vec3 position = anchoredPosition.add(billboard.animation().offsetAt(progress));
        Vec3 scale = multiply(active.resolveScale(renderTime), billboard.animation().scaleAt(progress));
        Vec3 rotation = billboard.rotation().add(billboard.animation().rotationAt(progress));
        float opacity = (float) Math.max(0.0D, Math.min(1.0D, billboard.opacity() * billboard.animation().opacityAt(progress)));
        boolean throughWalls = billboard.depthMode() == BillboardDepthMode.THROUGH_WALLS;
        Vec3 cameraPosition = draw.cameraPosition();
        poseStack.pushPose();
        poseStack.translate(
                position.x - cameraPosition.x,
                position.y - cameraPosition.y,
                position.z - cameraPosition.z
        );
        if (isCameraFacing(billboard.content())) {
            draw.faceCamera(poseStack);
        }
        BillboardDraw.rotate(poseStack, rotation);
        poseStack.scale(
                (float) Math.max(1.0E-6D, scale.x),
                (float) Math.max(1.0E-6D, scale.y),
                (float) Math.max(1.0E-6D, scale.z)
        );
        try {
            BillboardContent content = billboard.content();
            if (content instanceof BillboardContent.Texture texture) {
                submitTexture(texture, opacity, throughWalls, poseStack, draw);
            } else if (content instanceof BillboardContent.Item item) {
                submitItemModel(BillboardDraw.itemById(item.item()), item.scale(), false, opacity, throughWalls, poseStack, draw);
            } else if (content instanceof BillboardContent.ItemObject item) {
                submitItemModel(BillboardDraw.itemById(item.item()), item.scale(), true, opacity, throughWalls, poseStack, draw);
            } else if (content instanceof BillboardContent.BlockObject block) {
                submitItemModel(BillboardDraw.blockItemById(block.block()), block.scale(), true, opacity, throughWalls, poseStack, draw);
            } else if (content instanceof BillboardContent.Text text) {
                submitText(
                        text,
                        multiplyAlpha(billboard.animation().textColorAt(text.color(), progress), opacity),
                        throughWalls,
                        poseStack,
                        draw
                );
            } else {
                throw new IllegalArgumentException("Unknown billboard content type: " + content.getClass().getName());
            }
        } finally {
            poseStack.popPose();
        }
    }

    private static void submitTexture(BillboardContent.Texture texture, float opacity, boolean throughWalls, PoseStack poseStack, BillboardDraw draw) {
        draw.texture(poseStack, texture.texture(), texture.width() / 2.0F, texture.height() / 2.0F, white(opacity), throughWalls);
    }

    private static void submitItemModel(Item item, float itemScale, boolean worldOriented, float opacity, boolean throughWalls, PoseStack poseStack, BillboardDraw draw) {
        poseStack.scale(itemScale, itemScale, itemScale);
        draw.item(poseStack, item, worldOriented, opacity, throughWalls);
    }

    private static boolean isCameraFacing(BillboardContent content) {
        return content instanceof BillboardContent.Texture
                || content instanceof BillboardContent.Item
                || content instanceof BillboardContent.Text;
    }

    private static void submitText(BillboardContent.Text text, int color, boolean throughWalls, PoseStack poseStack, BillboardDraw draw) {
        Font font = Minecraft.getInstance().font;
        poseStack.scale(text.scale(), -text.scale(), text.scale());
        float x = -font.width(text.text()) / 2.0F;
        draw.text(poseStack, x, -font.lineHeight / 2.0F, text.text().getVisualOrderText(), color, throughWalls);
    }

    /** Same result as {@code ARGB.multiplyAlpha}, which older Minecraft versions lack. */
    static int multiplyAlpha(int argb, float alpha) {
        if (argb == 0 || alpha <= 0.0F) {
            return 0;
        }
        return alpha >= 1.0F ? argb : withAlpha(((argb >>> 24) / 255.0F) * alpha, argb);
    }

    /** Same result as {@code ARGB.white(float)}. */
    private static int white(float alpha) {
        return withAlpha(alpha, 0xFFFFFF);
    }

    private static int withAlpha(float alpha, int rgb) {
        return Mth.floor(alpha * 255.0F) << 24 | rgb & 0xFFFFFF;
    }

    private static boolean isActiveViewer(Player viewer, Minecraft minecraft, ClientLevel level) {
        return level != null && minecraft.player != null && viewer == minecraft.player && BillboardDraw.isInLevel(viewer, level);
    }

    private static @Nullable Vec3 resolveAnchor(ClientLevel level, BillboardAnchor anchor) {
        if (anchor instanceof BillboardAnchor.World world) {
            return world.position();
        }
        if (anchor instanceof BillboardAnchor.Entity bound) {
            Vec3 entityPosition = BillboardDraw.entityPosition(level, bound.entityId());
            return entityPosition == null ? null : entityPosition.add(bound.offset());
        }
        throw new IllegalArgumentException("Unknown billboard anchor type: " + anchor.getClass().getName());
    }

    private static final class ActiveBillboard {
        private final Billboard billboard;
        private final double startsAt;
        private final double expiresAt;
        private BillboardAnchor anchor;
        private ActiveTravel travel;
        private Vec3 scale;
        private ActiveScaleTransition scaleTransition;

        private ActiveBillboard(Billboard billboard, double startsAt, double expiresAt, BillboardAnchor anchor) {
            this.billboard = billboard;
            this.startsAt = startsAt;
            this.expiresAt = expiresAt;
            this.anchor = anchor;
            this.scale = billboard.scale();
        }

        private Billboard billboard() {
            return billboard;
        }

        private double startsAt() {
            return startsAt;
        }

        private double expiresAt() {
            return expiresAt;
        }

        private void moveImmediately(BillboardAnchor destination) {
            anchor = destination;
            travel = null;
        }

        private void travel(BillboardAnchor source, BillboardAnchor destination, double now, BillboardTransition spec) {
            anchor = source;
            travel = new ActiveTravel(source, destination, now, now + spec.durationTicks(), spec.easing());
        }

        private BillboardAnchor currentAnchor(double now) {
            if (travel != null && now >= travel.endsAt()) {
                anchor = travel.destination();
                travel = null;
            }
            return anchor;
        }

        private BillboardAnchor travelSource(ClientLevel level, double now) {
            currentAnchor(now);
            Vec3 displayedPosition = resolvePosition(level, now);
            return displayedPosition == null ? anchor : BillboardAnchor.world(displayedPosition);
        }

        private Vec3 resolvePosition(ClientLevel level, double now) {
            BillboardAnchor current = currentAnchor(now);
            if (travel == null) {
                return resolveAnchor(level, current);
            }
            Vec3 source = resolveAnchor(level, travel.source());
            Vec3 destination = resolveAnchor(level, travel.destination());
            if (source == null || destination == null) {
                return null;
            }
            double progress = (now - travel.startsAt()) / (travel.endsAt() - travel.startsAt());
            return source.lerp(destination, travel.easing().apply(progress));
        }

        private void scaleImmediately(Vec3 destination) {
            scale = destination;
            scaleTransition = null;
        }

        private void scale(Vec3 destination, double now, BillboardTransition spec) {
            Vec3 source = resolveScale(now);
            scale = source;
            scaleTransition = new ActiveScaleTransition(
                    source,
                    destination,
                    now,
                    now + spec.durationTicks(),
                    spec.easing()
            );
        }

        private Vec3 resolveScale(double now) {
            if (scaleTransition == null) {
                return scale;
            }
            if (now >= scaleTransition.endsAt()) {
                scale = scaleTransition.destination();
                scaleTransition = null;
                return scale;
            }
            double progress = (now - scaleTransition.startsAt())
                    / (scaleTransition.endsAt() - scaleTransition.startsAt());
            Vec3 interpolated = scaleTransition.source().lerp(
                    scaleTransition.destination(),
                    scaleTransition.easing().apply(progress)
            );
            return new Vec3(
                    Math.max(0.0D, interpolated.x),
                    Math.max(0.0D, interpolated.y),
                    Math.max(0.0D, interpolated.z)
            );
        }
    }

    private record ActiveTravel(
            BillboardAnchor source,
            BillboardAnchor destination,
            double startsAt,
            double endsAt,
            com.iamkaf.amber.api.billboard.v1.BillboardAnimation.Easing easing
    ) {
    }

    private record ActiveScaleTransition(
            Vec3 source,
            Vec3 destination,
            double startsAt,
            double endsAt,
            com.iamkaf.amber.api.billboard.v1.BillboardAnimation.Easing easing
    ) {
    }
}
