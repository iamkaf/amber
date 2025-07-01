# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

See the full changelog at https://github.com/iamkaf/amber

## Unreleased

### Added

- Added support for Forge.
- Added a DeferredRegister system to allow for registering items, blocks, and entities in a more structured way.
- Added a new AmberInitializer class to init mods.
- Added a new JSON config system.
- Added events to register commands.

### Changed

- TBD

### Deprecated

- AmberMod is now deprecated in favor of AmberInitializer.
- All unversioned API helper classes have been deprecated in preparation for versioned packages.
- JsonFileReader is now deprecated in favor of a new JSON system.

### Removed

- TBD

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