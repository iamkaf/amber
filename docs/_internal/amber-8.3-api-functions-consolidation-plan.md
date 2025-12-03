# Amber API Refactoring Plan

## Overview

This document outlines the refactoring of Amber's utility API to consolidate scattered helper classes into organized functional packages. The goal is to reduce API surface area, improve discoverability, and create a more intuitive architecture.

## Problem Statement

Currently, Amber has ~20+ utility packages containing single helper classes, leading to:
- Scattered and hard-to-discover utilities
- Inconsistent organization
- Large API surface area for similar functionality
- Cognitive overhead for users finding the right helper

## Solution: Functional Consolidation

Group related utilities by domain into versioned packages following the existing `v1` pattern.

## New Package Structure

### `api.functions.v1.ItemFunctions`

**Consolidates:**
- `api.inventory.InventoryHelper` - Inventory manipulation and consumption
- `api.inventory.ItemHelper` - Item-related utilities
- `api.item.ArmorTierHelper` - Armor tier constants and repair utilities
- `api.item.BrewingHelper` - Potion brewing utilities

**Functionality:**
- Inventory operations (consume, check, iterate)
- Item manipulation and queries
- Armor tier data (toughness, knockback resistance, enchantability)
- Brewing recipe utilities

### `api.functions.v1.ClientFunctions`

**Consolidates:**
- `api.player.FeedbackHelper` - Player feedback and messaging
- `api.common.client.CommonClientUtils` - Client-side utilities
- `api.item.SmartTooltip` - Dynamic tooltip builder with keybind detection
- `api.keymapping.KeybindHelper` - Keybinding registration

**Functionality:**
- Player messaging (chat, action bar)
- Client-specific helper methods
- Smart tooltip building with conditional components
- Keybinding registration and management

### `api.functions.v1.WorldFunctions`

**Consolidates:**
- `api.level.LevelHelper` - World/level utilities
- `api.sound.SoundHelper` - Sound utilities
- `api.common.CommonUtils` - Common utility methods
- `api.aabb.BoundingBoxMerger` - Physics and geometry utilities

**Functionality:**
- World operations (spawn items, timed execution)
- Sound playback and management
- Raytracing and common game utilities
- Bounding box calculations and merging

### `api.functions.v1.MathFunctions`

**Consolidates:**
- `api.math.Chance` - Probability and chance utilities

**Functionality:**
- Random chance calculations
- Probability distributions
- Random number utilities

**Note:** `SimpleIntegerDataComponent` will be removed entirely as it's redundant.

## Registry System Reorganization

### `api.registry.v1.creativetabs`

**Moves:**
- `api.creativetabs.CreativeModeTabRegistry` → `api.registry.v1.creativetabs.CreativeModeTabRegistry`
- `api.creativetabs.CreativeTabHelper` → `api.registry.v1.creativetabs.CreativeTabHelper`
- `api.creativetabs.TabBuilder` → `api.registry.v1.creativetabs.TabBuilder`

**Rationale:** Creative tabs are fundamentally a registry system, so they belong in the registry package.

## Migration Strategy

### Phase 1: Implementation (Current Release)
1. Create new `api.functions.v1` package classes
2. Move and adapt functionality from deprecated helpers
3. Add `@Deprecated` annotations to old classes with migration guidance
4. Update internal codebase to use new APIs
5. Update documentation

### Phase 2: Deprecation Period (Amber 8.x - 9.x)
- Old classes remain available but deprecated
- Documentation points to new APIs
- Clear migration messages in deprecation notices
- Examples updated to use new APIs

### Phase 3: Removal (Amber 10.0)
- Remove all deprecated utility classes
- Clean up empty packages
- Final documentation update

## Deprecation Template

```java
@Deprecated
@RemovalInVersion("10.0")
public class InventoryHelper {
    /**
     * @deprecated Use {@link ItemFunctions#consumeIfAvailable} instead.
     * Will be removed in Amber 10.0
     */
    @Deprecated
    public static boolean consumeIfAvailable(Inventory inventory, ItemLike item) {
        return ItemFunctions.consumeIfAvailable(inventory, item);
    }
}
```

## Benefits

### For Users
- **Improved Discoverability:** Related functions grouped logically
- **Reduced Complexity:** Fewer packages to navigate
- **Clearer API:** Functional organization is more intuitive
- **Better Documentation:** Easier to document comprehensive functionality

### For Developers
- **Cleaner Codebase:** Consolidated, organized code
- **Easier Maintenance:** Related code in one place
- **Better Testing:** Focused test suites per functional area
- **Future Growth:** Clear patterns for adding new functionality

## API Surface Reduction

**Before Refactoring:**
- ~20 scattered utility packages
- ~15 single-class packages
- Inconsistent organization
- Hard to discover related functionality

**After Refactoring:**
- 4 focused functional packages
- 1 reorganized registry subpackage
- Logical domain separation
- Easy discovery of related functionality

## Implementation Timeline

- **Amber 8.3.x**: Implement new packages and deprecate old ones
- **Amber 9.x**: Maintain backward compatibility with deprecated APIs
- **Amber 10.0**: Remove deprecated APIs and complete transition

## Backward Compatibility

- No breaking changes in Amber 8.x - all old APIs remain functional
- Clear migration path for users
- Gradual transition period allows ecosystem to adapt
- Versioned packages ensure stability going forward

## Future Considerations

This refactoring creates a solid foundation for:
- Additional functional packages as needed
- Consistent patterns for new utilities
- Better mod interop and compatibility
- Cleaner API evolution

The new structure aligns with modern API design principles and provides a much more intuitive experience for Amber mod developers.