//? if >=1.21.11 || >=26.1 {
package com.iamkaf.amber.networking.v1;

import com.iamkaf.amber.api.billboard.v1.Billboard;
import com.iamkaf.amber.api.billboard.v1.BillboardAnimation;
import com.iamkaf.amber.api.billboard.v1.BillboardAnchor;
import com.iamkaf.amber.api.billboard.v1.BillboardContent;
import com.iamkaf.amber.api.billboard.v1.BillboardTransition;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

final class BillboardPacketCodec {
    private static final int TEXTURE = 0;
    private static final int ITEM = 1;
    private static final int TEXT = 2;
    private static final int ANCHOR_WORLD = 0;
    private static final int ANCHOR_ENTITY = 1;
    private static final int ANIMATION_NONE = 0;
    private static final int ANIMATION_TRANSLATION = 1;
    private static final int ANIMATION_TRACKS = 2;
    private static final int TRACK_TRANSLATION = 1;
    private static final int TRACK_TEXT_COLOR = 1 << 1;
    private static final int TRACK_TEXT_OPACITY = 1 << 2;
    private static final int TRACK_SCALE = 1 << 3;
    private static final int EASING_LINEAR = 0;
    private static final int EASING_EASE_OUT_CUBIC = 1;
    private static final int EASING_EASE_IN_SINE = 2;
    private static final int EASING_EASE_OUT_SINE = 3;
    private static final int EASING_EASE_IN_OUT_SINE = 4;
    private static final int EASING_EASE_IN_QUAD = 5;
    private static final int EASING_EASE_OUT_QUAD = 6;
    private static final int EASING_EASE_IN_OUT_QUAD = 7;
    private static final int EASING_EASE_IN_CUBIC = 8;
    private static final int EASING_EASE_IN_OUT_CUBIC = 9;
    private static final int EASING_EASE_IN_BACK = 10;
    private static final int EASING_EASE_OUT_BACK = 11;
    private static final int EASING_EASE_IN_OUT_BACK = 12;
    private static final int EASING_EASE_IN_BOUNCE = 13;
    private static final int EASING_EASE_OUT_BOUNCE = 14;
    private static final int EASING_EASE_IN_OUT_BOUNCE = 15;
    private static final int EASING_EASE_IN_ELASTIC = 16;
    private static final int EASING_EASE_OUT_ELASTIC = 17;
    private static final int EASING_EASE_IN_OUT_ELASTIC = 18;

    private BillboardPacketCodec() {
    }

    static void encode(Billboard billboard, FriendlyByteBuf buffer) {
        buffer.writeUUID(billboard.id());
        encodeAnchor(billboard.anchor(), buffer);
        writeVec3(buffer, billboard.scale());
        buffer.writeVarInt(billboard.durationTicks());

        BillboardAnimation animation = billboard.animation();
        if (animation.isNone()) {
            buffer.writeVarInt(ANIMATION_NONE);
        } else {
            buffer.writeVarInt(ANIMATION_TRACKS);
            int tracks = 0;
            if (animation.translation() != null) {
                tracks |= TRACK_TRANSLATION;
            }
            if (animation.textColor() != null) {
                tracks |= TRACK_TEXT_COLOR;
            }
            if (animation.textOpacity() != null) {
                tracks |= TRACK_TEXT_OPACITY;
            }
            if (animation.scale() != null) {
                tracks |= TRACK_SCALE;
            }
            buffer.writeVarInt(tracks);
            if (animation.translation() != null) {
                writeTranslation(buffer, animation.translation());
            }
            if (animation.textColor() != null) {
                buffer.writeInt(animation.textColor().targetColor());
                buffer.writeVarInt(encodeEasing(animation.textColor().easing()));
            }
            if (animation.textOpacity() != null) {
                buffer.writeFloat(animation.textOpacity().targetOpacity());
                buffer.writeVarInt(encodeEasing(animation.textOpacity().easing()));
            }
            if (animation.scale() != null) {
                writeVec3(buffer, animation.scale().from());
                writeVec3(buffer, animation.scale().to());
                buffer.writeVarInt(encodeEasing(animation.scale().easing()));
            }
        }

        switch (billboard.content()) {
            case BillboardContent.Texture texture -> {
                buffer.writeVarInt(TEXTURE);
                buffer.writeIdentifier(texture.texture());
                buffer.writeFloat(texture.width());
                buffer.writeFloat(texture.height());
            }
            case BillboardContent.Item item -> {
                buffer.writeVarInt(ITEM);
                buffer.writeIdentifier(item.item());
                buffer.writeFloat(item.scale());
            }
            case BillboardContent.Text text -> {
                buffer.writeVarInt(TEXT);
                ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.encode(buffer, text.text());
                buffer.writeFloat(text.scale());
                buffer.writeInt(text.color());
            }
        }
    }

