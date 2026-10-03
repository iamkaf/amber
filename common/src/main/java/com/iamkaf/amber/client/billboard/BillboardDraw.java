package com.iamkaf.amber.client.billboard;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
//? if >=1.21.9 {
import net.minecraft.client.renderer.SubmitNodeCollector;
//?} else {
/*import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
*///?}
//? if >=26.1 {
import net.minecraft.client.renderer.state.level.CameraRenderState;
//?} else if >=1.21.9 {
/*import net.minecraft.client.renderer.state.CameraRenderState;
*///?}
//? if >=1.21.4
import net.minecraft.client.renderer.item.ItemStackRenderState;
//? if >=1.19.4 {
import net.minecraft.world.item.ItemDisplayContext;
//?} else {
/*import net.minecraft.client.renderer.block.model.ItemTransforms;
*///?}
//? if >=1.19.3 {
import com.mojang.math.Axis;
import net.minecraft.core.registries.BuiltInRegistries;
import org.joml.Quaternionf;
//?} else {
/*import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.core.Registry;
*///?}
//? if <1.21.5 {
/*import java.util.HashMap;
import java.util.Map;
*///?}

import java.util.UUID;

/**
 * Minecraft rendering and world lookups for one frame of billboards.
 *
 * <p>{@link ClientBillboards} owns billboard state and transforms. This class owns every call whose
 * shape depends on the Minecraft version.</p>
 */
public final class BillboardDraw {
    private static final int FULL_BRIGHT = 0x00F000F0;
    //? if <1.21.5 {
    /*// Level.getEntity(UUID) arrives in 1.21.5. Older client levels only expose the rendered entity list.
    private static final Map<UUID, Entity> ENTITIES = new HashMap<>();
    private static @Nullable ClientLevel entitiesLevel;
    *///?}

    //? if >=1.21.9 {
    private final SubmitNodeCollector output;
    //?} else {
    /*private final MultiBufferSource output;
    *///?}
    private final Vec3 cameraPosition;
    //? if >=1.19.3 {
    private final Quaternionf cameraRotation;
    //?} else {
    /*private final Quaternion cameraRotation;
    *///?}

    //? if >=1.21.9 {
    public BillboardDraw(SubmitNodeCollector output, CameraRenderState camera) {
        this.output = output;
        this.cameraPosition = camera.pos;
        this.cameraRotation = camera.orientation;
    }
    //?} else {
    /*public BillboardDraw(MultiBufferSource output, Camera camera) {
        this.output = output;
        this.cameraPosition = camera.getPosition();
        this.cameraRotation = camera.rotation();
    }
    *///?}

    Vec3 cameraPosition() {
        return cameraPosition;
    }

    void faceCamera(PoseStack poseStack) {
        //? if >=26.3
        poseStack.rotate(cameraRotation);
        //? if <26.3
        /*poseStack.mulPose(cameraRotation);*/
    }

    /** Applies local X, then Y, then Z rotations in degrees. */
    static void rotate(PoseStack poseStack, Vec3 degrees) {
        //? if >=26.3 {
        poseStack.rotateDegrees(Axis.XP, (float) degrees.x);
        poseStack.rotateDegrees(Axis.YP, (float) degrees.y);
        poseStack.rotateDegrees(Axis.ZP, (float) degrees.z);
        //?} else if >=1.19.3 {
        /*poseStack.mulPose(Axis.XP.rotationDegrees((float) degrees.x));
        poseStack.mulPose(Axis.YP.rotationDegrees((float) degrees.y));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) degrees.z));
        *///?} else {
        /*poseStack.mulPose(Vector3f.XP.rotationDegrees((float) degrees.x));
        poseStack.mulPose(Vector3f.YP.rotationDegrees((float) degrees.y));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees((float) degrees.z));
        *///?}
    }

