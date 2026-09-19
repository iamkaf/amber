//? if >=1.21.11 {
package com.iamkaf.amber.api.doctor.v1;

/**
 * Reads a fresh snapshot on the calling side's game thread. Do not block, send
 * packets, or change gameplay. A failure is isolated to this contributor.
 */
@FunctionalInterface
public interface DoctorContributor<C> {
    void contribute(C context, DoctorSection section);
}
//?}
