/**
 * Internal networking system for Amber.
 * 
 * <p>This package contains Amber's internal networking functionality, including:</p>
 * <ul>
 *   <li>Ping-pong latency measurement system using the networking API</li>
 *   <li>Network diagnostics and connectivity testing</li>
 *   <li>Internal packet definitions and handlers</li>
 * </ul>
 * 
 * <p>These classes are for internal use by Amber and should not be used directly 
 * by mod developers. Use the public networking API in {@code com.iamkaf.amber.api.networking.v1} instead.</p>
 * 
 * <p>This implementation uses the user-friendly networking API, providing a clean abstraction
 * over platform-specific networking implementations.</p>
 * 
 * @see com.iamkaf.amber.api.networking.v1 For the public networking API
 */
package com.iamkaf.amber.networking.v1;