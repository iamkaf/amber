//? if >=1.21.11 || >=26.1 {
package com.iamkaf.amber.client.billboard;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.billboard.v1.Billboard;
import com.iamkaf.amber.api.billboard.v1.BillboardAnchor;
import com.iamkaf.amber.api.billboard.v1.BillboardContent;
import com.iamkaf.amber.api.billboard.v1.BillboardDepthMode;
import com.iamkaf.amber.api.billboard.v1.BillboardTransition;
import com.iamkaf.amber.api.billboard.v1.Billboards;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
//? if >=26.1 {
import net.minecraft.client.renderer.state.level.CameraRenderState;
//?} else {
/*import net.minecraft.client.renderer.state.CameraRenderState;*/
//?}
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Client-only storage and render submission for Amber billboards. */
public final class ClientBillboards {
    private static final int FULL_BRIGHT = 0x00F000F0;
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

    public static void render(PoseStack poseStack, SubmitNodeCollector output, CameraRenderState camera) {
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
            submit(active, level, renderTime, poseStack, output, camera);
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

    private static void submit(ActiveBillboard active, ClientLevel level, double renderTime, PoseStack poseStack, SubmitNodeCollector output, CameraRenderState camera) {
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
        Vec3 cameraPosition = camera.pos;
        poseStack.pushPose();
        poseStack.translate(
                position.x - cameraPosition.x,
                position.y - cameraPosition.y,
                position.z - cameraPosition.z
        );
        if (isCameraFacing(billboard.content())) {
            //? if >=26.3
            poseStack.rotate(camera.orientation);
            //? if <26.3
            /*poseStack.mulPose(camera.orientation);*/
        }
        //? if >=26.3 {
        poseStack.rotateDegrees(Axis.XP, (float) rotation.x);
        poseStack.rotateDegrees(Axis.YP, (float) rotation.y);
        poseStack.rotateDegrees(Axis.ZP, (float) rotation.z);
        //?} else {
        /*
        poseStack.mulPose(Axis.XP.rotationDegrees((float) rotation.x));
        poseStack.mulPose(Axis.YP.rotationDegrees((float) rotation.y));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) rotation.z));
        */
        //?}
        poseStack.scale(
                (float) Math.max(1.0E-6D, scale.x),
                (float) Math.max(1.0E-6D, scale.y),
                (float) Math.max(1.0E-6D, scale.z)
        );
        try {
            switch (billboard.content()) {
                case BillboardContent.Texture texture -> submitTexture(texture, opacity, throughWalls, poseStack, output);
                case BillboardContent.Item item -> submitItemModel(item.item(), item.scale(), ItemDisplayContext.FIXED, opacity, throughWalls, poseStack, output);
                case BillboardContent.ItemObject item -> submitItemModel(item.item(), item.scale(), ItemDisplayContext.GROUND, opacity, throughWalls, poseStack, output);
                case BillboardContent.BlockObject block -> {
                    net.minecraft.world.level.block.Block resolved = BuiltInRegistries.BLOCK.getValue(block.block());
                    if (resolved != null) {
                        Identifier itemId = BuiltInRegistries.ITEM.getKey(resolved.asItem());
                        if (itemId != null) {
                            submitItemModel(itemId, block.scale(), ItemDisplayContext.GROUND, opacity, throughWalls, poseStack, output);
                        }
                    }
                }
                case BillboardContent.Text text -> submitText(
                        text,
                        ARGB.multiplyAlpha(billboard.animation().textColorAt(text.color(), progress), opacity),
                        throughWalls,
                        poseStack,
                        output
                );
            }
        } finally {
            poseStack.popPose();
        }
    }

    private static void submitTexture(BillboardContent.Texture texture, float opacity, boolean throughWalls, PoseStack poseStack, SubmitNodeCollector output) {
        float halfWidth = texture.width() / 2.0F;
        float halfHeight = texture.height() / 2.0F;
        int color = ARGB.white(opacity);
        if (throughWalls) {
            output.submitCustomGeometry(poseStack, RenderTypes.textSeeThrough(texture.texture()), (pose, vertices) -> {
                vertices.addVertex(pose, -halfWidth, -halfHeight, 0.0F).setColor(color).setUv(0.0F, 1.0F).setLight(FULL_BRIGHT);
                vertices.addVertex(pose, halfWidth, -halfHeight, 0.0F).setColor(color).setUv(1.0F, 1.0F).setLight(FULL_BRIGHT);
                vertices.addVertex(pose, halfWidth, halfHeight, 0.0F).setColor(color).setUv(1.0F, 0.0F).setLight(FULL_BRIGHT);
                vertices.addVertex(pose, -halfWidth, halfHeight, 0.0F).setColor(color).setUv(0.0F, 0.0F).setLight(FULL_BRIGHT);
            });
        } else {
            output.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(texture.texture(), false), (pose, vertices) -> {
                vertices.addVertex(pose, -halfWidth, -halfHeight, 0.0F).setColor(color).setUv(0.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(pose, 0.0F, 0.0F, 1.0F);
                vertices.addVertex(pose, halfWidth, -halfHeight, 0.0F).setColor(color).setUv(1.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(pose, 0.0F, 0.0F, 1.0F);
                vertices.addVertex(pose, halfWidth, halfHeight, 0.0F).setColor(color).setUv(1.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(pose, 0.0F, 0.0F, 1.0F);
                vertices.addVertex(pose, -halfWidth, halfHeight, 0.0F).setColor(color).setUv(0.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(pose, 0.0F, 0.0F, 1.0F);
            });
        }
    }

    private static void submitItemModel(Identifier itemId, float itemScale, ItemDisplayContext displayContext, float opacity, boolean throughWalls, PoseStack poseStack, SubmitNodeCollector output) {
        Minecraft minecraft = Minecraft.getInstance();
        Player viewer = minecraft.player;
        if (viewer == null) {
            return;
        }
        net.minecraft.world.item.Item resolved = BuiltInRegistries.ITEM.getValue(itemId);
        if (resolved == null) {
            return;
        }
        ItemStackRenderState state = new ItemStackRenderState();
        minecraft.getItemModelResolver().updateForNonLiving(
                state,
                new ItemStack(resolved),
                displayContext,
                viewer
        );
        poseStack.scale(itemScale, itemScale, itemScale);
        if (displayContext == ItemDisplayContext.FIXED) {
            //? if >=26.3
            poseStack.rotate(Axis.YP, (float) Math.PI);
            //? if <26.3
            /*poseStack.mulPose(Axis.YP.rotation((float) Math.PI));*/
        }
        BillboardItemSubmitter.submit(state, poseStack, output, FULL_BRIGHT, OverlayTexture.NO_OVERLAY, throughWalls, opacity);
    }

    private static boolean isCameraFacing(BillboardContent content) {
        return content instanceof BillboardContent.Texture
                || content instanceof BillboardContent.Item
                || content instanceof BillboardContent.Text;
    }

    private static void submitText(BillboardContent.Text text, int color, boolean throughWalls, PoseStack poseStack, SubmitNodeCollector output) {
        Font font = Minecraft.getInstance().font;
        poseStack.scale(text.scale(), -text.scale(), text.scale());
        float x = -font.width(text.text()) / 2.0F;
        output.submitText(
                poseStack,
                x,
                -font.lineHeight / 2.0F,
                text.text().getVisualOrderText(),
                true,
                throughWalls ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.POLYGON_OFFSET,
                FULL_BRIGHT,
                color,
                0,
                0
        );
    }

    private static boolean isActiveViewer(Player viewer, Minecraft minecraft, ClientLevel level) {
        return level != null && minecraft.player != null && viewer == minecraft.player && viewer.level() == level;
    }

    private static float entityPartialTick(ClientLevel level, Entity entity) {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(
                !level.tickRateManager().isEntityFrozen(entity)
        );
    }

    private static Vec3 resolveAnchor(ClientLevel level, BillboardAnchor anchor) {
        return switch (anchor) {
            case BillboardAnchor.World world -> world.position();
            case BillboardAnchor.Entity bound -> {
                Entity entity = level.getEntity(bound.entityId());
                if (entity == null || entity.isRemoved()) {
                    yield null;
                }
                yield entity.getPosition(entityPartialTick(level, entity)).add(bound.offset());
            }
        };
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
//?}
