# NeoForge 26.1.2 Block Break Event Note

When rebuilding Amber after reverting the Stonecutter work, preserve this compatibility fix for the `26.1.2` NeoForge line.

## What Changed

NeoForge moved the block break event type used by Amber:

- Older `26.1` and `26.1.1` NeoForge lines use `net.neoforged.neoforge.event.level.BlockEvent.BreakEvent`.
- `26.1.2` uses `net.neoforged.neoforge.event.level.block.BreakBlockEvent`.

## Required Amber Fix

For the `26.1.2` NeoForge line, `NeoForgeAmberEventSetup#onBlockBreak` must use `BreakBlockEvent` instead of `BlockEvent.BreakEvent`.

The old Stonecutter-era fix was introduced in commit:

```text
e1387dac8ab095c52ca122bb9d43b6b1753b43c3 Fix NeoForge 26.1.2 block break event change
```

The relevant code change was:

```java
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

public static void onBlockBreak(BreakBlockEvent event) {
    // existing Amber block break forwarding logic
}
```

## Verified Boundary

Do not apply this unconditionally to all `26.1.x` NeoForge versions.

A compile probe showed `BreakBlockEvent` does not exist in `26.1` or `26.1.1`; those lines still need the older `BlockEvent.BreakEvent` type.

Use the new type only for `26.1.2+` unless a later NeoForge API check proves a different boundary.