    /** Draws a textured quad centered on the pose origin. */
    void texture(PoseStack poseStack, Identifier texture, float halfWidth, float halfHeight, int color, boolean throughWalls) {
        //? if >=1.21.9 {
        if (throughWalls) {
            output.submitCustomGeometry(poseStack, BillboardRenderTypes.seeThrough(texture),
                    (pose, vertices) -> quad(pose, vertices, halfWidth, halfHeight, color, false));
        } else {
            output.submitCustomGeometry(poseStack, BillboardRenderTypes.translucent(texture),
                    (pose, vertices) -> quad(pose, vertices, halfWidth, halfHeight, color, true));
        }
        //?} else {
        /*if (throughWalls) {
            quad(poseStack.last(), output.getBuffer(BillboardRenderTypes.seeThrough(texture)), halfWidth, halfHeight, color, false);
        } else {
            quad(poseStack.last(), output.getBuffer(BillboardRenderTypes.translucent(texture)), halfWidth, halfHeight, color, true);
        }
        *///?}
    }

    /** Draws an item model, using the dropped-item transform when world oriented. */
    void item(PoseStack poseStack, Item item, boolean worldOriented, float opacity, boolean throughWalls) {
        Minecraft minecraft = Minecraft.getInstance();
        Player viewer = minecraft.player;
        if (viewer == null) {
            return;
        }
        //? if >=1.19.4 {
        ItemDisplayContext context = worldOriented ? ItemDisplayContext.GROUND : ItemDisplayContext.FIXED;
        //?} else {
        /*ItemTransforms.TransformType context = worldOriented ? ItemTransforms.TransformType.GROUND : ItemTransforms.TransformType.FIXED;
        *///?}
        //? if >=1.21.4 {
        ItemStackRenderState state = new ItemStackRenderState();
        minecraft.getItemModelResolver().updateForNonLiving(state, new ItemStack(item), context, viewer);
        //?}
        if (!worldOriented) {
            //? if >=26.3 {
            poseStack.rotate(Axis.YP, (float) Math.PI);
            //?} else if >=1.19.3 {
            /*poseStack.mulPose(Axis.YP.rotation((float) Math.PI));
            *///?} else {
            /*poseStack.mulPose(Vector3f.YP.rotation((float) Math.PI));
            *///?}
        }
        //? if >=1.21.9 {
        BillboardItemSubmitter.submit(state, poseStack, output, FULL_BRIGHT, OverlayTexture.NO_OVERLAY, throughWalls, opacity);
        //?} else if >=1.21.4 {
        /*state.render(poseStack, BillboardItemSubmitter.buffers(output, throughWalls, opacity), FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        *///?} else {
        /*minecraft.getItemRenderer().renderStatic(
                null,
                new ItemStack(item),
                context,
                false,
                poseStack,
                BillboardItemSubmitter.buffers(output, throughWalls, opacity),
                minecraft.level,
                FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                viewer.getId()
        );
        *///?}
    }

    /** Draws shadowed full-bright text with its top-left corner at {@code x, y}. */
    void text(PoseStack poseStack, float x, float y, FormattedCharSequence text, int color, boolean throughWalls) {
        //? if <1.21.6 {
        /*// Font draws colors with alpha below 4 as opaque here, so a fade-out would flash at its end.
        if ((color & 0xFC000000) == 0) {
            return;
        }
        *///?}
        //? if >=1.21.9 {
        output.submitText(
                poseStack,
                x,
                y,
                text,
                true,
                throughWalls ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.POLYGON_OFFSET,
                FULL_BRIGHT,
                color,
                0,
                0
        );
        //?} else if >=1.19.4 {
        /*Minecraft.getInstance().font.drawInBatch(
                text,
                x,
                y,
                color,
                true,
                poseStack.last().pose(),
                output,
                throughWalls ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.POLYGON_OFFSET,
                0,
                FULL_BRIGHT
        );
        *///?} else {
        /*Minecraft.getInstance().font.drawInBatch(text, x, y, color, true, poseStack.last().pose(), output, throughWalls, 0, FULL_BRIGHT);
        *///?}
    }

    static Item itemById(Identifier id) {
        //? if >=1.21.2 {
        return BuiltInRegistries.ITEM.getValue(id);
        //?} else if >=1.19.3 {
        /*return BuiltInRegistries.ITEM.get(id);
        *///?} else {
        /*return Registry.ITEM.get(id);
        *///?}
    }

