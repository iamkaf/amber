//? if >=1.21.11 || >=26.1 {
package com.iamkaf.amber.api.billboard.v1;

/** Controls whether world geometry can occlude a billboard. */
public enum BillboardDepthMode {
    /** The normal world-rendering behavior: nearer geometry hides the billboard. */
    DEPTH_TESTED,
    /** Renders the billboard over world geometry, suitable for navigation indicators. */
    THROUGH_WALLS
}
//?}
