package com.iamkaf.amber.client.billboard;

//? if >=26.1 {
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
//? if <26.2 {
import net.minecraft.client.model.geom.ModelPart;
//?}
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
//? if <26.2 {
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
//?}
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
//? if >=26.2 {
import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives;
//?}
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
//? if >=26.2 {
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
//?}
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//? if >=26.3 {
import net.minecraft.client.renderer.texture.UvMapping;
import net.minecraft.client.resources.model.geometry.ItemQuads;
//?}
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
//? if >=26.2 {
import net.minecraft.world.phys.shapes.VoxelShape;
//?}
import org.joml.Quaternionf;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
//?} else if >=1.21.9 {
/*import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
//? if >=1.21.11 {
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Vector3fc;
//?} else {
/^import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.HitboxesRenderState;
^/
//?}

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
*///?} else {
/*import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.jetbrains.annotations.Nullable;
*///?}

//? if >=26.1 {
/** Adapts deferred item submissions to Amber's depth and opacity presentation. */
final class BillboardItemSubmitter implements SubmitNodeCollector {
    private final SubmitNodeCollector delegate;
    private final boolean throughWalls;
    private final float opacity;

    private BillboardItemSubmitter(SubmitNodeCollector delegate, boolean throughWalls, float opacity) {
        this.delegate = delegate;
        this.throughWalls = throughWalls;
        this.opacity = opacity;
    }

    static void submit(ItemStackRenderState state, PoseStack poseStack, SubmitNodeCollector output, int light, int overlay) {
        submit(state, poseStack, output, light, overlay, false, 1.0F);
    }

    static void submit(ItemStackRenderState state, PoseStack poseStack, SubmitNodeCollector output, int light, int overlay, boolean throughWalls, float opacity) {
        state.submit(poseStack, new BillboardItemSubmitter(output, throughWalls, opacity), light, overlay, 0);
    }

    @Override
    public OrderedSubmitNodeCollector order(int order) {
        return delegate.order(order);
    }

    @Override
    public void submitItem(PoseStack poseStack, ItemDisplayContext context, int light, int overlay, int outlineColor, int[] tints,
                           //? if >=26.3
                           ItemQuads quads,
                           //? if <26.3
                           /*List<BakedQuad> quads,*/
                           ItemStackRenderState.FoilType foilType) {
        if (!throughWalls && opacity >= 1.0F) {
            delegate.submitItem(poseStack, context, light, overlay, outlineColor, tints, quads, foilType);
            return;
        }
        //? if >=26.3
        List<BakedQuad> allQuads = quads.all();
        //? if <26.3
        /*List<BakedQuad> allQuads = quads;*/
        if (allQuads.isEmpty()) {
            return;
        }

        Identifier firstAtlas = allQuads.getFirst().materialInfo().sprite().atlasLocation();
        boolean singleAtlas = true;
        for (int index = 1; index < allQuads.size(); index++) {
            if (!firstAtlas.equals(allQuads.get(index).materialInfo().sprite().atlasLocation())) {
                singleAtlas = false;
                break;
            }
        }
        if (singleAtlas) {
            submitQuads(poseStack, light, tints, firstAtlas, allQuads);
            return;
        }

        Map<Identifier, List<BakedQuad>> byAtlas = new LinkedHashMap<>();
        for (BakedQuad quad : allQuads) {
            byAtlas.computeIfAbsent(quad.materialInfo().sprite().atlasLocation(), ignored -> new java.util.ArrayList<>()).add(quad);
        }
        for (Map.Entry<Identifier, List<BakedQuad>> entry : byAtlas.entrySet()) {
            submitQuads(poseStack, light, tints, entry.getKey(), entry.getValue());
        }
    }

    private void submitQuads(PoseStack poseStack, int light, int[] tints, Identifier atlas, List<BakedQuad> quads) {
        //? if >=26.3
        RenderType renderType = throughWalls ? BillboardRenderTypes.seeThrough(atlas) : RenderTypes.text(atlas);
        //? if <26.3
        /*RenderType renderType = throughWalls ? RenderTypes.textSeeThrough(atlas) : RenderTypes.text(atlas);*/
        delegate.submitCustomGeometry(poseStack, renderType, (pose, vertices) -> {
            for (BakedQuad quad : quads) {
                int tintIndex = quad.materialInfo().tintIndex();
                int color = tintIndex >= 0 && tintIndex < tints.length ? tints[tintIndex] : -1;
                color = ARGB.multiplyAlpha(color, opacity);
                for (int vertex = 0; vertex < BakedQuad.VERTEX_COUNT; vertex++) {
                    Vector3fc position = quad.position(vertex);
                    long uv = quad.packedUV(vertex);
                    vertices.addVertex(pose, position.x(), position.y(), position.z())
                            .setColor(color)
                            .setUv(UVPair.unpackU(uv), UVPair.unpackV(uv))
                            .setLight(light);
                }
            }
        });
    }

