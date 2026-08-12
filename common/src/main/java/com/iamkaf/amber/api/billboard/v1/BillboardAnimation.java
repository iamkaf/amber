//? if >=1.21.11 || >=26.1 {
package com.iamkaf.amber.api.billboard.v1;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Composable, serializable animation tracks sampled over a billboard's finite lifetime.
 *
 * <p>Translation, local-axis scale, billboard opacity, text color, and text opacity tracks can run concurrently and
 * may use different timing curves.</p>
 */
public record BillboardAnimation(
        @Nullable Translation translation,
        @Nullable Rotation rotation,
        @Nullable Scale scale,
        @Nullable TextColor textColor,
        @Nullable TextOpacity textOpacity,
        @Nullable Opacity opacity
) {
    public static final BillboardAnimation NONE = new BillboardAnimation(null, null, null, null, null, null);

    /** Source-compatible constructor for the original animation-track value shape. */
    public BillboardAnimation(
            @Nullable Translation translation,
            @Nullable Scale scale,
            @Nullable TextColor textColor,
            @Nullable TextOpacity textOpacity
    ) {
        this(translation, null, scale, textColor, textOpacity, null);
    }

    public static BillboardAnimation translate(Vec3 offset) {
        return translate(offset, Easing.LINEAR);
    }

    public static BillboardAnimation translate(Vec3 offset, Easing easing) {
        return NONE.withTranslation(Vec3.ZERO, offset, easing);
    }

    public boolean isNone() {
        return translation == null && rotation == null && scale == null && textColor == null && textOpacity == null && opacity == null;
    }

    public BillboardAnimation withTranslation(Vec3 offset, Easing easing) {
        return withTranslation(Vec3.ZERO, offset, easing);
    }

    public BillboardAnimation withTranslation(Vec3 from, Vec3 to, Easing easing) {
        return new BillboardAnimation(new Translation(from, to, easing), rotation, scale, textColor, textOpacity, opacity);
    }

    public BillboardAnimation withRotation(Vec3 from, Vec3 to, Easing easing) {
        return new BillboardAnimation(translation, new Rotation(from, to, easing), scale, textColor, textOpacity, opacity);
    }

    public BillboardAnimation withScale(Vec3 from, Vec3 to, Easing easing) {
        return new BillboardAnimation(translation, rotation, new Scale(from, to, easing), textColor, textOpacity, opacity);
    }

    public BillboardAnimation withTextColor(int targetColor, Easing easing) {
        return new BillboardAnimation(translation, rotation, scale, new TextColor(targetColor, easing), textOpacity, opacity);
    }

    public BillboardAnimation withTextOpacity(float targetOpacity, Easing easing) {
        return new BillboardAnimation(translation, rotation, scale, textColor, new TextOpacity(targetOpacity, easing), opacity);
    }

    public BillboardAnimation withOpacity(float from, float to, Easing easing) {
        return new BillboardAnimation(translation, rotation, scale, textColor, textOpacity, new Opacity(from, to, easing));
    }

    /** Returns the world-space offset at normalized lifetime progress. */
    public Vec3 offsetAt(double progress) {
        return translation == null ? Vec3.ZERO : translation.offsetAt(progress);
    }

    /** Returns the local-axis animated rotation in degrees at normalized lifetime progress. */
    public Vec3 rotationAt(double progress) {
        return rotation == null ? Vec3.ZERO : rotation.rotationAt(progress);
    }

    /** Returns the local-axis scale multiplier at normalized lifetime progress. */
    public Vec3 scaleAt(double progress) {
        return scale == null ? new Vec3(1.0D, 1.0D, 1.0D) : scale.scaleAt(progress);
    }

    /** Returns the billboard-wide opacity multiplier at normalized lifetime progress. */
    public double opacityAt(double progress) {
        return opacity == null ? 1.0D : opacity.opacityAt(progress);
    }

    /**
     * Returns animated default text ARGB at normalized lifetime progress.
     *
     * <p>Color animation interpolates RGB. Opacity animation multiplies the base alpha, allowing
     * a translucent starting color and a fade track to compose predictably.</p>
     */
    public int textColorAt(int baseColor, double progress) {
        int color = textColor == null ? baseColor : textColor.colorAt(baseColor, progress);
        if (textOpacity == null) {
            return color;
        }
        int baseAlpha = color >>> 24;
        int animatedAlpha = clampChannel((int) Math.round(baseAlpha * textOpacity.opacityAt(progress)));
        return animatedAlpha << 24 | color & 0x00FFFFFF;
    }

    private static int interpolateChannel(int start, int end, double progress) {
        return clampChannel((int) Math.round(start + (end - start) * progress));
    }

    private static int clampChannel(int value) {
        return Math.max(0, Math.min(255, value));
    }

    /** A world-space translation from the billboard's original position. */
    public record Translation(Vec3 from, Vec3 to, Easing easing) {
        public Translation(Vec3 offset, Easing easing) {
            this(Vec3.ZERO, offset, easing);
        }

        public Translation {
            requireFinite(from, "from");
            requireFinite(to, "to");
            Objects.requireNonNull(easing, "easing");
        }

        public Vec3 offsetAt(double progress) {
            return from.lerp(to, easing.apply(progress));
        }

        /** Backwards-compatible shorthand for zero-to-target translation callers. */
        public Vec3 offset() {
            return to;
        }
    }

    /** A local-axis rotation transition in degrees. */
    public record Rotation(Vec3 from, Vec3 to, Easing easing) {
        public Rotation {
            requireFinite(from, "from");
            requireFinite(to, "to");
            Objects.requireNonNull(easing, "easing");
        }

        public Vec3 rotationAt(double progress) {
            return from.lerp(to, easing.apply(progress));
        }
    }

    /** A local-axis scale transition, suitable for squash and stretch. */
    public record Scale(Vec3 from, Vec3 to, Easing easing) {
        public Scale {
            requireScale(from, "from");
            requireScale(to, "to");
            Objects.requireNonNull(easing, "easing");
        }

        public Vec3 scaleAt(double progress) {
            double eased = easing.apply(progress);
            Vec3 interpolated = from.lerp(to, eased);
            return new Vec3(
                    Math.max(0.0D, interpolated.x),
                    Math.max(0.0D, interpolated.y),
                    Math.max(0.0D, interpolated.z)
            );
        }

        private static void requireScale(Vec3 value, String name) {
            Objects.requireNonNull(value, name);
            if (!Double.isFinite(value.x) || !Double.isFinite(value.y) || !Double.isFinite(value.z)
                    || value.x < 0.0D || value.y < 0.0D || value.z < 0.0D) {
                throw new IllegalArgumentException(name + " scale must be finite and non-negative");
            }
        }
    }

    /** An RGB transition; the base text color continues to own initial alpha. */
    public record TextColor(int targetColor, Easing easing) {
        public TextColor {
            Objects.requireNonNull(easing, "easing");
        }

        public int colorAt(int baseColor, double progress) {
            double eased = easing.apply(progress);
            int red = interpolateChannel(baseColor >> 16 & 0xFF, targetColor >> 16 & 0xFF, eased);
            int green = interpolateChannel(baseColor >> 8 & 0xFF, targetColor >> 8 & 0xFF, eased);
            int blue = interpolateChannel(baseColor & 0xFF, targetColor & 0xFF, eased);
            return baseColor & 0xFF000000 | red << 16 | green << 8 | blue;
        }
    }

    /** A multiplicative opacity transition from {@code 1.0} to the target value. */
    public record TextOpacity(float targetOpacity, Easing easing) {
        public TextOpacity {
            Objects.requireNonNull(easing, "easing");
            if (!Float.isFinite(targetOpacity) || targetOpacity < 0.0F || targetOpacity > 1.0F) {
                throw new IllegalArgumentException("targetOpacity must be finite and between 0 and 1");
            }
        }

        public double opacityAt(double progress) {
            return 1.0D + (targetOpacity - 1.0D) * easing.apply(progress);
        }
    }

    /** A billboard-wide opacity transition that applies to every content type. */
    public record Opacity(float from, float to, Easing easing) {
        public Opacity {
            requireOpacity(from, "from");
            requireOpacity(to, "to");
            Objects.requireNonNull(easing, "easing");
        }

        public double opacityAt(double progress) {
            double value = from + (to - from) * easing.apply(progress);
            return Math.max(0.0D, Math.min(1.0D, value));
        }

        private static void requireOpacity(float value, String name) {
            if (!Float.isFinite(value) || value < 0.0F || value > 1.0F) {
                throw new IllegalArgumentException(name + " opacity must be finite and between 0 and 1");
            }
        }
    }

    private static void requireFinite(Vec3 value, String name) {
        Objects.requireNonNull(value, name);
        if (!Double.isFinite(value.x) || !Double.isFinite(value.y) || !Double.isFinite(value.z)) {
            throw new IllegalArgumentException(name + " must be finite");
        }
    }

    /**
     * Timing curves supported by Amber's packet-safe billboard animations.
     *
     * <p>Back, bounce, and elastic curves may intentionally overshoot or briefly reverse. All
     * curves clamp input progress to {@code [0, 1]} and return exact endpoints.</p>
     */
    public enum Easing {
        LINEAR,
        EASE_OUT_CUBIC,
        EASE_IN_SINE,
        EASE_OUT_SINE,
        EASE_IN_OUT_SINE,
        EASE_IN_QUAD,
        EASE_OUT_QUAD,
        EASE_IN_OUT_QUAD,
        EASE_IN_CUBIC,
        EASE_IN_OUT_CUBIC,
        EASE_IN_BACK,
        EASE_OUT_BACK,
        EASE_IN_OUT_BACK,
        EASE_IN_BOUNCE,
        EASE_OUT_BOUNCE,
        EASE_IN_OUT_BOUNCE,
        EASE_IN_ELASTIC,
        EASE_OUT_ELASTIC,
        EASE_IN_OUT_ELASTIC;

        private static final double BACK_OVERSHOOT = 1.70158D;
        private static final double BACK_IN_OUT_OVERSHOOT = BACK_OVERSHOOT * 1.525D;

        public double apply(double progress) {
            double clamped = Math.max(0.0D, Math.min(1.0D, progress));
            if (clamped == 0.0D || clamped == 1.0D) {
                return clamped;
            }
            return switch (this) {
                case LINEAR -> clamped;
                case EASE_IN_SINE -> 1.0D - Math.cos(clamped * Math.PI / 2.0D);
                case EASE_OUT_SINE -> Math.sin(clamped * Math.PI / 2.0D);
                case EASE_IN_OUT_SINE -> -(Math.cos(Math.PI * clamped) - 1.0D) / 2.0D;
                case EASE_IN_QUAD -> clamped * clamped;
                case EASE_OUT_QUAD -> 1.0D - square(1.0D - clamped);
                case EASE_IN_OUT_QUAD -> clamped < 0.5D
                        ? 2.0D * clamped * clamped
                        : 1.0D - square(-2.0D * clamped + 2.0D) / 2.0D;
                case EASE_IN_CUBIC -> clamped * clamped * clamped;
                case EASE_OUT_CUBIC -> 1.0D - cube(1.0D - clamped);
                case EASE_IN_OUT_CUBIC -> clamped < 0.5D
                        ? 4.0D * clamped * clamped * clamped
                        : 1.0D - cube(-2.0D * clamped + 2.0D) / 2.0D;
                case EASE_IN_BACK -> (BACK_OVERSHOOT + 1.0D) * cube(clamped)
                        - BACK_OVERSHOOT * square(clamped);
                case EASE_OUT_BACK -> 1.0D + (BACK_OVERSHOOT + 1.0D) * cube(clamped - 1.0D)
                        + BACK_OVERSHOOT * square(clamped - 1.0D);
                case EASE_IN_OUT_BACK -> clamped < 0.5D
                        ? square(2.0D * clamped) * ((BACK_IN_OUT_OVERSHOOT + 1.0D) * 2.0D * clamped - BACK_IN_OUT_OVERSHOOT) / 2.0D
                        : (square(2.0D * clamped - 2.0D) * ((BACK_IN_OUT_OVERSHOOT + 1.0D) * (clamped * 2.0D - 2.0D) + BACK_IN_OUT_OVERSHOOT) + 2.0D) / 2.0D;
                case EASE_IN_BOUNCE -> 1.0D - bounceOut(1.0D - clamped);
                case EASE_OUT_BOUNCE -> bounceOut(clamped);
                case EASE_IN_OUT_BOUNCE -> clamped < 0.5D
                        ? (1.0D - bounceOut(1.0D - 2.0D * clamped)) / 2.0D
                        : (1.0D + bounceOut(2.0D * clamped - 1.0D)) / 2.0D;
                case EASE_IN_ELASTIC -> -Math.pow(2.0D, 10.0D * clamped - 10.0D)
                        * Math.sin((clamped * 10.0D - 10.75D) * (2.0D * Math.PI / 3.0D));
                case EASE_OUT_ELASTIC -> Math.pow(2.0D, -10.0D * clamped)
                        * Math.sin((clamped * 10.0D - 0.75D) * (2.0D * Math.PI / 3.0D)) + 1.0D;
                case EASE_IN_OUT_ELASTIC -> clamped < 0.5D
                        ? -(Math.pow(2.0D, 20.0D * clamped - 10.0D)
                        * Math.sin((20.0D * clamped - 11.125D) * (2.0D * Math.PI / 4.5D))) / 2.0D
                        : Math.pow(2.0D, -20.0D * clamped + 10.0D)
                        * Math.sin((20.0D * clamped - 11.125D) * (2.0D * Math.PI / 4.5D)) / 2.0D + 1.0D;
            };
        }

        private static double bounceOut(double progress) {
            double divisor = 2.75D;
            double coefficient = 7.5625D;
            if (progress < 1.0D / divisor) {
                return coefficient * progress * progress;
            }
            if (progress < 2.0D / divisor) {
                double shifted = progress - 1.5D / divisor;
                return coefficient * shifted * shifted + 0.75D;
            }
            if (progress < 2.5D / divisor) {
                double shifted = progress - 2.25D / divisor;
                return coefficient * shifted * shifted + 0.9375D;
            }
            double shifted = progress - 2.625D / divisor;
            return coefficient * shifted * shifted + 0.984375D;
        }

        private static double square(double value) {
            return value * value;
        }

        private static double cube(double value) {
            return value * value * value;
        }
    }
}
//?}
