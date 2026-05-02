# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

See the full changelog at https://github.com/iamkaf/amber

## 11.0.0

### Changed

- Ported the mod to Stonecutter

### Fixed

- Fixed Fabric `ItemEvents.ITEM_PICKUP` firing repeatedly before vanilla completed the pickup
- Fixed Fabric `EntityEvent.SHEAR` not firing for player-sheared sheep on modern Minecraft versions
- Fixed Fabric `EntityEvent.SHEAR` reporting an empty drops list for player-sheared sheep on modern Minecraft versions
- Fixed Fabric `PlayerEvents.CRAFT_ITEM` missing crafted-item callback paths such as normal result-slot pickup and smithing table upgrades
- Fixed Fabric `PlayerEvents.SHIELD_BLOCK` not firing for fully blocked shield damage
- Fixed Fabric `FarmingEvents.BONEMEAL_USE` firing once on the client and once on the server for a single use
- Fixed Fabric `FarmingEvents.FARMLAND_TRAMPLE` not firing or supporting cancellation on modern Minecraft versions
- Fixed Fabric `LootEvents.MODIFY` not forwarding loot table callbacks on 1.20.5 and 1.20.6
- Fixed Fabric `NetworkChannel.sendToAllPlayers` not having an active server for `PlayerLookup.all`
- Fixed Fabric custom creative tabs registered by consumer mods not receiving Amber creative tab content callbacks on 26.1, 1.21.x, 1.20.6, and 1.20.5
- Fixed Fabric custom creative tab registration crashing on 1.19.2 by using Fabric API's legacy item group builder instead of vanilla's fixed tab array
- Fixed Fabric 1.19.2 vanilla creative tab content callbacks by bridging legacy `CreativeModeTab#fillItemList`
- Fixed Fabric 1.19.2 custom creative tabs not invoking Amber content callbacks when their item lists are built
- Fixed Forge 1.19.2 compiling against the 1.19.3-only `CreativeModeTabEvent` bridge
- Fixed Forge 1.19.2 vanilla creative tab content callbacks by bridging legacy `CreativeModeTab#fillItemList`
- Fixed legacy custom creative tabs not invoking Amber content callbacks when their item lists are built on pre-1.19.3 loaders
- Fixed Forge 1.18.x custom creative tab creation crashing from direct legacy tab array and identifier method links
- Fixed Forge 1.18.x deferred registry supplier metadata crashing from direct resource-key location links
- Fixed Forge 1.18.x command and client-command registration crashing from direct legacy registry-access field links
- Fixed Forge 1.18.x world lifecycle events crashing from direct legacy server accessor method links
- Fixed Forge 1.18.x entity damage events crashing from direct legacy level side field links
- Fixed Forge 1.18.x `HudEvents.RENDER_HUD` not firing by bridging legacy overlay render events
- Fixed Forge 1.18.x `RenderEvents.BLOCK_OUTLINE_RENDER` crashing from direct client renderer field links
- Fixed Forge 1.18 `PlayerEvents.SHIELD_BLOCK` not firing by falling back to the legacy blocked-damage path when Forge has no shield-block event
- Fixed Forge 1.18.x `ClientFunctions.shouldRenderHud` crashing from direct client singleton and options links
- Fixed Forge 1.18.x `ClientFunctions.renderText` and legacy tooltip helpers crashing from direct client rendering and item-stack links
- Fixed Forge 1.18.x `ItemFunctions` inventory helpers crashing from direct inventory method links
- Fixed old Fabric `ClientCommandEvents.EVENT` registrations becoming invisible to consumer callbacks that register after Amber setup
- Fixed pre-1.18.2 function helpers directly linking `Holder` APIs that do not exist on older Minecraft versions
- Fixed Forge 1.19 event guards to use the patched 41.1.x `level` event package and key mapping APIs
- Fixed Fabric legacy sheep shearing mixin imports on 1.18.x
- Fixed `PlayerFunctions` last-death-location helpers on pre-1.19 Minecraft versions
- Fixed NeoForge `NetworkChannel.sendToAllPlayers` not broadcasting clientbound packets
- Fixed NeoForge `BlockEvents.BLOCK_BREAK_BEFORE` firing twice from client and server break events
- Fixed NeoForge `FarmingEvents.FARMLAND_TRAMPLE` not firing on modern Minecraft versions
- Fixed NeoForge `EntityEvent.SHEAR` not firing for player-sheared sheep on modern and legacy 1.21 Minecraft versions
- Fixed NeoForge `PlayerEvents.CRAFT_ITEM` missing crafted-item callback paths such as normal result-slot pickup and smithing table upgrades
- Fixed Forge `EntityEvent.SHEAR` not firing for player-sheared sheep on modern Minecraft versions
- Fixed Forge `RenderEvents.BLOCK_OUTLINE_RENDER` not firing because the client render mixin was not loaded
- Fixed Forge `PlayerEvents.CRAFT_ITEM` missing crafted-item callback paths such as normal result-slot pickup and smithing table upgrades
- Fixed Forge `ItemEvents.MODIFY_DEFAULT_COMPONENTS` missing late consumer registrations after default item component caches were initialized
- Fixed Forge 1.19.3 custom creative tab content bridging against the current 44.1.x `CreativeModeTabEvent.BuildContents` API
- Fixed legacy Forge keybind registration racing late consumer registrations after Forge's key mapping event had already fired
- Fixed Forge 1.17.1 `InputEvents.MOUSE_SCROLL_PRE` and `InputEvents.MOUSE_SCROLL_POST` by bridging the native legacy mouse scroll event
- Fixed Forge 1.17.1 `RenderEvents.BLOCK_OUTLINE_RENDER` by bridging the native legacy block selection render event
- Fixed Forge 1.17.1 ordinary `PlayerEvents.CRAFT_ITEM` callbacks by bridging Forge's native item-crafted event
- Fixed `CompoundEventResult` being an empty public API shell by adding pass and interrupt result contracts

