//? if >=1.21.11 || >=26.1 {
package com.iamkaf.amber.api.billboard.v1;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.UUID;

/**
 * An immutable visual placed in the world. Texture, item, and text content face the camera;
 * item-object and block-object content retain world orientation.
 *
 * <p>Billboards last for five seconds by default. Lifetimes use a smooth client presentation
 * clock at 20 ticks per second and are unaffected by server time synchronization. Use
 * {@link #forTicks(int)} to choose another
 * bounded lifetime or {@link #persistent()} together with {@link Billboards#hide} for an explicitly
 * managed billboard.</p>
 *
 * @param id            stable identity used to replace or hide this billboard
 * @param anchor        world or entity reference used as the billboard's center
 * @param content       camera-facing or world-oriented visual content
 * @param rotation      base local-axis rotation in degrees
 * @param scale         base local-axis transform scale
 * @param opacity       base opacity multiplier between 0 and 1
 * @param depthMode     whether world geometry can occlude the billboard
 * @param durationTicks positive lifetime in ticks, or {@link #PERSISTENT}
 * @param animation     animation sampled over the bounded lifetime
 */
public record Billboard(UUID id, BillboardAnchor anchor, BillboardContent content, Vec3 rotation, Vec3 scale, float opacity, BillboardDepthMode depthMode, int durationTicks, BillboardAnimation animation) {
    public static final int DEFAULT_DURATION_TICKS = 100;
    public static final int PERSISTENT = -1;

    public Billboard {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(anchor, "anchor");
        Objects.requireNonNull(content, "content");
        requireFinite(rotation, "rotation");
        requireScale(scale);
        requireOpacity(opacity, "opacity");
        Objects.requireNonNull(depthMode, "depthMode");
        Objects.requireNonNull(animation, "animation");
        if (durationTicks != PERSISTENT && durationTicks <= 0) {
            throw new IllegalArgumentException("durationTicks must be positive or Billboard.PERSISTENT");
        }
        if (durationTicks == PERSISTENT && !animation.isNone()) {
            throw new IllegalArgumentException("animated billboards must have a bounded lifetime");
        }
    }

    /** Source-compatible constructor for the original billboard value shape. */
    public Billboard(UUID id, BillboardAnchor anchor, BillboardContent content, Vec3 scale, int durationTicks, BillboardAnimation animation) {
        this(id, anchor, content, Vec3.ZERO, scale, 1.0F, BillboardDepthMode.DEPTH_TESTED, durationTicks, animation);
    }

    public static Billboard texture(Vec3 position, Identifier texture, float width, float height) {
        return new Billboard(UUID.randomUUID(), BillboardAnchor.world(position), new BillboardContent.Texture(texture, width, height), Vec3.ZERO, unitScale(), 1.0F, BillboardDepthMode.DEPTH_TESTED, DEFAULT_DURATION_TICKS, BillboardAnimation.NONE);
    }

    public static Billboard item(Vec3 position, Item item, float scale) {
        Objects.requireNonNull(item, "item");
        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        if (itemId == null) {
            throw new IllegalArgumentException("item must be registered");
        }
        return new Billboard(UUID.randomUUID(), BillboardAnchor.world(position), new BillboardContent.Item(itemId, scale), Vec3.ZERO, unitScale(), 1.0F, BillboardDepthMode.DEPTH_TESTED, DEFAULT_DURATION_TICKS, BillboardAnimation.NONE);
    }

    /** Creates a world-oriented item object using the dropped-item model transform. */
    public static Billboard itemObject(Vec3 position, Item item, float scale) {
        Objects.requireNonNull(item, "item");
        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        if (itemId == null) {
            throw new IllegalArgumentException("item must be registered");
        }
        return new Billboard(UUID.randomUUID(), BillboardAnchor.world(position), new BillboardContent.ItemObject(itemId, scale), Vec3.ZERO, unitScale(), 1.0F, BillboardDepthMode.DEPTH_TESTED, DEFAULT_DURATION_TICKS, BillboardAnimation.NONE);
    }

    /** Creates a world-oriented block object from its registered block item model. */
    public static Billboard blockObject(Vec3 position, Block block, float scale) {
        Objects.requireNonNull(block, "block");
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
        if (blockId == null) {
            throw new IllegalArgumentException("block must be registered");
        }
        if (block.asItem() == net.minecraft.world.item.Items.AIR) {
            throw new IllegalArgumentException("block must have an item model");
        }
        return new Billboard(UUID.randomUUID(), BillboardAnchor.world(position), new BillboardContent.BlockObject(blockId, scale), Vec3.ZERO, unitScale(), 1.0F, BillboardDepthMode.DEPTH_TESTED, DEFAULT_DURATION_TICKS, BillboardAnimation.NONE);
    }