    static Item blockItemById(Identifier blockId) {
        //? if >=1.21.2 {
        return BuiltInRegistries.BLOCK.getValue(blockId).asItem();
        //?} else if >=1.19.3 {
        /*return BuiltInRegistries.BLOCK.get(blockId).asItem();
        *///?} else {
        /*return Registry.BLOCK.get(blockId).asItem();
        *///?}
    }

    static boolean isInLevel(Entity entity, Level level) {
        //? if >=1.20
        return entity.level() == level;
        //? if <1.20
        /*return entity.level == level;*/
    }

    /** Returns the entity's interpolated position for this frame, or {@code null} while it is not tracked. */
    static @Nullable Vec3 entityPosition(ClientLevel level, UUID entityId) {
        Entity entity = findEntity(level, entityId);
        if (entity == null || entity.isRemoved()) {
            return null;
        }
        return entity.getPosition(partialTick(level, entity));
    }

    private static @Nullable Entity findEntity(ClientLevel level, UUID entityId) {
        //? if >=1.21.5 {
        return level.getEntity(entityId);
        //?} else {
        /*if (level != entitiesLevel) {
            ENTITIES.clear();
            entitiesLevel = level;
        }
        Entity cached = ENTITIES.get(entityId);
        if (cached != null && !cached.isRemoved()) {
            return cached;
        }
        ENTITIES.remove(entityId);
        for (Entity entity : level.entitiesForRendering()) {
            if (entity.getUUID().equals(entityId)) {
                ENTITIES.put(entityId, entity);
                return entity;
            }
        }
        return null;
        *///?}
    }

    private static float partialTick(ClientLevel level, Entity entity) {
        Minecraft minecraft = Minecraft.getInstance();
        //? if >=1.21.2 {
        return minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(!level.tickRateManager().isEntityFrozen(entity));
        //?} else if >=1.21 {
        /*return minecraft.getTimer().getGameTimeDeltaPartialTick(!level.tickRateManager().isEntityFrozen(entity));
        *///?} else if >=1.20.3 {
        /*boolean frozenPastTick = level.tickRateManager().isEntityFrozen(entity) && !level.tickRateManager().runsNormally();
        return frozenPastTick ? 1.0F : minecraft.getFrameTime();
        *///?} else {
        /*return minecraft.getFrameTime();
        *///?}
    }

    /** Emits a centered quad in the entity vertex format, or the text format when {@code entityFormat} is false. */
    private static void quad(PoseStack.Pose pose, VertexConsumer vertices, float halfWidth, float halfHeight, int color, boolean entityFormat) {
        vertex(pose, vertices, -halfWidth, -halfHeight, 0.0F, 1.0F, color, entityFormat);
        vertex(pose, vertices, halfWidth, -halfHeight, 1.0F, 1.0F, color, entityFormat);
        vertex(pose, vertices, halfWidth, halfHeight, 1.0F, 0.0F, color, entityFormat);
        vertex(pose, vertices, -halfWidth, halfHeight, 0.0F, 0.0F, color, entityFormat);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer vertices, float x, float y, float u, float v, int color, boolean entityFormat) {
        //? if >=1.21 {
        if (entityFormat) {
            vertices.addVertex(pose, x, y, 0.0F).setColor(color).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(pose, 0.0F, 0.0F, 1.0F);
        } else {
            vertices.addVertex(pose, x, y, 0.0F).setColor(color).setUv(u, v).setLight(FULL_BRIGHT);
        }
        //?} else {
        /*vertices.vertex(pose.pose(), x, y, 0.0F).color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24).uv(u, v);
        if (entityFormat) {
            vertices.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(FULL_BRIGHT);
            //? if >=1.20.5
            vertices.normal(pose, 0.0F, 0.0F, 1.0F);
            //? if <1.20.5
            /^vertices.normal(pose.normal(), 0.0F, 0.0F, 1.0F);^/
        } else {
            vertices.uv2(FULL_BRIGHT);
        }
        vertices.endVertex();
        *///?}
    }
}
