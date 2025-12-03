# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

See the full changelog at https://github.com/iamkaf/amber

## 8.3.0

### Added

- Added item component modification system:
  - `ItemEvents.MODIFY_DEFAULT_COMPONENTS` - Allows mods to modify the default properties and behaviors of items when they are created
  - Enables adding custom data components to existing items (e.g., making normally non-repairable items repairable with specific materials)
  - Cross-platform support for Fabric, NeoForge, and Forge
  - Useful for mods that need to modify vanilla item behaviors without replacing the items
- Added player shield block event:
  - `PlayerEvents.SHIELD_BLOCK` - Fires when a player successfully blocks damage with a shield
  - Provides access to the player, shield ItemStack, blocked damage amount, and damage source
  - Cross-platform support for Fabric, NeoForge, and Forge
  - Useful for shield leveling systems, shield durability management, and combat mechanics

### Removed

- Removed deprecated and unused classes:
  - `JsonFileReader` - Unused configuration class that was deprecated in favor of `JsonConfigManager`
  - `LiteralSetHolder` - Unused utility class that had no references in the codebase
  - `AmberMod` (deprecated v1 version) - Unused legacy initialization class, replaced by `AmberInitializer`
- Removed deprecated `SmartTooltip.into()` method in favor of the Consumer-based implementation

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