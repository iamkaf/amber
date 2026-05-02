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
- no untyped `Object` shims are used for Minecraft or loader API values when a typed version overlay can express the real type
- no stringly method/field dispatch such as `invokePlayer(player, "method")`, `intField(target, "field")`, or `aabbCoordinate(box, "minX")`
- representative nodes in that band compile sequentially
- runtime conformance remains green when the touched behavior affects runtime paths

## Current Position

| Area | Status | Evidence |
| --- | --- | --- |
| Stonecutter convention upgrade | `PASS` | Committed in `multiloader-conventions` as `db980f0 Upgrade Stonecutter conventions`. |
| Legacy compat extracted to overlays | `PASS` | Committed in Amber as `a17cec2 Move Amber legacy compat to overlays`. |
| Versioned `ItemCompat` / `PlayerCompat` / `WorldCompat` overlays added | `PASS` | Committed in Amber as `5753898 Add versioned compat overlays`; representative common compile checks passed before commit. |
| Overlay guard/comment cleanup | `PASS` | Local uncommitted cleanup removed Stonecutter guard residue from version compat overlays. Residue scan is clean and every common node compiles sequentially. |
| Compat package promotion | `PASS` | Compat classes live in `com.iamkaf.amber.compat`, not under `util`, because they define Amber's version boundary rather than miscellaneous helpers. |
| Reflection and dynamic-dispatch removal | `PASS` | Minecraft/loader gameplay and platform paths are clean in source scan. Remaining reflection is confined to the event system internals, which are the explicit exception for advanced callback dispatch. |
| `ClientCompat` extraction | `PASS` | `ClientFunctions` delegates Minecraft client calls to typed `ClientCompat` overlays. Representative API split nodes compiled sequentially. |
| Modern Fabric creative tab display generator | `PASS` | `TabBuilder` no longer uses reflection/proxy dispatch to configure modern Fabric tab contents. Modern creative-tab registration now builds through the loader service, and Fabric access wideners expose `CreativeModeTab.Output` and `TabVisibility` only on `1.20+` nodes. Focused `26.1.2-fabric` creative-tab conformance passed. |
| Forge creative tabs and platform/mixin helpers | `PASS` | Legacy Forge creative-tab mixins live in version overlays. Forge loot-table mutation, item component cache invalidation, renderer access, registrar keys, world lifecycle helpers, item pickup helpers, and shield-block fallbacks use typed calls or mixin accessors. All Forge compile/resource nodes passed sequentially; focused `26.1.2-forge` creative-tab conformance passed. |

## Version Bands

| Version Band | Overlay Shape | Reflection Removal | Compile Evidence | Runtime Evidence | Notes |
| --- | --- | --- | --- | --- | --- |
| `26.1.x` | `PASS` | `PASS` | `PASS` | `PASS` | `ItemCompat`, `PlayerCompat`, `WorldCompat`, `ClientCompat`, modern Fabric creative tabs, and Forge platform/mixin paths are non-reflective outside event internals. All Forge `26.1.x` nodes compile and process resources; focused `26.1.2` Fabric/Forge creative-tab conformance passed. |
| `1.21.x` | `PASS` | `PASS` | `PASS` | `PASS` | Common compat classes and Forge platform/mixin paths are non-reflective outside event internals. All Forge `1.21.x` nodes compile and process resources sequentially. |
| `1.20.5` to `1.20.6` | `PASS` | `PASS` | `PASS` | `PASS` | Common compat classes and Forge platform/mixin paths are non-reflective outside event internals. Forge `1.20.6` compiles and processes resources sequentially; Fabric `1.20.5`/`1.20.6` creative-tab work was previously verified. |
| `1.20` to `1.20.4` | `PASS` | `PASS` | `PASS` | `PASS` | Common compat classes and Forge platform/mixin paths are non-reflective outside event internals. All Forge `1.20` to `1.20.4` nodes compile and process resources sequentially. |
| `1.19.x` | `PASS` | `PASS` | `PASS` | `PASS` | Common compat classes and Forge platform/mixin paths are non-reflective outside event internals. All Forge `1.19.x` nodes compile and process resources sequentially. |
| `1.18.x` | `PASS` | `PASS` | `PASS` | `PASS` | Common compat classes and Forge platform/mixin paths are non-reflective outside event internals. All Forge `1.18.x` nodes compile and process resources sequentially. |
| `1.17.x` | `PASS` | `PASS` | `PASS` | `PASS` | Common compat classes and Forge platform/mixin paths are non-reflective outside event internals. Forge `1.17.1` compiles and processes resources sequentially; documented conformance gaps remain separate from this source-shape work. |
| `1.16.5` | `PASS` | `PASS` | `PASS` | `PASS` | Common compat classes and Forge platform/mixin paths are non-reflective outside event internals. Final Forge legacy node compiles and processes resources sequentially. |
| `1.16` to `1.16.4` | `PASS` | `PASS` | `PASS` | `PASS` | Common compat classes are non-reflective and free of compat `Object` shims/string dispatch. Fabric-only band; previous full compile coverage remains intact. |
| `1.15.x` to `1.14.4` | `PASS` | `PASS` | `PASS` | `PASS` | Common compat classes are non-reflective and free of compat `Object` shims/string dispatch. Fabric-only band; previous full compile coverage remains intact. |

