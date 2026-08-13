package com.iamkaf.amber.client.billboard;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 1.21.11 submission adapter for Amber billboard item depth and opacity. */
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
        RenderType renderType = throughWalls ? RenderTypes.textSeeThrough(atlas) : RenderTypes.text(atlas);
        delegate.submitCustomGeometry(poseStack, renderType, (pose, vertices) -> {
            for (BakedQuad quad : quads) {
                int tintIndex = quad.tintIndex();
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
    @Override public void submitNameTag(PoseStack poseStack, @Nullable Vec3 attachment, int offset, Component name, boolean seeThrough, int light, double distance, CameraRenderState camera) { delegate.submitNameTag(poseStack, attachment, offset, name, seeThrough, light, distance, camera); }
    @Override public void submitText(PoseStack poseStack, float x, float y, FormattedCharSequence text, boolean shadow, Font.DisplayMode mode, int light, int color, int backgroundColor, int outlineColor) { delegate.submitText(poseStack, x, y, text, shadow, mode, light, color, backgroundColor, outlineColor); }
    @Override public void submitFlame(PoseStack poseStack, EntityRenderState state, Quaternionf rotation) { delegate.submitFlame(poseStack, state, rotation); }
    @Override public void submitLeash(PoseStack poseStack, EntityRenderState.LeashState state) { delegate.submitLeash(poseStack, state); }
    @Override public <S> void submitModel(Model<? super S> model, S state, PoseStack poseStack, RenderType renderType, int light, int overlay, int tintedColor, @Nullable TextureAtlasSprite sprite, int outlineColor, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) { delegate.submitModel(model, state, poseStack, renderType, light, overlay, tintedColor, sprite, outlineColor, crumblingOverlay); }
    @Override public void submitModelPart(ModelPart modelPart, PoseStack poseStack, RenderType renderType, int light, int overlay, @Nullable TextureAtlasSprite sprite, boolean flag, boolean flag1, int tintedColor, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay, int outlineColor) { delegate.submitModelPart(modelPart, poseStack, renderType, light, overlay, sprite, flag, flag1, tintedColor, crumblingOverlay, outlineColor); }
    @Override public void submitBlock(PoseStack poseStack, BlockState state, int light, int overlay, int outlineColor) { delegate.submitBlock(poseStack, state, light, overlay, outlineColor); }
    @Override public void submitMovingBlock(PoseStack poseStack, MovingBlockRenderState state) { delegate.submitMovingBlock(poseStack, state); }
    @Override public void submitBlockModel(PoseStack poseStack, RenderType renderType, BlockStateModel model, float red, float green, float blue, int light, int overlay, int outlineColor) { delegate.submitBlockModel(poseStack, renderType, model, red, green, blue, light, overlay, outlineColor); }
    @Override public void submitCustomGeometry(PoseStack poseStack, RenderType renderType, CustomGeometryRenderer renderer) { delegate.submitCustomGeometry(poseStack, renderType, renderer); }
    @Override public void submitParticleGroup(ParticleGroupRenderer renderer) { delegate.submitParticleGroup(renderer); }
}
