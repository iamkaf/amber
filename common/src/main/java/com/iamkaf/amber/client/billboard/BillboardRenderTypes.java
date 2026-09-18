//? if >=26.3 {
package com.iamkaf.amber.client.billboard;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.oit.OitPipelineSet;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

final class BillboardRenderTypes {
    // Vanilla see-through text is drawn in a separate pass. Custom geometry needs OIT variants.
    private static final OitPipelineSet SEE_THROUGH_OIT = OitPipelineSet.builder(
            "amber/billboard_see_through",
            RenderPipeline.builder()
                    .withBindGroupLayout(BindGroupLayouts.GLOBALS)
                    .withBindGroupLayout(BindGroupLayouts.PROJECTION)
                    .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
                    .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
                    .withVertexShader("core/text")
                    .withFragmentShader("core/text")
                    .withShaderDefine("IS_SEE_THROUGH")
                    .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR)
                    .withPrimitiveTopology(PrimitiveTopology.QUADS)
    ).withoutDepthTest().build();
    private static final Map<Identifier, RenderType> SEE_THROUGH = new HashMap<>();

    private BillboardRenderTypes() {
    }

    static RenderType seeThrough(Identifier texture) {
        return SEE_THROUGH.computeIfAbsent(texture, id -> RenderType.create(
                "amber_billboard_see_through",
                RenderSetup.builder(RenderPipelines.TEXT_SEE_THROUGH)
                        .setOitPipelines(SEE_THROUGH_OIT)
                        .withTexture("Sampler0", id)
                        .createRenderSetup()
        ));
    }
}
//?}
