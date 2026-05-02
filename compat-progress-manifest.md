# Amber Compat Progress Manifest

This manifest tracks the Phase 2 maintainability work around Amber version compat overlays. It is a planning and reporting aid, not the source of truth for parity. Runtime parity remains proven by `amber-conformance`; compile and source-shape progress is proven by the Amber build and source tree.

## Status Codes

| Code | Meaning |
| --- | --- |
| `PASS` | Implemented in resolved version overlays and verified by relevant compile/runtime checks. |
| `WIP` | Work exists locally or conceptually, but is not fully verified or committed. |
| `TODO` | Expected work has not started. |
| `PARTIAL` | Some versions or call sites are clean, but the band is not complete. |
| `BLOCKED` | Progress depends on a decision, missing symbol research, or another repo/tooling fix. |
| `N/A` | Does not apply to that version band or loader. |

Do not mark a row `PASS` only because a file exists. For overlay work, `PASS` requires:

- version overlay source contains only code for that version, with no Stonecutter guard residue
- no reflection is used for Minecraft or loader API calls
- representative nodes in that band compile sequentially
- runtime conformance remains green when the touched behavior affects runtime paths

## Current Position

| Area | Status | Evidence |
| --- | --- | --- |
| Stonecutter convention upgrade | `PASS` | Committed in `multiloader-conventions` as `db980f0 Upgrade Stonecutter conventions`. |
| Legacy compat extracted to overlays | `PASS` | Committed in Amber as `a17cec2 Move Amber legacy compat to overlays`. |
| Versioned `ItemCompat` / `PlayerCompat` / `WorldCompat` overlays added | `PASS` | Committed in Amber as `5753898 Add versioned compat overlays`; representative common compile checks passed before commit. |
| Overlay guard/comment cleanup | `PASS` | Local uncommitted cleanup removed Stonecutter guard residue from version compat overlays. Residue scan is clean and every common node compiles sequentially. |
| Reflection removal | `PARTIAL` | `26.1.x` and local `1.21.x` `ItemCompat` direction are non-reflective, but the full matrix is not complete. |
| `ClientCompat` extraction | `TODO` | Not started. |

## Version Bands

| Version Band | Overlay Shape | Reflection Removal | Compile Evidence | Runtime Evidence | Notes |
| --- | --- | --- | --- | --- | --- |
| `26.1.x` | `PASS` | `PARTIAL` | `PASS` | `PASS` | All `26.1.x` common nodes compile after cleanup. `ItemCompat` is directly implemented in overlays. `PlayerCompat` and `WorldCompat` still need reflection scans. |
| `1.21.x` | `PASS` | `WIP` | `PASS` | `PASS` | All `1.21.x` common nodes compile after splitting `ResourceKey.location()` versus `ResourceKey.identifier()` at `1.21.11`, and restoring `Ingredient#getItems()` helpers for `1.21`/`1.21.1`. |
| `1.20.5` to `1.20.6` | `PASS` | `TODO` | `PASS` | `PASS` | All nodes in this band compile. Data-component era but not identical to `1.21.x`; use Beam before copying modern implementations. Biome precipitation uses `hasPrecipitation()`. |
| `1.20` to `1.20.4` | `PASS` | `TODO` | `PASS` | `PASS` | All nodes in this band compile after resolving `Biome.hasPrecipitation()`. Legacy networking skips must remain intentional. |
| `1.19.x` | `PASS` | `TODO` | `PASS` | `PASS` | All `1.19.x` common nodes compile after resolving the precipitation boundary at `1.19.4`. Shared baseline with `1.18.x`; expect many of the same compat constraints. |
| `1.18.x` | `PASS` | `TODO` | `PASS` | `PASS` | All `1.18.x` common nodes compile. Shared baseline with `1.19.x`; avoid rediscovering solved TeaKit/creative-tab/Forge command issues. |
| `1.17.x` | `PASS` | `TODO` | `PASS` | `PASS` | All `1.17.x` common nodes compile. Forge `1.17.1` has documented conformance gaps from userdev/mixin limitations. |
| `1.16.5` | `PASS` | `TODO` | `PASS` | `PASS` | Common compile is green. Final Forge legacy node. Reference this heavily before touching older Fabric-only lines. |
| `1.16` to `1.16.4` | `PASS` | `TODO` | `PASS` | `PASS` | All nodes in this band compile. `DeferredRegister.entryKey` now casts through `ResourceKey<Registry<T>>` to support the pre-`1.16.2` `ResourceKey.create` signature. |
| `1.15.x` to `1.14.4` | `PASS` | `TODO` | `PASS` | `PASS` | All nodes in this band compile. Old Fabric-only support; keep changes minimal and evidence-driven. |

## Compat Classes

| Compat Area | Purpose | Current Status | Next Action |
| --- | --- | --- | --- |
| `ItemCompat` | Item stack, inventory, ingredient, attribute, enchantment, tag, and default-instance differences. | `PARTIAL` | Finish non-reflective implementations band by band, newest to oldest. |
| `PlayerCompat` | Inventory, abilities, game mode, food, ender chest, packets, selected slot, XP, sleep, and player state differences. | `WIP` | Remove guard residue first, then scan for reflection or stringly API calls. |
| `WorldCompat` | Time, vectors, sounds, difficulty, biomes, dimensions, entity queries, AABB, and direction differences. | `WIP` | Resolve API differences with Beam, especially `ResourceKey` and biome/direction changes. |
| `ClientCompat` | Client-only UI/render/input API differences. | `TODO` | Extract after the common compat overlays are cleaner, so the pattern is stable. |

## Source Shape Checklist

| Rule | Status | Notes |
| --- | --- | --- |
| Version overlays contain resolved Java only | `PASS` | `rg` found no Stonecutter guard residue or inactive block comments in `versions/*/.../compat`; every common node compiles after cleanup. |
| No reflection for Minecraft or loader APIs | `PARTIAL` | Hard ban. Replace with overlays or small typed adapters. |
| Common/root code remains readable | `PARTIAL` | Guards are acceptable in root common code when readable, but large Minecraft API divergence belongs in compat overlays. |
| No JSON mixin overlay churn unless needed | `PASS` | Keep mixin config as `.json`; use Stonecutter support without breaking existing config expectations. |
| No conformance or TeaKit paths in Amber production | `PASS` | Amber runtime checks remain external through `amber-conformance` and TeaKit. |

## Verification Checklist

Use sequential checks. Do not fan out the matrix on this machine.

| Check | When Required | Status |
| --- | --- | --- |
| `rg` scan for Stonecutter residue in version compat overlays | After overlay cleanup edits | `PASS` |
| `rg` scan for reflection in compat overlays | After each reflection-removal slice | `WIP` |
| Representative common compiles per touched band | After source edits | `PASS` |
| Full touched-node compile | Before committing a band | `PASS` |
| Amber conformance runtime for touched runtime behavior | Before claiming runtime parity | `TODO` |
| `git diff --check` | Before commit | `PASS` |

## Next Suggested Slice

Finish the interrupted overlay cleanup as its own small slice:

1. Review the current local cleanup diff and keep only resolved-overlay cleanup that is mechanically correct.
2. Use Beam for any Minecraft symbol disagreement instead of guessing from generated comments.
3. Verify `1.21.11`, one mid-band `1.21.x`, `26.1.2`, and one older representative node sequentially.
4. Commit the cleanup separately from deeper reflection removal.

After that, continue reflection removal with `ItemCompat` newest-to-oldest, then move to `PlayerCompat` / `WorldCompat`, and only then extract `ClientCompat`.
