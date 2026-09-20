package com.iamkaf.amber.api.networking.v1;

/** Availability on the current play connection. Pending never means absent. */
public enum PeerAvailability {
    PENDING, SUPPORTED, ABSENT, INCOMPATIBLE
}