    @Override public void submitShadow(PoseStack poseStack, float radius, List<EntityRenderState.ShadowPiece> pieces) { delegate.submitShadow(poseStack, radius, pieces); }
    //? if <26.2 {
    @Override public void submitNameTag(PoseStack poseStack, @Nullable Vec3 attachment, int offset, Component name, boolean seeThrough, int light, double distance, CameraRenderState camera) { delegate.submitNameTag(poseStack, attachment, offset, name, seeThrough, light, distance, camera); }
    //?} else {
    @Override public void submitNameTag(PoseStack poseStack, @Nullable Vec3 attachment, int offset, Component name, boolean seeThrough, int light, CameraRenderState camera) { delegate.submitNameTag(poseStack, attachment, offset, name, seeThrough, light, camera); }
    //?}
    @Override public void submitText(PoseStack poseStack, float x, float y, FormattedCharSequence text, boolean shadow, Font.DisplayMode mode, int light, int color, int backgroundColor, int outlineColor) { delegate.submitText(poseStack, x, y, text, shadow, mode, light, color, backgroundColor, outlineColor); }
    //? if >=26.3
    @Override public void submitTextBackground(PoseStack poseStack, float x0, float y0, float x1, float y1, int color, Font.DisplayMode mode, int light) { delegate.submitTextBackground(poseStack, x0, y0, x1, y1, color, mode, light); }
    @Override public void submitFlame(PoseStack poseStack, EntityRenderState state, Quaternionf rotation) { delegate.submitFlame(poseStack, state, rotation); }
    @Override public void submitLeash(PoseStack poseStack, EntityRenderState.LeashState state) { delegate.submitLeash(poseStack, state); }
    //? if >=26.3 {
    @Override public <S> void submitModel(Model<? super S> model, S state, PoseStack poseStack, RenderType renderType, int light, int overlay, int tintedColor, @Nullable UvMapping uvMapping, int outlineColor) { delegate.submitModel(model, state, poseStack, renderType, light, overlay, tintedColor, uvMapping, outlineColor); }
    @Override public <S> void submitCrumblingOverlay(Model<? super S> model, S state, PoseStack poseStack, RenderType renderType, int light, int overlay, int tintedColor, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) { delegate.submitCrumblingOverlay(model, state, poseStack, renderType, light, overlay, tintedColor, crumblingOverlay); }
    //?} else {
    /*@Override public <S> void submitModel(Model<? super S> model, S state, PoseStack poseStack, RenderType renderType, int light, int overlay, int tintedColor, @Nullable TextureAtlasSprite sprite, int outlineColor, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) { delegate.submitModel(model, state, poseStack, renderType, light, overlay, tintedColor, sprite, outlineColor, crumblingOverlay); }*/
    //?}
    //? if <26.2 {
    @Override public void submitModelPart(ModelPart modelPart, PoseStack poseStack, RenderType renderType, int light, int overlay, @Nullable TextureAtlasSprite sprite, boolean sheeted, boolean hasFoil, int tintedColor, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay, int outlineColor) { delegate.submitModelPart(modelPart, poseStack, renderType, light, overlay, sprite, sheeted, hasFoil, tintedColor, crumblingOverlay, outlineColor); }
    @Override public void submitMovingBlock(PoseStack poseStack, MovingBlockRenderState state) { delegate.submitMovingBlock(poseStack, state); }
    //?} else {
    @Override public void submitMovingBlock(PoseStack poseStack, MovingBlockRenderState state, int outlineColor) { delegate.submitMovingBlock(poseStack, state, outlineColor); }
    //?}
    @Override public void submitBlockModel(PoseStack poseStack, RenderType renderType, List<BlockStateModelPart> parts, int[] tintLayers, int light, int overlay, int outlineColor) { delegate.submitBlockModel(poseStack, renderType, parts, tintLayers, light, overlay, outlineColor); }
    //? if <26.2 {
    @Override public void submitBreakingBlockModel(PoseStack poseStack, BlockStateModel model, long seed, int progress) { delegate.submitBreakingBlockModel(poseStack, model, seed, progress); }
    //?} else if <26.3 {
    @Override public void submitBreakingBlockModel(PoseStack poseStack, List<BlockStateModelPart> parts, int progress) { delegate.submitBreakingBlockModel(poseStack, parts, progress); }
    //?} else {
    @Override public void submitBreakingBlockModel(PoseStack poseStack, List<BlockStateModelPart> parts, int progress, boolean isBlockTranslucent) { delegate.submitBreakingBlockModel(poseStack, parts, progress, isBlockTranslucent); }
    //?}
    //? if >=26.2 {
    @Override public void submitShapeOutline(PoseStack poseStack, VoxelShape shape, RenderType renderType, int color, float width, boolean afterTerrain) { delegate.submitShapeOutline(poseStack, shape, renderType, color, width, afterTerrain); }
    //?}
    @Override public void submitCustomGeometry(PoseStack poseStack, RenderType renderType, CustomGeometryRenderer renderer) { delegate.submitCustomGeometry(poseStack, renderType, renderer); }
    //? if <26.2 {
    @Override public void submitParticleGroup(ParticleGroupRenderer renderer) { delegate.submitParticleGroup(renderer); }
    //?} else {
    @Override public void submitQuadParticleGroup(QuadParticleRenderState particles) { delegate.submitQuadParticleGroup(particles); }
    @Override public void submitGizmoPrimitives(DrawableGizmoPrimitives.Group group, CameraRenderState camera, boolean onTop) { delegate.submitGizmoPrimitives(group, camera, onTop); }
    //?}
}
//?} else if >=1.21.9 {
/*/^* Adapts deferred item submissions to Amber's depth and opacity presentation. ^/
final class BillboardItemSubmitter implements SubmitNodeCollector {
    private final SubmitNodeCollector delegate;
    private final boolean throughWalls;
    private final float opacity;

    private BillboardItemSubmitter(SubmitNodeCollector delegate, boolean throughWalls, float opacity) {
        this.delegate = delegate;
        this.throughWalls = throughWalls;
        this.opacity = opacity;
    }

    static void submit(ItemStackRenderState state, PoseStack poseStack, SubmitNodeCollector output, int light, int overlay, boolean throughWalls, float opacity) {
        state.submit(poseStack, new BillboardItemSubmitter(output, throughWalls, opacity), light, overlay, 0);
    }

    @Override
    public OrderedSubmitNodeCollector order(int order) {
        return delegate.order(order);
    }

    @Override
    public void submitItem(PoseStack poseStack, ItemDisplayContext context, int light, int overlay, int outlineColor, int[] tints, List<BakedQuad> quads, RenderType originalRenderType, ItemStackRenderState.FoilType foilType) {
        if (!throughWalls && opacity >= 1.0F) {
            delegate.submitItem(poseStack, context, light, overlay, outlineColor, tints, quads, originalRenderType, foilType);
            return;
        }
        if (quads.isEmpty()) {
            return;
        }

        Identifier firstAtlas = quads.getFirst().sprite().atlasLocation();
        boolean singleAtlas = true;
        for (int index = 1; index < quads.size(); index++) {
            if (!firstAtlas.equals(quads.get(index).sprite().atlasLocation())) {
                singleAtlas = false;
                break;
            }
        }
        if (singleAtlas) {
            submitQuads(poseStack, light, tints, firstAtlas, quads);
            return;
        }

        Map<Identifier, List<BakedQuad>> byAtlas = new LinkedHashMap<>();
        for (BakedQuad quad : quads) {
            byAtlas.computeIfAbsent(quad.sprite().atlasLocation(), ignored -> new java.util.ArrayList<>()).add(quad);
        }
        for (Map.Entry<Identifier, List<BakedQuad>> entry : byAtlas.entrySet()) {
            submitQuads(poseStack, light, tints, entry.getKey(), entry.getValue());
        }
    }

    private void submitQuads(PoseStack poseStack, int light, int[] tints, Identifier atlas, List<BakedQuad> quads) {
        RenderType renderType = throughWalls ? BillboardRenderTypes.seeThrough(atlas) : BillboardRenderTypes.text(atlas);
        delegate.submitCustomGeometry(poseStack, renderType, (pose, vertices) -> {
            for (BakedQuad quad : quads) {
                int tintIndex = quad.tintIndex();
                int color = tintIndex >= 0 && tintIndex < tints.length ? tints[tintIndex] : -1;
                color = ClientBillboards.multiplyAlpha(color, opacity);
                //? if >=1.21.11 {
                for (int vertex = 0; vertex < BakedQuad.VERTEX_COUNT; vertex++) {
                    Vector3fc position = quad.position(vertex);
                    long uv = quad.packedUV(vertex);
                    vertices.addVertex(pose, position.x(), position.y(), position.z())
                            .setColor(color)
                            .setUv(UVPair.unpackU(uv), UVPair.unpackV(uv))
                            .setLight(light);
                }
                //?} else {
                /^// Quads pack four vertices in the block vertex format: eight ints each, UV at ints four and five.
                int[] data = quad.vertices();
                for (int vertex = 0; vertex < 4; vertex++) {
                    int offset = vertex * 8;
                    vertices.addVertex(pose, Float.intBitsToFloat(data[offset]), Float.intBitsToFloat(data[offset + 1]), Float.intBitsToFloat(data[offset + 2]))
                            .setColor(color)
                            .setUv(Float.intBitsToFloat(data[offset + 4]), Float.intBitsToFloat(data[offset + 5]))
                            .setLight(light);
                }
                ^/
                //?}
            }
        });
    }

    //? if <1.21.11
    /^@Override public void submitHitbox(PoseStack poseStack, EntityRenderState state, HitboxesRenderState hitboxes) { delegate.submitHitbox(poseStack, state, hitboxes); }^/
    @Override public void submitShadow(PoseStack poseStack, float radius, List<EntityRenderState.ShadowPiece> pieces) { delegate.submitShadow(poseStack, radius, pieces); }
    @Override public void submitNameTag(PoseStack poseStack, @Nullable Vec3 attachment, int offset, Component name, boolean seeThrough, int light, double distance, CameraRenderState camera) { delegate.submitNameTag(poseStack, attachment, offset, name, seeThrough, light, distance, camera); }
    @Override public void submitText(PoseStack poseStack, float x, float y, FormattedCharSequence text, boolean shadow, Font.DisplayMode mode, int light, int color, int backgroundColor, int outlineColor) { delegate.submitText(poseStack, x, y, text, shadow, mode, light, color, backgroundColor, outlineColor); }
    @Override public void submitFlame(PoseStack poseStack, EntityRenderState state, Quaternionf rotation) { delegate.submitFlame(poseStack, state, rotation); }
    @Override public void submitLeash(PoseStack poseStack, EntityRenderState.LeashState state) { delegate.submitLeash(poseStack, state); }
    @Override public <S> void submitModel(Model<? super S> model, S state, PoseStack poseStack, RenderType renderType, int light, int overlay, int tintedColor, @Nullable TextureAtlasSprite sprite, int outlineColor, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) { delegate.submitModel(model, state, poseStack, renderType, light, overlay, tintedColor, sprite, outlineColor, crumblingOverlay); }
    @Override public void submitModelPart(ModelPart modelPart, PoseStack poseStack, RenderType renderType, int light, int overlay, @Nullable TextureAtlasSprite sprite, boolean sheeted, boolean hasFoil, int tintedColor, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay, int outlineColor) { delegate.submitModelPart(modelPart, poseStack, renderType, light, overlay, sprite, sheeted, hasFoil, tintedColor, crumblingOverlay, outlineColor); }
    @Override public void submitBlock(PoseStack poseStack, BlockState state, int light, int overlay, int outlineColor) { delegate.submitBlock(poseStack, state, light, overlay, outlineColor); }
    @Override public void submitMovingBlock(PoseStack poseStack, MovingBlockRenderState state) { delegate.submitMovingBlock(poseStack, state); }
    @Override public void submitBlockModel(PoseStack poseStack, RenderType renderType, BlockStateModel model, float red, float green, float blue, int light, int overlay, int outlineColor) { delegate.submitBlockModel(poseStack, renderType, model, red, green, blue, light, overlay, outlineColor); }
    @Override public void submitCustomGeometry(PoseStack poseStack, RenderType renderType, CustomGeometryRenderer renderer) { delegate.submitCustomGeometry(poseStack, renderType, renderer); }
    @Override public void submitParticleGroup(ParticleGroupRenderer renderer) { delegate.submitParticleGroup(renderer); }
}
*///?} else {
/*/^*
 * Adapts buffered item rendering to Amber's depth and opacity presentation.
 *
 * <p>Item model quads are redrawn unshaded in the text vertex format with scaled alpha, and foil is
 * dropped. Special item renderers keep their own render types.</p>
 ^/
final class BillboardItemSubmitter implements MultiBufferSource {
    private final MultiBufferSource delegate;
    private final boolean throughWalls;
    private final float opacity;

    private BillboardItemSubmitter(MultiBufferSource delegate, boolean throughWalls, float opacity) {
        this.delegate = delegate;
        this.throughWalls = throughWalls;
        this.opacity = opacity;
    }

    static MultiBufferSource buffers(MultiBufferSource output, boolean throughWalls, float opacity) {
        if (!throughWalls && opacity >= 1.0F) {
            return output;
        }
        return new BillboardItemSubmitter(output, throughWalls, opacity);
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        if (isFoil(renderType)) {
            return new ItemVertices(null, opacity);
        }
        if (!isItemSheet(renderType)) {
            return delegate.getBuffer(renderType);
        }
        RenderType remapped = throughWalls
                ? BillboardRenderTypes.seeThrough(TextureAtlas.LOCATION_BLOCKS)
                : BillboardRenderTypes.text(TextureAtlas.LOCATION_BLOCKS);
        return new ItemVertices(delegate.getBuffer(remapped), opacity);
    }

    private static boolean isItemSheet(RenderType renderType) {
        //? if <1.21.2 {
        /^if (renderType == Sheets.translucentCullBlockSheet()) {
            return true;
        }
        ^/
        //?}
        return renderType == Sheets.solidBlockSheet()
                || renderType == Sheets.cutoutBlockSheet()
                || renderType == Sheets.translucentItemSheet();
    }

    private static boolean isFoil(RenderType renderType) {
        //? if <1.21 {
        /^if (renderType == RenderType.glintDirect() || renderType == RenderType.armorGlint()) {
            return true;
        }
        ^/
        //?}
        //? if <1.21.2 {
        /^if (renderType == RenderType.entityGlintDirect()) {
            return true;
        }
        ^/
        //?}
        return renderType == RenderType.glint()
                || renderType == RenderType.glintTranslucent()
                || renderType == RenderType.entityGlint()
                || renderType == RenderType.armorEntityGlint();
    }

    /^* Forwards position, color, texture and light with scaled alpha, or discards everything without a target. ^/
    private static final class ItemVertices implements VertexConsumer {
        private final @Nullable VertexConsumer target;
        private final float opacity;

        private ItemVertices(@Nullable VertexConsumer target, float opacity) {
            this.target = target;
            this.opacity = opacity;
        }

        private int fade(int alpha) {
            return ClientBillboards.multiplyAlpha(alpha << 24, opacity) >>> 24;
        }

        //? if >=1.21 {
        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            if (target != null) {
                target.addVertex(x, y, z);
            }
            return this;
        }

        @Override
        public VertexConsumer setColor(int red, int green, int blue, int alpha) {
            if (target != null) {
                target.setColor(red, green, blue, fade(alpha));
            }
            return this;
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            if (target != null) {
                target.setUv(u, v);
            }
            return this;
        }

        @Override
        public VertexConsumer setUv1(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer setUv2(int u, int v) {
            if (target != null) {
                target.setUv2(u, v);
            }
            return this;
        }

        @Override
        public VertexConsumer setNormal(float x, float y, float z) {
            return this;
        }
        //?} else {
        /^@Override
        public VertexConsumer vertex(double x, double y, double z) {
            if (target != null) {
                target.vertex(x, y, z);
            }
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            if (target != null) {
                target.color(red, green, blue, fade(alpha));
            }
            return this;
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            if (target != null) {
                target.uv(u, v);
            }
            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer uv2(int u, int v) {
            if (target != null) {
                target.uv2(u, v);
            }
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            return this;
        }

        @Override
        public void endVertex() {
            if (target != null) {
                target.endVertex();
            }
        }

        @Override
        public void defaultColor(int red, int green, int blue, int alpha) {
            if (target != null) {
                target.defaultColor(red, green, blue, fade(alpha));
            }
        }

        @Override
        public void unsetDefaultColor() {
            if (target != null) {
                target.unsetDefaultColor();
            }
        }
        ^/
        //?}
    }
}
*///?}