    public static Billboard text(Vec3 position, Component text, float scale) {
        return text(position, text, scale, -1);
    }

    public static Billboard text(Vec3 position, Component text, float scale, int color) {
        return new Billboard(UUID.randomUUID(), BillboardAnchor.world(position), new BillboardContent.Text(text, scale, color), Vec3.ZERO, unitScale(), 1.0F, BillboardDepthMode.DEPTH_TESTED, DEFAULT_DURATION_TICKS, BillboardAnimation.NONE);
    }

    /** Returns a copy that replaces any visible billboard with the same identity. */
    public Billboard identifiedBy(UUID newId) {
        return new Billboard(newId, anchor, content, rotation, scale, opacity, depthMode, durationTicks, animation);
    }

    /** Returns a copy at a new world-space center. */
    public Billboard at(Vec3 newPosition) {
        return anchoredTo(BillboardAnchor.world(newPosition));
    }

    /** Returns a copy bound to an entity with a world-axis offset from its interpolated position. */
    public Billboard boundTo(net.minecraft.world.entity.Entity entity, Vec3 offset) {
        return anchoredTo(BillboardAnchor.entity(entity, offset));
    }

    /** Returns a copy with the supplied initial positioning anchor. */
    public Billboard anchoredTo(BillboardAnchor newAnchor) {
        return new Billboard(id, newAnchor, content, rotation, scale, opacity, depthMode, durationTicks, animation);
    }

    /** Returns a copy with a base local-axis rotation in degrees. */
    public Billboard withRotation(Vec3 degrees) {
        return new Billboard(id, anchor, content, degrees, scale, opacity, depthMode, durationTicks, animation);
    }

    /** Returns a copy with a base local-axis rotation in degrees. */
    public Billboard withRotation(double x, double y, double z) {
        return withRotation(new Vec3(x, y, z));
    }

    /** Returns a copy with a uniform base transform scale. */
    public Billboard withScale(double uniformScale) {
        return withScale(new Vec3(uniformScale, uniformScale, uniformScale));
    }

    /** Returns a copy with a local-axis base transform scale. */
    public Billboard withScale(double x, double y, double z) {
        return withScale(new Vec3(x, y, z));
    }

    /** Returns a copy with a local-axis base transform scale. */
    public Billboard withScale(Vec3 newScale) {
        return new Billboard(id, anchor, content, rotation, newScale, opacity, depthMode, durationTicks, animation);
    }

    /** Returns a copy with a base opacity multiplier between 0 and 1. */
    public Billboard withOpacity(float newOpacity) {
        return new Billboard(id, anchor, content, rotation, scale, newOpacity, depthMode, durationTicks, animation);
    }

    /** Returns a copy using the requested world-occlusion behavior. */
    public Billboard withDepthMode(BillboardDepthMode newDepthMode) {
        return new Billboard(id, anchor, content, rotation, scale, opacity, newDepthMode, durationTicks, animation);
    }

    /** Returns a copy that remains visible through world geometry. */
    public Billboard visibleThroughWalls() {
        return withDepthMode(BillboardDepthMode.THROUGH_WALLS);
    }

    /** Returns a copy with a bounded presentation lifetime at 20 ticks per second. */
    public Billboard forTicks(int ticks) {
        return new Billboard(id, anchor, content, rotation, scale, opacity, depthMode, ticks, animation);
    }

    /** Returns a copy translated by the supplied world-space offset over its lifetime. */
    public Billboard translateBy(Vec3 offset) {
        return animate(animation.withTranslation(offset, BillboardAnimation.Easing.LINEAR));
    }

    /** Returns a copy translated by the supplied world-space offset over its lifetime. */
    public Billboard translateBy(double x, double y, double z) {
        return translateBy(new Vec3(x, y, z));
    }

    /** Returns a copy translated with the supplied timing curve over its lifetime. */
    public Billboard translateBy(double x, double y, double z, BillboardAnimation.Easing easing) {
        return translateBy(new Vec3(x, y, z), easing);
    }

    /** Returns a copy translated with the supplied timing curve over its lifetime. */
    public Billboard translateBy(Vec3 offset, BillboardAnimation.Easing easing) {
        return translateFromTo(Vec3.ZERO, offset, easing);
    }

    /** Returns a copy translated between two world-space lifetime offsets. */
    public Billboard translateFromTo(Vec3 from, Vec3 to) {
        return translateFromTo(from, to, BillboardAnimation.Easing.LINEAR);
    }

    /** Returns a copy translated between two world-space lifetime offsets. */
    public Billboard translateFromTo(Vec3 from, Vec3 to, BillboardAnimation.Easing easing) {
        return animate(animation.withTranslation(from, to, easing));
    }

    /** Returns a copy translated from zero to the supplied world-space lifetime offset. */
    public Billboard translateTo(Vec3 target) {
        return translateTo(target, BillboardAnimation.Easing.LINEAR);
    }

