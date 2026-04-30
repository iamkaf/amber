# Parity Checks

Amber's parity contract should be enforced by checks that run, not by a second hand-maintained feature registry.

## Current Checks

- `just testmod-compile` compiles a development-only consumer mod against Amber's public API.
- `just scenario-check <node>` boots the existing TeaKit doctor scenario for one loader/version node.
- `just scenario-check-all` runs the doctor scenario across the enabled matrix.

## Test Mod

The `testmod` project is intentionally development-only. It imports Amber APIs like a consumer would, without depending on Amber internals.

Phase 1 covers compile-time API shape. Later phases should turn `testmod` into a runtime probe mod that TeaKit can drive in-game.

## Feature Slices

The compile fixture currently exercises these coarse API slices:

- commands
- block events
- entity and player events
- client events
- deferred registry
- creative tabs
- networking channels
- common functions
- platform info

Later phases should split these into smaller runtime probes with concrete pass/fail assertions.

## Exception Policy

Known differences should be documented in the support policy, linked to issues when practical, and reflected in executable checks when the behavior can be asserted. Silent divergence is not compatible with Perfect Parity.
