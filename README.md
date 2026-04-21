# Amber

Amber is a multi-loader Minecraft library for shared gameplay, registry, networking, event, and utility APIs across Fabric, Forge, and NeoForge.

## Layout

Amber uses the same branch-based Stonecutter layout as Konfig, TeaKit, and the multiloader template.

Source roots:
- `common/` for shared baseline code and resources
- `fabric/`, `forge/`, `neoforge/` for loader-specific baseline code and resources
- `versions/<mc>/...` for version overlays and version properties

There are no flat per-version project roots in the live build graph. Version selection comes from Stonecutter branches declared in `settings.gradle.kts` and `versions/<mc>/gradle.properties`.

## Supported loaders

Current workspace policy for Amber in this repo:
- Fabric: `1.14.4+`
- Forge: `1.17.1+`
- NeoForge: `1.21.1+`

The exact enabled matrix lives in `versions/*/gradle.properties`. Use `just list-nodes` to see the authoritative set.

## Build workflow

Use `just` from the repo root.

Useful commands:

```bash
just list-versions
just list-nodes

just build 1.21.11-forge
just run 1.21.11 forge build
just run 1.21.11 forge runClient

just build-all
just boot-check 1.21.11-forge 60
just boot-check-all 60
```

Notes:
- `just run <version> <loader> <task>` expands to the correct Stonecutter branch project task.
- `just build-all` runs the root `./gradlew build`.
- `boot-check` uses a bounded client run and checks the run log for the expected startup marker.

## Build output

Normal build outputs go under the active branch projects, for example:
- `fabric/versions/<mc>/build/libs/`
- `forge/versions/<mc>/build/libs/`
- `neoforge/versions/<mc>/build/libs/`

Generated Stonecutter workdirs under `common/versions`, `fabric/versions`, `forge/versions`, and `neoforge/versions` are build artifacts, not source roots.

## Documentation

Project documentation lives in `docs/`.

Published docs:
- https://iamkaf.github.io/amber/

Some docs under `docs/v8/` still describe the older release line and older dependency coordinates. Treat the code and current build graph as the source of truth if docs and repo structure disagree.

## Development notes

Prerequisites:
- Java toolchains managed through Gradle
- `just`

Common local checks:

```bash
./gradlew projects --console=plain
./gradlew build --console=plain
just boot-check 1.17.1-forge 25
just boot-check 1.21.11-forge 60
just boot-check 26.1.2-neoforge 60
```

## License

MIT. See `LICENSE`.