    /** Returns a copy translated from zero to the supplied world-space lifetime offset. */
    public Billboard translateTo(Vec3 target, BillboardAnimation.Easing easing) {
        return translateFromTo(Vec3.ZERO, target, easing);
    }

    /** Returns a copy translated from zero to the supplied world-space lifetime offset. */
    public Billboard translateTo(double x, double y, double z) {
        return translateTo(new Vec3(x, y, z));
    }

    /** Returns a copy translated from zero with the supplied timing curve. */
    public Billboard translateTo(double x, double y, double z, BillboardAnimation.Easing easing) {
        return translateTo(new Vec3(x, y, z), easing);
    }

    /** Returns a copy rotating between two local-axis degree vectors over its lifetime. */
    public Billboard rotateFromTo(Vec3 from, Vec3 to) {
        return rotateFromTo(from, to, BillboardAnimation.Easing.LINEAR);
    }

    /** Returns a copy rotating between two local-axis degree vectors over its lifetime. */
    public Billboard rotateFromTo(Vec3 from, Vec3 to, BillboardAnimation.Easing easing) {
        return animate(animation.withRotation(from, to, easing));
    }

    /** Returns a copy rotating by the supplied local-axis degrees over its lifetime. */
    public Billboard rotateBy(Vec3 degrees) {
        return rotateTo(degrees);
    }

    /** Returns a copy rotating by the supplied local-axis degrees and timing curve. */
    public Billboard rotateBy(Vec3 degrees, BillboardAnimation.Easing easing) {
        return rotateTo(degrees, easing);
    }

    /** Returns a copy rotating by the supplied local-axis degrees over its lifetime. */
    public Billboard rotateBy(double x, double y, double z) {
        return rotateBy(new Vec3(x, y, z));
    }

    /** Returns a copy rotating by the supplied local-axis degrees and timing curve. */
    public Billboard rotateBy(double x, double y, double z, BillboardAnimation.Easing easing) {
        return rotateBy(new Vec3(x, y, z), easing);
    }

    /** Returns a copy rotating from zero to the supplied local-axis degrees. */
    public Billboard rotateTo(Vec3 target) {
        return rotateTo(target, BillboardAnimation.Easing.LINEAR);
    }

    /** Returns a copy rotating from zero to the supplied local-axis degrees. */
    public Billboard rotateTo(Vec3 target, BillboardAnimation.Easing easing) {
        return rotateFromTo(Vec3.ZERO, target, easing);
    }

    /** Returns a copy rotating from zero to the supplied local-axis degrees. */
    public Billboard rotateTo(double x, double y, double z) {
        return rotateTo(new Vec3(x, y, z));
    }

    /** Returns a copy rotating from zero with the supplied timing curve. */
    public Billboard rotateTo(double x, double y, double z, BillboardAnimation.Easing easing) {
        return rotateTo(new Vec3(x, y, z), easing);
    }

    /** Returns a copy scaling from one uniform lifetime multiplier to another. */
    public Billboard scaleFromTo(double from, double to) {
        return scaleFromTo(from, to, BillboardAnimation.Easing.LINEAR);
    }

    /** Returns a copy scaling from one uniform lifetime multiplier to another. */
    public Billboard scaleFromTo(double from, double to, BillboardAnimation.Easing easing) {
        return scaleFromTo(new Vec3(from, from, from), new Vec3(to, to, to), easing);
    }

    /** Returns a copy with an animatable local-axis squash/stretch track. */
    public Billboard scaleFromTo(Vec3 from, Vec3 to) {
        return scaleFromTo(from, to, BillboardAnimation.Easing.LINEAR);
    }

    /** Returns a copy with an animatable local-axis squash/stretch track. */
    public Billboard scaleFromTo(Vec3 from, Vec3 to, BillboardAnimation.Easing easing) {
        return animate(animation.withScale(from, to, easing));
    }

    /** Returns a copy scaling from the identity multiplier to a uniform target. */
    public Billboard scaleTo(double target) {
        return scaleTo(target, BillboardAnimation.Easing.LINEAR);
    }

    /** Returns a copy scaling from the identity multiplier to a uniform target. */
    public Billboard scaleTo(double target, BillboardAnimation.Easing easing) {
        return scaleFromTo(1.0D, target, easing);
    }

    /** Returns a copy scaling from the identity multiplier to a local-axis target. */
    public Billboard scaleTo(Vec3 target) {
        return scaleTo(target, BillboardAnimation.Easing.LINEAR);
    }

    /** Returns a copy scaling from the identity multiplier to a local-axis target. */
    public Billboard scaleTo(Vec3 target, BillboardAnimation.Easing easing) {
        return scaleFromTo(unitScale(), target, easing);
    }