## 10.0.2

### Fixed

- Fixed creative tab registration namespace on 26.1

## 10.0.1

### Fixed

- Fixed crash on Fabric's dedicated server startup on 26.1

## 10.0.0

### Removed

- Removed 14 deprecated classes

### Added

- Ported Amber to Minecraft **26.1** on Fabric and NeoForge
- Kept the shared HUD and block outline rendering hooks working on the `26.1` client rendering stack

### Changed

- Amber `26.1` now targets Java **25**
- Forge support for `26.1` is temporarily disabled while the Forge toolchain work is still in progress

## 9.0.5

### Fixed

- Fixed Forge `EntityEvent.AFTER_DAMAGE` on 1.21.11 by correcting the `LivingEntity#hurtServer` local capture used by Amber's post-damage hook
- Fixed the same Forge post-damage local capture issue in the 1.21.10 and 26.1 code lines

## 9.0.4

### Fixed

- Fixed `EntityEvent.AFTER_DAMAGE` not firing on Fabric, Forge, and NeoForge
- Fixed NeoForge dispenser shears support on 1.21.11

## 9.0.3

### Changed

- Made Fabric Loader requirement less strict

### Removed

- Removed network test commands

## 9.0.2

### Fixed

- Fixed HUD event on Forge

## 9.0.0

### Changed

- **Updated to Minecraft 1.21.11**

### Added

- Added consolidated API utility classes in `api.functions.v1` package:
  - **PlayerFunctions** - Player operations, experience, abilities, inventory, messaging
  - **ItemFunctions** - Item/inventory operations, armor management, crafting utilities
  - **WorldFunctions** - World utilities, sounds, dimension checks, distance calculations
  - **ClientFunctions** - Client-side utilities, HUD rendering, tooltips
  - **MathFunctions** - Mathematical operations, probability, random generation
- Added `ItemEvents.MODIFY_DEFAULT_COMPONENTS` - Allows mods to modify default item properties and add custom data components with cross-platform support
- Added `PlayerEvents.SHIELD_BLOCK` - Fires when players block damage with shields, providing access to shield data for combat mechanics and shield systems

### Deprecated

- Deprecated legacy utility classes in favor of consolidated `api.functions.v1` package:
  - `InventoryHelper`, `ItemHelper`, `ArmorTierHelper` → Use `ItemFunctions`
  - `LevelHelper`, `SoundHelper`, `CommonUtils`, `BoundingBoxMerger` → Use `WorldFunctions`
  - `FeedbackHelper` → Use `PlayerFunctions`
  - `CommonClientUtils`, `SmartTooltip` → Use `ClientFunctions`
  - `Chance` → Use `MathFunctions`
- Legacy utility classes will be removed in Amber 10.0
- Creative tabs moved to `registry.v1` package

### Removed

- Removed deprecated and unused classes:
  - `JsonFileReader` - Unused configuration class that was deprecated in favor of `JsonConfigManager`
  - `LiteralSetHolder` - Unused utility class that had no references in the codebase
  - `AmberMod` (deprecated v1 version) - Unused legacy initialization class, replaced by `AmberInitializer`
  - `BrewingHelper` - Never implemented brewing functionality that was planned but never developed
- Removed deprecated `SmartTooltip.into()` method in favor of the Consumer-based implementation

## 8.3.6

### Fixed

- Fixed the `LightningBoltIgnitionMixin` runtime injection on Fabric, Forge, and NeoForge

## 8.3.5

### Fixed

- Fixed Forge `EntityEvent.AFTER_DAMAGE` on 1.21.10 by correcting the `LivingEntity#hurtServer` local capture used by Amber's post-damage hook

## 8.3.4

### Fixed

- Fixed `EntityEvent.AFTER_DAMAGE` not firing on Fabric, Forge, and NeoForge
- Fixed NeoForge dispenser shears support on 1.21.10

## 8.2.0

### Added

