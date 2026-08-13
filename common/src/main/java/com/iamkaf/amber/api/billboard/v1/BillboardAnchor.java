//? if >=1.21.11 || >=26.1 {
package com.iamkaf.amber.api.billboard.v1;

import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.UUID;

/**
 * The world-space reference used to position a billboard.
 *
 * <p>World anchors remain at an absolute position. Entity anchors follow the matching entity in
 * the viewer's current client level and add their offset to its smoothly interpolated position.
 * An entity-bound billboard is suspended while that UUID is not tracked.</p>
 */
public sealed interface BillboardAnchor permits BillboardAnchor.World, BillboardAnchor.Entity {
    static World world(Vec3 position) {
        return new World(position);
    }

    static Entity entity(net.minecraft.world.entity.Entity entity, Vec3 offset) {
        Objects.requireNonNull(entity, "entity");
        return new Entity(entity.getUUID(), offset);
    }

    /** An absolute position in the viewer's current world. */
    record World(Vec3 position) implements BillboardAnchor {
        public World {
            requireFinite(position, "position");
        }
    }

    /** An entity UUID and world-axis offset from its interpolated position. */
    record Entity(UUID entityId, Vec3 offset) implements BillboardAnchor {
        public Entity {
            Objects.requireNonNull(entityId, "entityId");
            requireFinite(offset, "offset");
        }
    }

    private static void requireFinite(Vec3 value, String name) {
        Objects.requireNonNull(value, name);
        if (!Double.isFinite(value.x) || !Double.isFinite(value.y) || !Double.isFinite(value.z)) {
            throw new IllegalArgumentException(name + " must be finite");
        }
    }
}
//?}
