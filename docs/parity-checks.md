# Parity Checks

Amber's parity contract should be enforced by checks that run, not by a second hand-maintained feature registry.

## Current Checks

- `just scenario-check <node>` boots the existing TeaKit doctor scenario for one loader/version node.
- `just scenario-check-all` runs the doctor scenario across the enabled matrix.

## Runtime Conformance

Amber runtime conformance checks should live in an external consumer testmod. The testmod should depend on Amber as a normal consumer, register callbacks through Amber's public APIs, and record observed behavior through TeaKit spies.

TeaKit should drive real gameplay and assert spy calls, counts, order, and arguments. Amber production code should not contain test-only probe commands, counters, or TeaKit dependencies.

## Feature Slices

Runtime conformance scenarios should grow to cover these behavior slices:

- commands
- block events
- entity and player events
- client events
- registry behavior
- creative tabs
- networking channels
- common functions with observable game effects
- platform info

Later phases should split these into smaller consumer-owned runtime checks with concrete pass/fail assertions.

## Exception Policy

Known differences should be documented in the support policy, linked to issues when practical, and reflected in executable checks when the behavior can be asserted. Silent divergence is not compatible with Perfect Parity.