    /** Returns a copy scaling from the identity multiplier to a local-axis target. */
    public Billboard scaleTo(double x, double y, double z) {
        return scaleTo(new Vec3(x, y, z));
    }

    /** Returns a copy scaling from the identity multiplier with the supplied timing curve. */
    public Billboard scaleTo(double x, double y, double z, BillboardAnimation.Easing easing) {
        return scaleTo(new Vec3(x, y, z), easing);
    }

    /** Returns a copy that animates unstyled text RGB to the supplied color. */
    public Billboard textColorTo(int targetColor) {
        return textColorTo(targetColor, BillboardAnimation.Easing.LINEAR);
    }

    /** Returns a copy that animates unstyled text RGB with the supplied timing curve. */
    public Billboard textColorTo(int targetColor, BillboardAnimation.Easing easing) {
        requireTextContent("textColorTo");
        return animate(animation.withTextColor(targetColor, easing));
    }

    /** Returns a copy that animates text opacity to a multiplier between 0 and 1. */
    public Billboard textOpacityTo(float targetOpacity) {
        return textOpacityTo(targetOpacity, BillboardAnimation.Easing.LINEAR);
    }

    /** Returns a copy that animates text opacity with the supplied timing curve. */
    public Billboard textOpacityTo(float targetOpacity, BillboardAnimation.Easing easing) {
        requireTextContent("textOpacityTo");
        return animate(animation.withTextOpacity(targetOpacity, easing));
    }

    /** Returns a copy with a billboard-wide opacity multiplier animated over its lifetime. */
    public Billboard opacityFromTo(float from, float to) {
        return opacityFromTo(from, to, BillboardAnimation.Easing.LINEAR);
    }

    /** Returns a copy with a billboard-wide opacity multiplier animated over its lifetime. */
    public Billboard opacityFromTo(float from, float to, BillboardAnimation.Easing easing) {
        return animate(animation.withOpacity(from, to, easing));
    }

    /** Returns a copy that animates billboard-wide opacity from fully visible to the target. */
    public Billboard opacityTo(float targetOpacity) {
        return opacityTo(targetOpacity, BillboardAnimation.Easing.LINEAR);
    }

    /** Returns a copy that animates billboard-wide opacity from fully visible to the target. */
    public Billboard opacityTo(float targetOpacity, BillboardAnimation.Easing easing) {
        return opacityFromTo(1.0F, targetOpacity, easing);
    }

    /** Returns a copy that fades every content type to fully transparent. */
    public Billboard fadeOut() {
        return fadeOut(BillboardAnimation.Easing.LINEAR);
    }

    /** Returns a copy that fades every content type from transparent to fully visible. */
    public Billboard fadeIn() {
        return fadeIn(BillboardAnimation.Easing.LINEAR);
    }

    /** Returns a copy that fades every content type from transparent to fully visible. */
    public Billboard fadeIn(BillboardAnimation.Easing easing) {
        return opacityFromTo(0.0F, 1.0F, easing);
    }

    /** Returns a copy that fades every content type to fully transparent with the supplied timing curve. */
    public Billboard fadeOut(BillboardAnimation.Easing easing) {
        return opacityTo(0.0F, easing);
    }

    /** Returns a copy with an animation sampled over its bounded lifetime. */
    public Billboard animate(BillboardAnimation newAnimation) {
        return new Billboard(id, anchor, content, rotation, scale, opacity, depthMode, durationTicks, newAnimation);
    }

    /** Returns a copy that remains visible until hidden or the client leaves the world. */
    public Billboard persistent() {
        return new Billboard(id, anchor, content, rotation, scale, opacity, depthMode, PERSISTENT, animation);
    }

    private void requireTextContent(String operation) {
        if (!(content instanceof BillboardContent.Text)) {
            throw new IllegalStateException(operation + " requires text billboard content");
        }
    }

    private static Vec3 unitScale() {
        return new Vec3(1.0D, 1.0D, 1.0D);
    }

    private static void requireScale(Vec3 value) {
        Objects.requireNonNull(value, "scale");
        if (!Double.isFinite(value.x) || !Double.isFinite(value.y) || !Double.isFinite(value.z)
                || value.x < 0.0D || value.y < 0.0D || value.z < 0.0D) {
            throw new IllegalArgumentException("scale must be finite and non-negative");
        }
    }

    private static void requireFinite(Vec3 value, String name) {
        Objects.requireNonNull(value, name);
        if (!Double.isFinite(value.x) || !Double.isFinite(value.y) || !Double.isFinite(value.z)) {
            throw new IllegalArgumentException(name + " must be finite");
        }
    }

    private static void requireOpacity(float value, String name) {
        if (!Float.isFinite(value) || value < 0.0F || value > 1.0F) {
            throw new IllegalArgumentException(name + " must be finite and between 0 and 1");
        }
    }
}
//?}