    static Billboard decode(FriendlyByteBuf buffer) {
        UUID id = buffer.readUUID();
        BillboardAnchor anchor = decodeAnchor(buffer);
        Vec3 scale = readVec3(buffer);
        int durationTicks = buffer.readVarInt();
        BillboardAnimation animation = switch (buffer.readVarInt()) {
            case ANIMATION_NONE -> BillboardAnimation.NONE;
            case ANIMATION_TRANSLATION -> {
                Vec3 offset = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
                yield BillboardAnimation.translate(offset, decodeEasing(buffer.readVarInt()));
            }
            case ANIMATION_TRACKS -> decodeAnimationTracks(buffer);
            default -> throw new IllegalArgumentException("Unknown billboard animation type");
        };
        BillboardContent content = switch (buffer.readVarInt()) {
            case TEXTURE -> new BillboardContent.Texture(buffer.readIdentifier(), buffer.readFloat(), buffer.readFloat());
            case ITEM -> new BillboardContent.Item(buffer.readIdentifier(), buffer.readFloat());
            case TEXT -> {
                Component component = ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.decode(buffer);
                yield new BillboardContent.Text(component, buffer.readFloat(), buffer.readInt());
            }
            default -> throw new IllegalArgumentException("Unknown billboard content type");
        };
        return new Billboard(id, anchor, content, scale, durationTicks, animation);
    }

    static void encodeAnchor(BillboardAnchor anchor, FriendlyByteBuf buffer) {
        switch (anchor) {
            case BillboardAnchor.World world -> {
                buffer.writeVarInt(ANCHOR_WORLD);
                writeVec3(buffer, world.position());
            }
            case BillboardAnchor.Entity entity -> {
                buffer.writeVarInt(ANCHOR_ENTITY);
                buffer.writeUUID(entity.entityId());
                writeVec3(buffer, entity.offset());
            }
        }
    }

    static BillboardAnchor decodeAnchor(FriendlyByteBuf buffer) {
        return switch (buffer.readVarInt()) {
            case ANCHOR_WORLD -> BillboardAnchor.world(readVec3(buffer));
            case ANCHOR_ENTITY -> new BillboardAnchor.Entity(buffer.readUUID(), readVec3(buffer));
            default -> throw new IllegalArgumentException("Unknown billboard anchor type");
        };
    }

    static void encodeTransition(BillboardTransition transition, FriendlyByteBuf buffer) {
        buffer.writeVarInt(transition.durationTicks());
        buffer.writeVarInt(encodeEasing(transition.easing()));
    }

    static BillboardTransition decodeTransition(FriendlyByteBuf buffer) {
        return new BillboardTransition(buffer.readVarInt(), decodeEasing(buffer.readVarInt()));
    }

    private static void writeVec3(FriendlyByteBuf buffer, Vec3 vector) {
        buffer.writeDouble(vector.x);
        buffer.writeDouble(vector.y);
        buffer.writeDouble(vector.z);
    }

