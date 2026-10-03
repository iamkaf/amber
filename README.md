# Amber

A cross-loader library for Minecraft mod development.

![License](https://img.shields.io/badge/license-MIT-blue.svg)

## ✨ About

Amber is a foundation library that provides **unified APIs** for common modding tasks across **Fabric**, **Forge**, and **NeoForge**.
It aims to make multi-loader development feel consistent by abstracting platform differences behind a clean common API.

## 📦 What Amber Provides

- Registry helpers (deferred registration / suppliers)
- Event system (common + client events, including client connect and disconnect)
- Permission checks that defer to each loader's permission system
- In-world billboards for text, textures, items, and block models
- `/amber doctor` diagnostics for players and server operators
- Networking utilities (simple packet/channel helpers)
- Configuration utilities
- Commands + small gameplay/dev helpers (HUD, keybind utilities, etc.)

## Repository Structure

Amber uses a Stonecutter-driven multiloader layout. The supported matrix is defined by `versions/*/gradle.properties` and exposed by `just list-nodes`.

```
amber/
├── common/           # shared code
├── fabric/           # Fabric implementation
├── forge/            # Forge implementation
├── neoforge/         # NeoForge implementation
└── versions/<mc>/    # per-version metadata
```

## Supported Versions

Every Minecraft release from 1.17 through 26.3 gets the same features and behavior:

- Fabric on every line
- Forge on every line Forge supports, from 1.17.1
- NeoForge from 1.21

## 🛠️ Building

Use `just` from the repo root as the command runner.

```bash
just list-nodes
just compile-all
just build 26.1.2-fabric
just boot-check 26.1.2-neoforge
```

Built jars will be in `<loader>/versions/<version>/build/libs/`.

## 💻 Development

### Prerequisites

- Java 25 or higher
- Git
- just (install: https://github.com/casey/just)

### Setup

```bash
git clone https://github.com/iamkaf/amber.git
cd amber
./gradlew projects
```

## 📝 License

MIT — see [LICENSE](LICENSE).

## 🔗 Links

- **Issues**: https://github.com/iamkaf/amber/issues
- **Modrinth**: https://modrinth.com/mod/amber
