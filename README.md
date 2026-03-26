# Amber

A cross-loader library for Minecraft mod development.

![License](https://img.shields.io/badge/license-MIT-blue.svg)

## ✨ About

Amber is a foundation library that provides **unified APIs** for common modding tasks across **Fabric**, **Forge**, and **NeoForge**.
It aims to make multi-loader development feel consistent by abstracting platform differences behind a clean common API.

## 📦 What Amber Provides

- Registry helpers (deferred registration / suppliers)
- Event system (common + client events)
- Networking utilities (simple packet/channel helpers)
- Configuration utilities
- Commands + small gameplay/dev helpers (HUD, keybind utilities, etc.)

## 📚 Documentation

Docs live in this repo at `docs/` and are published here:

- https://iamkaf.github.io/amber/

## 🗂️ Monorepo Structure

This repository contains multiple Minecraft versions of Amber:

```
amber/
├── 1.20.1/           # Minecraft 1.20.1
├── 1.21.1/           # Minecraft 1.21.1
├── 1.21.10/          # Minecraft 1.21.10
├── 1.21.11/          # Minecraft 1.21.11
├── 26.1/             # Placeholder for a future Minecraft version
├── docs/             # Documentation site (VitePress)
└── README.md         # This file
```

Each version directory follows the same layout:

- `common/` — shared code across loaders
- `fabric/` — Fabric implementation
- `forge/` — Forge implementation
- `neoforge/` — NeoForge implementation

## 🚀 Supported Versions

- 1.20.1 — ✅ Supported (Fabric + Forge)
- 1.21.1 — ✅ Supported
- 1.21.11 — ✅ Supported (Forge temporarily disabled; FG7 migration planned)
- 26.1 — 🚧 Placeholder (does not compile yet)

## 🛠️ Building

Use `just` from the repo root as the command runner.

```bash
# Build all loaders for a specific version
just build 1.21.10

# Build all supported versions
just build

# Build a specific loader for a specific version
just run 1.21.10 :fabric:build
just run 1.21.10 :forge:build
just run 1.21.10 :neoforge:build

# Run tests for a specific version
just test 1.21.10
```

Built jars will be in `<version>/<loader>/build/libs/`.

## 💻 Development

### Prerequisites

- Java 21 or higher
- Git
- just (install: https://github.com/casey/just)

### Setup

```bash
git clone https://github.com/iamkaf/amber.git
cd amber
idea 1.21.11
```

## 📝 License

MIT — see [LICENSE](LICENSE).

## 🔗 Links

- **Docs**: https://iamkaf.github.io/amber/
- **Issues**: https://github.com/iamkaf/amber/issues
- **Modrinth**: https://modrinth.com/mod/amber