## Compat Classes

| Compat Area | Purpose | Current Status | Next Action |
| --- | --- | --- | --- |
| `ItemCompat` | Item stack, inventory, ingredient, attribute, enchantment, tag, and default-instance differences. | `PASS` | Clean across root common source and version overlays. Data-component modifier identity is typed through `modifierIdentity`. |
| `PlayerCompat` | Inventory, abilities, game mode, food, ender chest, packets, selected slot, XP, sleep, and player state differences. | `PASS` | Clean across version overlays. Replaced packet `Object`, field-by-string helpers, and method-by-string helpers with named typed methods. |
| `WorldCompat` | Time, vectors, sounds, difficulty, biomes, dimensions, entity queries, AABB, and direction differences. | `PASS` | Clean across version overlays. Replaced AABB string selectors plus `Vec3i`/direction `Object` shims with typed helpers. |
| `ClientCompat` | Client-only UI/render/input API differences. | `PASS` | Clean across root common source and version overlays. Replaced reflective Minecraft lookup, stack emptiness, HUD option/screen access, text drawing, and tooltip rendering with typed per-version calls. |

## Source Shape Checklist

| Rule | Status | Notes |
| --- | --- | --- |
| Version overlays contain resolved Java only | `PASS` | `rg` found no Stonecutter guard residue or inactive block comments in `versions/*/.../compat`; every common node compiles after cleanup. |
| No reflection for Minecraft or loader APIs | `PASS` | Hard ban outside the event system. Source scan now reports only the allowed event internals in `EventFactoryImpl`, `ArrayBackedEvent`, and `EventPhaseData`. |
| No untyped `Object` Minecraft API shims | `PARTIAL` | Compat overlays are clean. Remaining accepted guarded public shims include legacy `ClientFunctions` 1.14 graphics-context placeholders and legacy creative-tab `row(Object)`/`type(Object)` signatures. Continue with other production surfaces. |
| No stringly method or field dispatch | `PASS` | `ItemCompat`/`PlayerCompat`/`WorldCompat`/`ClientCompat`, creative tabs, Forge platform/mixin helpers, and registrar code are clean. |
| Common/root code remains readable | `PARTIAL` | Guards are acceptable in root common code when readable, but large Minecraft API divergence belongs in compat overlays. |
| No JSON mixin overlay churn unless needed | `PASS` | Keep mixin config as `.json`; use Stonecutter support without breaking existing config expectations. |
| No conformance or TeaKit paths in Amber production | `PASS` | Amber runtime checks remain external through `amber-conformance` and TeaKit. |

## Verification Checklist

Use sequential checks. Do not fan out the matrix on this machine.

| Check | When Required | Status |
| --- | --- | --- |
| `rg` scan for Stonecutter residue in version compat overlays | After overlay cleanup edits | `PASS` |
| `rg` scan for reflection in compat overlays | After each reflection-removal slice | `PASS` |
| `rg` scan for `Object` compat shims and stringly dispatch | After each dynamic-dispatch cleanup slice | `PASS` |
| Representative common compiles per touched band | After source edits | `PASS` |
| Full touched-node compile | Before committing a band | `PASS` |
| Amber conformance runtime for touched runtime behavior | Before claiming runtime parity | `PASS` |
| `git diff --check` | Before commit | `PASS` |

## Next Suggested Slice

Continue with narrower maintainability slices only when new runtime parity gaps require them. Current known Minecraft/loader reflection cleanup is complete outside the event system exception.
