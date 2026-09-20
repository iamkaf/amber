//? if >=1.21.11 {
package com.iamkaf.amber.api.doctor.v1;

/** A check's evaluated result. Unknown never establishes health. */
public enum DoctorStatus {
    OK, UNKNOWN, WARNING, ERROR;

    public DoctorStatus combine(DoctorStatus other) {
        return ordinal() >= other.ordinal() ? this : other;
    }
}
//?}
