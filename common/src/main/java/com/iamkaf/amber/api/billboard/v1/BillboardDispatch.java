//? if >=1.21.11 || >=26.1 {
package com.iamkaf.amber.api.billboard.v1;

/** Describes how a side-safe billboard operation reached the client. */
public enum BillboardDispatch {
    /** No operation was sent because the target player had already disconnected. */
    IGNORED,
    /** The caller was already on the logical client, so no packet was used. */
    IMMEDIATE,
    /** The caller was on the logical server, so Amber sent a clientbound packet. */
    CLIENTBOUND_PACKET
}
//?}
