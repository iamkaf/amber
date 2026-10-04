package com.iamkaf.amber.client.billboard;

//? if >=26.3 {
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.oit.OitPipelineSet;
import net.minecraft.client.renderer.rendertype.RenderSetup;
//?}
//? if >=1.21.11 {
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
//?} else {
/*import net.minecraft.client.renderer.RenderType;
*///?}
import net.minecraft.resources.Identifier;

//? if >=26.3 {
import java.util.HashMap;
import java.util.Map;
//?}

/** Billboard render types for this Minecraft version. */
final class BillboardRenderTypes {
    //? if >=26.3 {
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
    //?}

    private BillboardRenderTypes() {
    }

    /** Depth-tested translucent quads in the entity vertex format. */
    static RenderType translucent(Identifier texture) {
        //? if >=1.21.11
        return RenderTypes.entityTranslucent(texture, false);
        //? if <1.21.11
        /*return RenderType.entityTranslucent(texture, false);*/
    }

    /** Depth-tested, unshaded quads with position, color, texture and light. */
    static RenderType text(Identifier texture) {
        //? if >=1.21.11
        return RenderTypes.text(texture);
        //? if <1.21.11
        /*return RenderType.text(texture);*/
    }

    /** Quads in the {@link #text} vertex format drawn over world geometry. */
    static RenderType seeThrough(Identifier texture) {
        //? if >=26.3 {
        return SEE_THROUGH.computeIfAbsent(texture, id -> RenderType.create(
                "amber_billboard_see_through",
                RenderSetup.builder(RenderPipelines.TEXT_SEE_THROUGH)
                        .setOitPipelines(SEE_THROUGH_OIT)
                        .withTexture("Sampler0", id)
                        .createRenderSetup()
        ));
        //?} else if >=1.21.11 {
        /*return RenderTypes.textSeeThrough(texture);
        *///?} else {
        /*return RenderType.textSeeThrough(texture);
        *///?}
    }
}
