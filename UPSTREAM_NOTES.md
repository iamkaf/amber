# Upstream Notes (Amber Template Deviations / Fixes)

Date: 2026-02-05

This file documents local changes we made during the Amber → mod-template port that should be upstreamed into `mod-template` (and/or shared build tooling) to prevent recurring breakage.

## 1) Auto-select Java per version folder (SDKMAN) in `justfile`

### Problem
We needed to build year-based/snapshot Minecraft versions that require a newer JVM (e.g. `26.1-snapshot-5` requires **Java 25**) while still supporting older versions that must run Gradle 8.x / tooling comfortably on **Java 21**.

Running `just run build --refresh-dependencies` from the repo root executes each version’s `./gradlew`, inheriting the current shell JVM. If the shell JVM is Java 25, Gradle 8.x builds fail (e.g. `Unsupported class file major version 69`).

### Solution
Update `amber/justfile` to automatically switch Java **per version** by reading `java_version=<N>` from `<version>/gradle.properties`.

Implementation details:
- Before each `./gradlew ...` invocation (in `run`, `build`, `build-loader`, `test`, and the *-changed helpers):
  - If `sdk` exists, source SDKMAN: `source "$HOME/.sdkman/bin/sdkman-init.sh"`
  - Read `java_version`:
    - `java_version=$(sed -nE 's/^java_version=([0-9]+).*/\\1/p' gradle.properties | head -n1)`
  - Switch JVM:
    - `21 -> sdk use java 21.0.9-tem`
    - `25 -> sdk use java 25.0.2-tem`
  - Error if `java_version` is missing/unsupported.

Notes:
- This avoids brittle checks like `version == '26.1'`.
- If SDKMAN is not installed, the recipes fall back to running Gradle without switching.

## 2) CI: select Java per matrix.version by reading `java_version` from `gradle.properties`

### Problem
CI previously pinned Java 21 unconditionally in `.github/workflows/build.yml`, which breaks any version that requires Java 25.

### Solution
In `.github/workflows/build.yml` (both `build` and `test` jobs):

- Add a step to detect Java version:
  - Parse `${{ matrix.version }}/gradle.properties` for `java_version=`
  - Output it as `steps.java.outputs.version`
- Configure `actions/setup-java@v4` using that output:
  - `java-version: '${{ steps.java.outputs.version }}'`

This keeps the matrix simple and makes CI automatically compatible with future versions (27.x etc.) as long as they declare `java_version`.

## 3) Remove Amber self-dependency expectation from buildSrc

### Problem
The shared `buildSrc` plugin `multiloader-common.gradle` (copied from template) used the version catalog alias `libs.amber.*` to populate an `amber_version` expansion property:

- `libs.amber.neoforge.get().version.toString()`

In the Amber repo itself, this is conceptually wrong (Amber doesn’t depend on itself) and it also breaks immediately for catalogs that don’t define `libs.amber` (notably the early `mc-26.1-snapshot-5` catalog).

### Solution
In **each Amber version folder** that has `buildSrc/src/main/groovy/multiloader-common.gradle`, change:

- `amber_version = libs.amber.neoforge.get().version.toString()`

to:

- `amber_version = project.version.toString()`

Comment added:
- `// Amber does not depend on itself; keep placeholder for resource expansion`

This preserves resource expansion keys without requiring the catalog to define an Amber dependency.

Affected version folders (at time of change):
- `1.21.1/`
- `1.21.10/`
- `1.21.11/`
- `26.1/`

## 4) 26.1 placeholder adjustments (Amber)

Request-driven changes to Amber `26.1/` to make it a placeholder that tracks the snapshot catalog:

- In `26.1/gradle.properties`:
  - `platform_minecraft_version=26.1-snapshot-5`
  - `version=0.1.0+26.1-snapshot-5`
  - `minecraft_version_range=[26.1-snapshot-5, 27)`
  - `game_versions=26.1-snapshot-5`
  - `java_version` set to **25** (toolchain intent), but note Loom requires Gradle JVM to be Java 25.

- In `26.1/settings.gradle`:
  - Disabled loaders:
    - `// include('forge')`
    - `// include('neoforge')`

Important: even with toolchains, Fabric Loom can require the **Gradle JVM** to be the required Java version (Java 25), not just the toolchain.

---

## Quick rationale summary for upstream

- The build entrypoints (`just` + CI) need to be **Java-version aware** per MC version.
- The Amber repo itself must not require a version-catalog entry for Amber as a dependency.
- Avoid hardcoding special version folder names; derive behavior from per-version `gradle.properties`.
