//? if >=1.21.11 || >=26.1 {
package com.iamkaf.amber.api.billboard.v1;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Objects;

/**
 * The visual content of an in-world {@link Billboard}.
 */
public sealed interface BillboardContent permits BillboardContent.Texture, BillboardContent.Item, BillboardContent.Text, BillboardContent.ItemObject, BillboardContent.BlockObject {
    /**
     * A textured quad backed by a resource-pack PNG.
     *
     * @param texture the texture resource, such as {@code my_mod:textures/billboards/notice.png}
     * @param width   width in world blocks
     * @param height  height in world blocks
     */
    record Texture(Identifier texture, float width, float height) implements BillboardContent {
        public Texture {
            Objects.requireNonNull(texture, "texture");
            requirePositiveFinite(width, "width");
            requirePositiveFinite(height, "height");
        }
    }

    /**
     * A default item model rendered facing the camera.
     *
     * @param item  registered item identifier
     * @param scale model scale in world blocks
     */
    record Item(Identifier item, float scale) implements BillboardContent {
        public Item {
            Objects.requireNonNull(item, "item");
            requirePositiveFinite(scale, "scale");
        }
    }

    /** A world-oriented item model, rendered with the ground transform used by dropped items. */
    record ItemObject(Identifier item, float scale) implements BillboardContent {
        public ItemObject {
            Objects.requireNonNull(item, "item");
            requirePositiveFinite(scale, "scale");
        }
    }

    /** A world-oriented block item model. */
    record BlockObject(Identifier block, float scale) implements BillboardContent {
        public BlockObject {
            Objects.requireNonNull(block, "block");
            requirePositiveFinite(scale, "scale");
        }
    }

    /**
     * Camera-facing text.
     *
     * @param text  text and style to render
     * @param scale world blocks per font pixel
     * @param color default ARGB color; component-styled glyphs retain their RGB and inherit its alpha
     */
    record Text(Component text, float scale, int color) implements BillboardContent {
        public Text(Component text, float scale) {
            this(text, scale, -1);
        }

        public Text {
            Objects.requireNonNull(text, "text");
            requirePositiveFinite(scale, "scale");
        }
    }

    private static void requirePositiveFinite(float value, String name) {
        if (!Float.isFinite(value) || value <= 0.0F) {
            throw new IllegalArgumentException(name + " must be positive and finite");
        }
    }
}
//?}