    private static Vec3 readVec3(FriendlyByteBuf buffer) {
        return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    private static void writeTranslation(FriendlyByteBuf buffer, BillboardAnimation.Translation translation) {
        buffer.writeDouble(translation.offset().x);
        buffer.writeDouble(translation.offset().y);
        buffer.writeDouble(translation.offset().z);
        buffer.writeVarInt(encodeEasing(translation.easing()));
    }

    private static BillboardAnimation decodeAnimationTracks(FriendlyByteBuf buffer) {
        int tracks = buffer.readVarInt();
        int knownTracks = TRACK_TRANSLATION | TRACK_TEXT_COLOR | TRACK_TEXT_OPACITY | TRACK_SCALE;
        if (tracks == 0 || (tracks & ~knownTracks) != 0) {
            throw new IllegalArgumentException("Unknown or empty billboard animation tracks");
        }
        BillboardAnimation animation = BillboardAnimation.NONE;
        if ((tracks & TRACK_TRANSLATION) != 0) {
            Vec3 offset = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            animation = animation.withTranslation(offset, decodeEasing(buffer.readVarInt()));
        }
        if ((tracks & TRACK_TEXT_COLOR) != 0) {
            animation = animation.withTextColor(buffer.readInt(), decodeEasing(buffer.readVarInt()));
        }
        if ((tracks & TRACK_TEXT_OPACITY) != 0) {
            animation = animation.withTextOpacity(buffer.readFloat(), decodeEasing(buffer.readVarInt()));
        }
        if ((tracks & TRACK_SCALE) != 0) {
            animation = animation.withScale(readVec3(buffer), readVec3(buffer), decodeEasing(buffer.readVarInt()));
        }
        return animation;
    }

    private static int encodeEasing(BillboardAnimation.Easing easing) {
        return switch (easing) {
            case LINEAR -> EASING_LINEAR;
            case EASE_OUT_CUBIC -> EASING_EASE_OUT_CUBIC;
            case EASE_IN_SINE -> EASING_EASE_IN_SINE;
            case EASE_OUT_SINE -> EASING_EASE_OUT_SINE;
            case EASE_IN_OUT_SINE -> EASING_EASE_IN_OUT_SINE;
            case EASE_IN_QUAD -> EASING_EASE_IN_QUAD;
            case EASE_OUT_QUAD -> EASING_EASE_OUT_QUAD;
            case EASE_IN_OUT_QUAD -> EASING_EASE_IN_OUT_QUAD;
            case EASE_IN_CUBIC -> EASING_EASE_IN_CUBIC;
            case EASE_IN_OUT_CUBIC -> EASING_EASE_IN_OUT_CUBIC;
            case EASE_IN_BACK -> EASING_EASE_IN_BACK;
            case EASE_OUT_BACK -> EASING_EASE_OUT_BACK;
            case EASE_IN_OUT_BACK -> EASING_EASE_IN_OUT_BACK;
            case EASE_IN_BOUNCE -> EASING_EASE_IN_BOUNCE;
            case EASE_OUT_BOUNCE -> EASING_EASE_OUT_BOUNCE;
            case EASE_IN_OUT_BOUNCE -> EASING_EASE_IN_OUT_BOUNCE;
            case EASE_IN_ELASTIC -> EASING_EASE_IN_ELASTIC;
            case EASE_OUT_ELASTIC -> EASING_EASE_OUT_ELASTIC;
            case EASE_IN_OUT_ELASTIC -> EASING_EASE_IN_OUT_ELASTIC;
        };
    }

    private static BillboardAnimation.Easing decodeEasing(int easingId) {
        return switch (easingId) {
            case EASING_LINEAR -> BillboardAnimation.Easing.LINEAR;
            case EASING_EASE_OUT_CUBIC -> BillboardAnimation.Easing.EASE_OUT_CUBIC;
            case EASING_EASE_IN_SINE -> BillboardAnimation.Easing.EASE_IN_SINE;
            case EASING_EASE_OUT_SINE -> BillboardAnimation.Easing.EASE_OUT_SINE;
            case EASING_EASE_IN_OUT_SINE -> BillboardAnimation.Easing.EASE_IN_OUT_SINE;
            case EASING_EASE_IN_QUAD -> BillboardAnimation.Easing.EASE_IN_QUAD;
            case EASING_EASE_OUT_QUAD -> BillboardAnimation.Easing.EASE_OUT_QUAD;
            case EASING_EASE_IN_OUT_QUAD -> BillboardAnimation.Easing.EASE_IN_OUT_QUAD;
            case EASING_EASE_IN_CUBIC -> BillboardAnimation.Easing.EASE_IN_CUBIC;
            case EASING_EASE_IN_OUT_CUBIC -> BillboardAnimation.Easing.EASE_IN_OUT_CUBIC;
            case EASING_EASE_IN_BACK -> BillboardAnimation.Easing.EASE_IN_BACK;
            case EASING_EASE_OUT_BACK -> BillboardAnimation.Easing.EASE_OUT_BACK;
            case EASING_EASE_IN_OUT_BACK -> BillboardAnimation.Easing.EASE_IN_OUT_BACK;
            case EASING_EASE_IN_BOUNCE -> BillboardAnimation.Easing.EASE_IN_BOUNCE;
            case EASING_EASE_OUT_BOUNCE -> BillboardAnimation.Easing.EASE_OUT_BOUNCE;
            case EASING_EASE_IN_OUT_BOUNCE -> BillboardAnimation.Easing.EASE_IN_OUT_BOUNCE;
            case EASING_EASE_IN_ELASTIC -> BillboardAnimation.Easing.EASE_IN_ELASTIC;
            case EASING_EASE_OUT_ELASTIC -> BillboardAnimation.Easing.EASE_OUT_ELASTIC;
            case EASING_EASE_IN_OUT_ELASTIC -> BillboardAnimation.Easing.EASE_IN_OUT_ELASTIC;
            default -> throw new IllegalArgumentException("Unknown billboard animation easing");
        };
    }
}
//?}
