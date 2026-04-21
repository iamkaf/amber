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

## Release workflow

The docs site is primarily consumer and API documentation. For actual Amber release work, use the repo root workflow here.

Recommended release sequence:

1. Update release notes in `changelog.md`.
2. Build the full matrix:

```bash
just build-all
```

3. Run bounded runtime checks for the nodes touched by the release. Common Amber checks:

```bash
just boot-check 1.17.1-forge 25
just boot-check 1.21.11-forge 60
just boot-check 26.1.2-neoforge 90
```

4. Publish the target Minecraft line to Kaf Maven:

```bash
just run 26.1.2 publish
```

This publishes `common` plus every enabled loader for that Minecraft line to the configured Kaf Maven repository.

5. Do any manual production-instance validation you need before storefront publishing.

6. Publish to Modrinth and CurseForge for the target line:

```bash
just run 26.1.2 publishRelease
```

Useful publish variants:

```bash
# Publish one loader for one Minecraft line to Kaf Maven
just run 26.1.2 neoforge publish

# Dry-run storefront publishing for one Minecraft line
just run 26.1.2 publishRelease -Ppublish.dry-run=true

# Publish only one storefront for one Minecraft line
just run 26.1.2 publishModrinth
just run 26.1.2 publishCurseforge

# Publish only one loader on one Minecraft line to one storefront
just run 26.1.2 neoforge publishModrinth
just run 26.1.2 neoforge publishCurseforge

# Dry-run one loader on one Minecraft line
just run 26.1.2 neoforge publishModrinth -Ppublish.dry-run=true
```

Notes:
- `publishRelease` runs both CurseForge and Modrinth tasks for the enabled loaders on that line.
- Repo default is currently `publish.dry-run=false` in `gradle.properties`, so pass `-Ppublish.dry-run=true` explicitly when you want a dry run.
- If a release depends on updated shared artifacts such as `version-catalog` or `multiloader-conventions`, publish those first.
- For the authoritative loader matrix, use `just list-nodes`.

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
The docs site is aimed at Amber consumers. Release, build, and publishing operations should follow this README and the repo's live `justfile`/Gradle tasks.

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
