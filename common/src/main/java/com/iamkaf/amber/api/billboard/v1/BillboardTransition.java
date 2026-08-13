//? if >=1.21.11 || >=26.1 {
package com.iamkaf.amber.api.billboard.v1;

import java.util.Objects;

/** Presentation-time duration and easing for a live billboard property change. */
public record BillboardTransition(
        int durationTicks,
        BillboardAnimation.Easing easing
) {
    public BillboardTransition {
        Objects.requireNonNull(easing, "easing");
        if (durationTicks <= 0) {
            throw new IllegalArgumentException("durationTicks must be positive");
        }
    }
}
//?}
