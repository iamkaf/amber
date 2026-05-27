# Support Policy

Amber uses a Perfect Parity policy for supported Minecraft lines.

The shared Amber semantic version represents one feature set. The `+<mc-version>` suffix identifies only the target Minecraft line; it does not indicate a reduced API or loader-specific feature set.

## Support Bands

| Band | Minecraft lines | Policy | Release expectation |
| --- | --- | --- | --- |
| Modern Supported | `1.20+` | Full updates and strict parity | Full compile, API, and runtime parity checks |
| Legacy Supported | `1.17-1.19.4` | Supported, future EOL candidates | Compile/API checks and representative runtime checks |
| EOL | `1.14.4-1.16.5` | Critical bugfixes only | Compile and smoke coverage; no new feature work by default |

## What Perfect Parity Means

- Public Amber APIs should compile across every supported line that advertises the feature.
- Runtime behavior should match across Modern and Legacy Supported lines unless an exception is recorded.
- EOL lines remain compatibility lines, but they do not receive new feature work by default.
- Known gaps must be recorded in docs or executable checks instead of being hidden in code.

## Source Of Truth

Amber's support state is grounded in existing project inputs and executable checks:

- `versions/*/gradle.properties` defines the loader and Minecraft matrix.
- `just testmod-compile` checks that a real consumer can compile against Amber's public API.
- TeaKit scenarios cover runtime smoke and should grow into feature probes.
- This page records intentional support policy and known exceptions.

## Current Exceptions

- Fabric networking below `1.20.5` currently throws for packet operations.
- Some NeoForge networking broadcast paths are not fully implemented.
- Modern Fabric `26.x` creative tab visibility is currently ignored by Amber's common abstraction.

These are not final policy decisions. They are recorded so future parity work can either fix them or explicitly reclassify them.