- Added player crafting event:
  - `PlayerEvents.CRAFT_ITEM` - Fires when a player crafts an item using any crafting mechanism (informational)

## 8.1.0

### Added

- Added farming events:
  - `FarmingEvents.BONEMEAL_USE` - Fires when bonemeal is applied to blocks (cancellable)
  - `FarmingEvents.FARMLAND_TRAMPLE` - Fires when farmland is trampled by entities (cancellable)
  - `FarmingEvents.CROP_GROW` - Fires when crops attempt to grow (cancellable)
- Added animal events:
  - `AnimalEvents.ANIMAL_TAME` - Fires when animals are being tamed (cancellable)
  - `AnimalEvents.ANIMAL_BREED` - Fires when baby animals spawn from breeding (informational)


## 8.0.0

### Added

- Added debug tracking to all mixins for `/amber debug` command visibility
- Added player lifecycle events:
  - `PlayerEvents.PLAYER_JOIN` - Fires when a player joins the server
  - `PlayerEvents.PLAYER_LEAVE` - Fires when a player leaves the server
  - `PlayerEvents.PLAYER_RESPAWN` - Fires after a player respawns (from death or End portal)
- Added item events (informational only, non-cancellable):
  - `ItemEvents.ITEM_DROP` - Fires when a player drops an item (fires on both client and server)
  - `ItemEvents.ITEM_PICKUP` - Fires when a player picks up an item (fires on both client and server)

### Changed

- **Updated to Minecraft 1.21.10**

## 7.0.2

### Added

- Added comprehensive block interaction events:
  - `BlockEvents.BLOCK_BREAK_BEFORE/AFTER` - Fires before/after players break blocks (cancellable)
  - `BlockEvents.BLOCK_PLACE` - Unified block placement event (server-only, cancellable)
  - `BlockEvents.BLOCK_INTERACT` - Fires when players right-click blocks (cancellable)
  - `BlockEvents.BLOCK_CLICK` - Fires when players left-click blocks (cancellable)
- Added client-side input events:
  - `InputEvents.MOUSE_SCROLL_PRE` - Fires before mouse wheel scroll with position and delta data (cancellable)
  - `InputEvents.MOUSE_SCROLL_POST` - Fires after mouse wheel scroll with position and delta data (non-cancellable)
- Added client-side rendering events:
  - `RenderEvents.BLOCK_OUTLINE_RENDER` - Fires when block selection outlines are rendered (cancellable)
- Added Fabric Mixin support for mouse scroll events via `MouseHandlerMixin`
- Added full cross-platform event consistency across Fabric, Forge, and NeoForge
- Added EventBus 7 support for Forge 1.21.8+ with proper cancellation handling

### Changed

- **Updated to Minecraft 1.21.9** (Fabric and NeoForge)

## 6.0.10

### Added

- Added support for Forge.
- Added a DeferredRegister system to allow for registering items, blocks, and entities in a more structured way.
- Added a new AmberInitializer class to init mods.
- Added a new JSON config system.
- Added events to register commands.
- Added SimpleCommands helper to register commands easily.
- Added a KeybindHelper to register keybinds.
- Added events to render to the HUD.
- Added the `/amber doctor` command to diagnose issues with the mod.
- Added client tick events.
- Added unified networking API supporting Fabric, Forge, and NeoForge.
- Added internal networking system for connectivity testing and latency measurement.
- Added `/amber ping` command for manual network connectivity testing.
- Added `/amber reset-stats` command to reset networking statistics.
- Added networking diagnostics to `/amber doctor` command (initialization status, ping count, average latency).

### Deprecated

- AmberMod is now deprecated in favor of AmberInitializer.
- All unversioned API helper classes have been deprecated in preparation for versioned packages.
- JsonFileReader is now deprecated in favor of a new JSON system.

### Fixed

- Fixed a bug where the mod would not load properly on some platforms.
- FORGE: Fixed a bug preventing interactions with entities.

## 5.0.0

### Added

- Added support for 1.21.6

## 4.2.2

### Fixed

- NEOFORGE: Fixed a bug preventing interactions with entities.

## 4.2.1

- fix: fix a crash when using a mod that depends on the old SmartTooltip api

## 4.2.0

- feat: add PlayerEvents for player interactions

## 4.1.0

- feat: updated SmartTooltip to the new system

## 4.0.1

- feat: ported to 1.21.5

## 3.0.0

- feat: add event system
- feat: add platform helpers
- feat: add json file reader
- feat: remove architectury dependency

## 2.1.0

- add a few more inventory methods

## 2.0.0-beta.3

- port to 1.21.4

## 1.3.0-beta.2

- maintenance update
- port to 1.20.1

## 1.0.0-alpha.1

- Initial Release

## Types of changes

- `Added` for new features.
- `Changed` for changes in existing functionality.
- `Deprecated` for soon-to-be removed features.
- `Removed` for now removed features.
- `Fixed` for any bug fixes.
- `Security` in case of vulnerabilities.
