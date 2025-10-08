# 🎯 Amber Events Implementation Roadmap

This document outlines events across **Fabric**, **Forge**, and **NeoForge** that should be implemented in Amber's unified event system.

## ✅ **Already Implemented**
These events are already available in Amber:
- ✅ **PlayerEvents.ENTITY_INTERACT** - Player right-clicking entities
- ✅ **CommandEvents.EVENT** - Server command registration
- ✅ **ClientCommandEvents.EVENT** - Client command registration  
- ✅ **ClientTickEvents** - Client tick start/end
- ✅ **HudEvents.RENDER_HUD** - HUD rendering
- ✅ **LootEvents.MODIFY** - Loot table modification
- ✅ **EntityEvent.ENTITY_SPAWN** - When entities spawn in the world
- ✅ **EntityEvent.ENTITY_DEATH** - When living entities die
- ✅ **EntityEvent.ENTITY_DAMAGE** - When entities take damage
- ✅ **BlockEvents.BLOCK_BREAK_BEFORE/AFTER** - When players break blocks (before/after variants)
- ✅ **BlockEvents.BLOCK_PLACE_BEFORE/AFTER** - When players place blocks (before/after variants) 
- ✅ **BlockEvents.BLOCK_INTERACT** - When players right-click blocks
- ✅ **BlockEvents.BLOCK_CLICK** - When players left-click blocks
- ✅ **InputEvents.MOUSE_SCROLL_PRE/POST** - When mouse wheel is scrolled (before/after variants)
- ✅ **RenderEvents.BLOCK_OUTLINE_RENDER** - When block selection outlines are rendered

---

## 📋 Implementation Priority

### 🔥 **Phase 1: Essential Player & Entity Events** (High Priority)

#### 💀 **Entity Lifecycle Events**
- [x] **EntitySpawnEvent** - When entities spawn in the world ✅ **IMPLEMENTED**
  - *Fabric:* Not directly available (skipped for now)
  - *Forge:* `EntityJoinLevelEvent`
  - *NeoForge:* `EntityJoinLevelEvent`, `FinalizeSpawnEvent`
- [x] **EntityDeathEvent** - When living entities die ✅ **IMPLEMENTED**
  - *Fabric:* `ServerLivingEntityEvents.AFTER_DEATH`
  - *Forge:* `LivingDeathEvent`
  - *NeoForge:* `LivingDeathEvent`
- [x] **EntityDamageEvent** - When entities take damage ✅ **IMPLEMENTED**
  - *Fabric:* `ServerLivingEntityEvents.ALLOW_DAMAGE`, `AFTER_DAMAGE`
  - *Forge:* `LivingDamageEvent`, `LivingAttackEvent`
  - *NeoForge:* `LivingIncomingDamageEvent`

#### 🎮 **Player Interaction Events**
- [x] **PlayerInteractBlockEvent** - When players interact with blocks ✅ **IMPLEMENTED**
  - *Fabric:* `UseBlockCallback.EVENT`
  - *Forge:* `PlayerInteractEvent.RightClickBlock`
  - *NeoForge:* `PlayerInteractEvent.RightClickBlock`
- [ ] **PlayerUseItemEvent** - When players use items
  - *Fabric:* `UseItemCallback.EVENT`
  - *Forge:* `ItemUseEvent`, `RightClickItem`
  - *NeoForge:* `RightClickItem`, `UseItemOnBlockEvent`
- [x] **PlayerAttackEvent** - When players attack entities/blocks ✅ **IMPLEMENTED**
  - *Fabric:* `AttackEntityCallback.EVENT`, `AttackBlockCallback.EVENT`
  - *Forge:* `PlayerInteractEvent.LeftClickBlock`
  - *NeoForge:* `PlayerInteractEvent.LeftClickBlock`

#### 🧱 **World/Block Events**
- [x] **BlockBreakEvent** - When players break blocks ✅ **IMPLEMENTED**
  - *Fabric:* `PlayerBlockBreakEvents.BEFORE` / `AFTER`
  - *Forge:* `BlockEvent.BreakEvent`
  - *NeoForge:* `BlockEvent.BreakEvent`
- [x] **BlockPlaceEvent** - When players place blocks ✅ **IMPLEMENTED**
  - *Fabric:* Not directly available (skipped for now)
  - *Forge:* `BlockEvent.EntityPlaceEvent`
  - *NeoForge:* `BlockEvent.EntityPlaceEvent`

---

### ⚡ **Phase 2: Gameplay Mechanics** (Medium Priority)

#### 👥 **Player Lifecycle Events**
- [x] **PlayerJoinEvent** - When players join the server ✅ **IMPLEMENTED**
  - *Fabric:* `ServerPlayConnectionEvents.JOIN`
  - *Forge:* `PlayerEvent.PlayerLoggedInEvent`
  - *NeoForge:* `PlayerEvent.PlayerLoggedInEvent`
- [x] **PlayerLeaveEvent** - When players leave the server ✅ **IMPLEMENTED**
  - *Fabric:* `ServerPlayConnectionEvents.DISCONNECT`
  - *Forge:* `PlayerEvent.PlayerLoggedOutEvent`
  - *NeoForge:* `PlayerEvent.PlayerLoggedOutEvent`
- [x] **PlayerRespawnEvent** - When players respawn ✅ **IMPLEMENTED**
  - *Fabric:* `ServerPlayerEvents.AFTER_RESPAWN`
  - *Forge:* `PlayerEvent.PlayerRespawnEvent`
  - *NeoForge:* `PlayerEvent.PlayerRespawnEvent`

#### ⚔️ **Combat & PvP Events**
- [ ] **LivingKillEvent** - When one entity kills another
  - *Fabric:* `ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY`
  - *Forge:* Not directly listed
  - *NeoForge:* Not directly listed
- [ ] **ArrowLooseEvent** - When arrows are fired from bows
  - *Fabric:* Not directly listed
  - *Forge:* Not directly listed
  - *NeoForge:* `ArrowLooseEvent`, `ArrowNockEvent`
- [ ] **SweepAttackEvent** - During sweep attacks
  - *Fabric:* Not directly listed
  - *Forge:* Not directly listed
  - *NeoForge:* `SweepAttackEvent`

#### 📦 **Inventory & Item Events**
- [x] **ItemDropEvent** - When items are dropped/tossed ✅ **IMPLEMENTED**
  - *Fabric:* Mixin on `Player.drop()` (no native event)
  - *Forge:* `ItemTossEvent`
  - *NeoForge:* `ItemTossEvent`
- [x] **ItemPickupEvent** - When items are picked up ✅ **IMPLEMENTED**
  - *Fabric:* Mixin on `ItemEntity.playerTouch()` (no native event)
  - *Forge:* `EntityItemPickupEvent`
  - *NeoForge:* `ItemEntityPickupEvent.Pre`
- [ ] **ItemConsumeEvent** - When items are consumed
  - *Fabric:* Not directly listed
  - *Forge:* Not directly listed
  - *NeoForge:* `ItemConsumption`
- [ ] **ItemCraftEvent** - When items are crafted
  - *Fabric:* Not directly listed in summary
  - *Forge:* Not directly listed
  - *NeoForge:* Not directly listed
- [ ] **AnvilRepairEvent** - When items are repaired in anvils
  - *Fabric:* Not directly listed
  - *Forge:* `AnvilRepairEvent`, `AnvilUpdateEvent`
  - *NeoForge:* Not directly listed

---

### 🔧 **Phase 3: Advanced Gameplay** (Lower Priority)

#### 🌱 **Farming & Agriculture Events**
- [ ] **BonemealEvent** - When bonemeal is applied
  - *Fabric:* Not directly listed
  - *Forge:* `BonemealEvent`
  - *NeoForge:* `BonemealEvent`
- [ ] **FarmlandTrampleEvent** - When farmland is trampled
  - *Fabric:* Not directly listed
  - *Forge:* Not directly listed
  - *NeoForge:* `FarmlandTrampleEvent`
- [ ] **CropGrowEvent** - When crops grow
  - *Fabric:* Not directly listed
  - *Forge:* Not directly listed
  - *NeoForge:* `BlockGrowFeatureEvent`

#### 🐄 **Animal & Mob Events**
- [ ] **AnimalTameEvent** - When animals are tamed
  - *Fabric:* Not directly listed
  - *Forge:* `AnimalTameEvent`
  - *NeoForge:* Not directly listed
- [ ] **AnimalBreedEvent** - When animals breed
  - *Fabric:* Not directly listed
  - *Forge:* `BabyEntitySpawnEvent`
  - *NeoForge:* Not directly listed
- [ ] **VillagerTradeEvent** - Villager trade generation
  - *Fabric:* Not directly listed
  - *Forge:* Not directly listed
  - *NeoForge:* `VillagerTradesEvent`

#### 🛌 **Sleep & Time Events**
- [ ] **PlayerSleepEvent** - Sleep mechanics
  - *Fabric:* `EntitySleepEvents.START_SLEEPING`, `STOP_SLEEPING`
  - *Forge:* Not directly listed
  - *NeoForge:* Not directly listed

#### 🌍 **World & Environment Events**
- [ ] **ChunkLoadEvent** - When chunks are loaded
  - *Fabric:* `ServerChunkEvents.CHUNK_LOAD`
  - *Forge:* `ChunkEvent`
  - *NeoForge:* Not directly listed
- [ ] **ChunkUnloadEvent** - When chunks are unloaded
  - *Fabric:* `ServerChunkEvents.CHUNK_UNLOAD`
  - *Forge:* `ChunkEvent`
  - *NeoForge:* Not directly listed
- [ ] **ExplosionEvent** - When explosions occur
  - *Fabric:* Not directly listed
  - *Forge:* `ExplosionEvent`
  - *NeoForge:* Not directly listed

#### 🎮 **Player State Events**
- [ ] **GameModeChangeEvent** - When player's game mode changes
  - *Fabric:* Not directly listed
  - *Forge:* Not directly listed
  - *NeoForge:* `PlayerChangeGameModeEvent`
- [ ] **PlayerSpawnSetEvent** - When player's spawn point is set
  - *Fabric:* Not directly listed
  - *Forge:* Not directly listed
  - *NeoForge:* `PlayerSetSpawnEvent`
- [ ] **ExperienceEvent** - Experience point changes
  - *Fabric:* Not directly listed
  - *Forge:* `PlayerXpEvent`
  - *NeoForge:* Not directly listed

#### ✨ **Enchantment & Magic Events**
- [ ] **EnchantmentEvent** - Enchantment application
  - *Fabric:* `EnchantmentEvents.ALLOW_ENCHANTING`
  - *Forge:* Not directly listed
  - *NeoForge:* Not directly listed

#### 🧪 **Potion & Brewing Events**
- [ ] **PotionEffectEvent** - Potion effect application/removal
  - *Fabric:* Not directly listed
  - *Forge:* Not directly listed
  - *NeoForge:* Not directly listed

#### 🌐 **Teleportation & Dimension Events**
- [ ] **EntityTeleportEvent** - When entities teleport
  - *Fabric:* Not directly listed
  - *Forge:* Not directly listed
  - *NeoForge:* `EntityTeleportEvent`
- [ ] **DimensionChangeEvent** - When players change dimensions
  - *Fabric:* Not directly listed
  - *Forge:* Not directly listed
  - *NeoForge:* Not directly listed

---

## 🏗️ **Implementation Notes**

### **Design Principles:**
1. **Unified API** - One event interface that works across all platforms
2. **Fabric-inspired** - Use Fabric's callback-style pattern as the base
3. **Platform Abstraction** - Hide platform differences from mod developers
4. **Cancellable Events** - Support event cancellation where appropriate
5. **Performance** - Minimal overhead when events aren't being used

## ⚠️ **CRITICAL: Cross-Platform Behavior Consistency**

**🚨 MANDATORY REQUIREMENT: Event behavior MUST be identical across all 3 loaders (Fabric, Forge, NeoForge).**

**Inconsistencies in event behavior between loaders will be the death of Amber.** Mod developers must be able to trust that their code will work identically regardless of which loader they're running on.

### **Examples of Required Consistency:**

#### **Event Cancellation & Return Values**
Events must handle cancellation and return values identically:
```java
// ✅ GOOD: Forge implementation that matches Fabric behavior
if (result.equals(InteractionResult.PASS)) {
    return false; // Don't cancel, let other handlers run
}

// Complex platform-specific logic ensures Forge behaves like Fabric
if (side.isClient()) {
    if (result == SUCCESS) {
        event.setCancellationResult(SUCCESS);
        return true; // Cancel with SUCCESS
    } else if (result == CONSUME) {
        event.setCancellationResult(CONSUME);
        return true; // Cancel with CONSUME
    } else {
        return true; // Cancel with FAIL
    }
}
```

#### **Event Timing**
Events must fire at the exact same game phases:
- **BEFORE** events fire before the action happens
- **AFTER** events fire after the action completes
- **Cancellable** events must allow prevention of the action

#### **Event Data**
Event parameters must contain identical information:
- Same entity references
- Same world/level references  
- Same damage values, interaction results, etc.
- Same side (client/server) behavior

### **Multiloader Event Implementation Process:**

When implementing a new event in Amber's multiloader system, follow this process:

#### **1. Event Analysis Phase**
- **Check Platform Availability**: Verify if equivalent events exist in all 3 loaders (Fabric, Forge, NeoForge)
- **Document Platform Events**: Record the exact event names and signatures for each platform
- **Identify Gaps**: Note which platforms are missing the event

#### **2. Implementation Strategy**
- **✅ All Platforms Have Event**: Create a loader-agnostic callback interface and register platform-specific handlers
- **❌ Missing Platform Event**: Investigate source code of other platforms to understand implementation, then:
  - Use Mixins to hook into the appropriate game methods
  - Create custom event triggers that match the other platforms' behavior
  - **🚨 CRITICAL**: Ensure 100% consistent behavior across all platforms

#### **2.1 Behavior Consistency Requirements**
- **Event Timing**: Events must fire at identical game phases across all loaders
- **Cancellation Logic**: Event cancellation must work identically (see `onPlayerEntityInteract` in ForgeAmberEventSetup.java for complex example)
- **Return Values**: Same return value handling and semantics
- **Side Effects**: Identical side effects (client/server behavior, world modifications, etc.)
- **Parameter Data**: Exact same data passed to event callbacks
- **Error Handling**: Consistent error states and exception handling

#### **3. Code Structure**
1. **Common Event Interface**: Define in `common/src/main/java/com/iamkaf/amber/api/event/v1/events/common/`
2. **Platform Implementations**: 
   - `fabric/src/main/java/com/iamkaf/amber/platform/FabricAmberEventSetup.java`
   - `forge/src/main/java/com/iamkaf/amber/platform/ForgeAmberEventSetup.java`
   - `neoforge/src/main/java/com/iamkaf/amber/platform/NeoForgeAmberEventSetup.java`
3. **Event Registration**: Use platform-specific event buses and registration patterns

#### **4. Testing & Validation**
- **Compilation Test**: Run `./gradlew compileJava` to verify code compiles across all platforms
- **🚨 CRITICAL - Behavior Consistency Testing**: 
  - **Event Timing**: Verify events fire at identical game phases across all loaders
  - **Cancellation Behavior**: Test event cancellation works identically on all platforms
  - **Return Value Handling**: Ensure return values produce same effects across loaders
  - **Parameter Consistency**: Verify identical data is passed to callbacks
  - **Side Effect Validation**: Confirm identical world/entity state changes
  - **Client/Server Parity**: Test both client and server-side behavior matches
- **Performance**: Verify minimal overhead when events are not used
- **Cross-Platform Integration Testing**: Run identical test scenarios on all 3 loaders to verify behavior

#### **5. Documentation**
- **Update Event Lists**: Mark events as implemented in this document
- **API Documentation**: Add Javadoc comments explaining event behavior and parameters
- **Usage Examples**: Provide code examples for common use cases

### **Event Categories:**
- **Player Events** - Player lifecycle, interactions, and state changes
- **Entity Events** - Entity spawning, death, damage, and behavior
- **World Events** - Block changes, chunk loading, explosions, environment
- **Item Events** - Item usage, drops, crafting, and modifications
- **Combat Events** - PvP, damage, weapons, and combat mechanics
- **Farming Events** - Agriculture, crops, animals, and breeding
- **Client Events** - Rendering, input, HUD, and client-side logic
- **Magic Events** - Enchantments, potions, and magical mechanics

### **Implementation Strategy:**
1. Start with **Phase 1** events as they're most critical for basic modding
2. Create base event interfaces and platform implementations
3. Add comprehensive tests for cross-platform compatibility
4. Document usage patterns and migration guides
5. Implement remaining phases based on community feedback and usage analytics

---

## 🚀 **Phase 4: Amber-Exclusive Smart Events** (Revolutionary Priority)

These are cutting-edge events that would be unique to Amber, providing capabilities not available in any vanilla mod loader. They represent Amber's evolution from a compatibility layer to an intelligent modding platform.

### 🧠 **Smart Player Intent Detection Events**
- [ ] **SmartPlayerEvents.BUILDING_DETECTED** - AI-powered detection of sustained building activity
  - *Implementation:* Pattern analysis of block placement frequency, area coverage, and material consistency
  - *Triggers:* When player places >20 blocks in <2 minutes within a 32x32 area
  - *Use Cases:* Auto-enabling build mode, providing building assistance, territory detection
- [ ] **SmartPlayerEvents.MINING_SESSION_START** - Detection of focused mining behavior
  - *Implementation:* Analysis of block breaking patterns, tool usage, and movement in underground areas
  - *Triggers:* Continuous block breaking of stone/ore materials with mining tools for >30 seconds
  - *Use Cases:* Mining assistance modes, resource tracking, cave-in warnings
- [ ] **SmartPlayerEvents.PVP_PREPARATION** - Combat readiness detection
  - *Implementation:* Pattern recognition of armor equipping, weapon selection, potion consumption
  - *Triggers:* Player equips combat gear + consumes buffs + approaches other players
  - *Use Cases:* PvP zone notifications, combat mode switching, tournament systems
- [ ] **SmartPlayerEvents.EXPLORATION_MODE** - Long-distance travel detection
  - *Implementation:* Movement pattern analysis for sustained travel without building/mining
  - *Triggers:* Player moves >500 blocks without stopping to build/mine for >2 minutes
  - *Use Cases:* Journey assistance, landmark notifications, exploration rewards
- [ ] **SmartPlayerEvents.AFK_DETECTED** - Intelligent idle detection
  - *Implementation:* Advanced idle detection beyond simple movement (considers inventory management, looking around)
  - *Triggers:* No meaningful actions (beyond basic movement/looking) for configurable duration
  - *Use Cases:* AFK systems, resource protection, server management

### 🎮 **Gameplay Flow Context Events**
- [ ] **GameplayFlowEvents.BOSS_FIGHT_START** - Epic encounter detection
  - *Implementation:* Analysis of high-damage entities + player tension indicators (health loss, potion use)
  - *Triggers:* Player engages entity with >100 HP that deals >10 damage per hit
  - *Use Cases:* Dynamic music, UI changes, challenge notifications, recording highlights
- [ ] **GameplayFlowEvents.BASE_BUILDING_MODE** - Sustained construction activity
  - *Implementation:* Extended building pattern detection with infrastructure recognition
  - *Triggers:* Large-scale building activity (>100 blocks) with defensive/functional patterns
  - *Use Cases:* Base protection systems, building assistance, territory establishment
- [ ] **GameplayFlowEvents.RESOURCE_GATHERING** - Systematic collection detection
  - *Implementation:* Pattern recognition for repetitive farming/mining in productive areas
  - *Triggers:* Repetitive harvesting/mining actions in resource-rich areas
  - *Use Cases:* Automation suggestions, efficiency tracking, resource management
- [ ] **GameplayFlowEvents.SOCIAL_GATHERING** - Multi-player convergence
  - *Implementation:* Analysis of multiple players converging in close proximity with social indicators
  - *Triggers:* 3+ players within 16 blocks, low movement, possible chat activity
  - *Use Cases:* Event detection, social features, meeting assistance
- [ ] **GameplayFlowEvents.RAID_PREPARATION** - Group coordination detection
  - *Implementation:* Multi-player gear coordination and strategic positioning analysis
  - *Triggers:* Multiple players equipping combat gear, grouping, moving toward objectives
  - *Use Cases:* Raid coordination tools, team management, strategic assistance

### 🔧 **Smart Event Implementation Architecture**

#### **Pattern Recognition Engine**
- **Event Aggregation**: Combine multiple base events (block placement, movement, inventory changes)
- **Temporal Analysis**: Track patterns over time windows (30 seconds to 10 minutes)
- **Spatial Analysis**: Consider player location, biome, and nearby structures
- **Context Awareness**: Factor in time of day, weather, nearby players
- **Machine Learning**: Improve detection accuracy over time based on outcomes

#### **Configuration System**
```java
SmartEventConfig.builder()
    .sensitivity(0.7f)              // How easily events trigger
    .minimumConfidence(0.8f)        // Confidence threshold for triggering
    .temporalWindow(Duration.ofMinutes(2))  // How long to analyze patterns
    .spatialRadius(32)              // Area to consider for analysis
    .enableLearning(true)           // Whether to improve detection over time
    .build();
```

#### **Performance Considerations**
- **Lightweight Analysis**: Use efficient algorithms that don't impact game performance
- **Configurable Frequency**: Allow servers to adjust analysis frequency based on performance needs
- **Lazy Evaluation**: Only analyze patterns when base events suggest relevant activity
- **Memory Efficient**: Use circular buffers and bounded storage for pattern data

#### **Privacy & Ethics**
- **Opt-in System**: Players must explicitly enable smart detection
- **Transparency**: Clear notifications when smart events are detected
- **No Personal Data**: Only analyze gameplay patterns, never personal information
- **Local Processing**: Pattern analysis happens locally, no data transmitted

---

## 📚 **Developer References**

For developers implementing these events, here are essential documentation links:

### **Platform Event Documentation:**
- **[Fabric Events Documentation](https://github.com/iamkaf/modresources/blob/main/docs/fabric-events-summary.md)** - Comprehensive Fabric API event reference
- **[Forge Events Documentation](https://github.com/iamkaf/modresources/blob/main/docs/forge-events-summary.md)** - Comprehensive Forge event reference
- **[NeoForge Events Documentation](https://github.com/iamkaf/modresources/blob/main/docs/neoforge-events-summary.md)** - Comprehensive NeoForge event reference

### **Core System Documentation:**
- **[Forge Event Bus 7.0](https://gist.github.com/PaintNinja/ad82c224aecee25efac1ea3e2cf19b91)** - Official Forge Event Bus 7 documentation
- **[EventBus GitHub Repository](https://github.com/MinecraftForge/EventBus)** - Source code and examples
- **[Forge Community Wiki - Events](https://forge.gemwire.uk/wiki/Events)** - Community-maintained event guides

### **Porting & Migration Guides:**
- **[NeoForge Porting Primers](https://github.com/neoforged/.github/tree/main/primers)** - Official porting guides for different versions
- **[Forge to NeoForge Migration](https://github.com/neoforged/.github/blob/main/primers/README.md)** - Migration strategies and best practices

### **Additional Resources:**
- **[Fabric API Documentation](https://fabricmc.net/wiki/tutorial:events)** - Official Fabric event tutorial
- **[Minecraft Forge Documentation](https://docs.minecraftforge.net/)** - Complete Forge modding documentation
- **[NeoForge Documentation](https://docs.neoforged.net/)** - Official NeoForge documentation

---

*Last Updated: 2025-07-25*  
*Event Data Sources: [iamkaf/modresources](https://github.com/iamkaf/modresources/tree/main/docs)*

## 📝 **Recent Implementation History**

### **2025-01-07 - Entity Lifecycle Events**
- ✅ **EntityEvent.ENTITY_SPAWN** - Implemented using `EntityJoinLevelEvent` (Forge/NeoForge), skipped Fabric (no direct equivalent)
- ✅ **EntityEvent.ENTITY_DEATH** - Implemented using `ServerLivingEntityEvents.AFTER_DEATH` (Fabric), `LivingDeathEvent` (Forge/NeoForge)  
- ✅ **EntityEvent.ENTITY_DAMAGE** - Implemented using `ServerLivingEntityEvents.ALLOW_DAMAGE` (Fabric), `LivingAttackEvent` (Forge), `LivingIncomingDamageEvent` (NeoForge)

**Technical Notes:**
- Fabric EntitySpawn event was skipped due to lack of direct equivalent - would require Mixin implementation
- All platforms use consistent callback interfaces: `EntitySpawn`, `EntityDeath`, `EntityDamage`
- Events follow Amber's established patterns with platform-specific registration in `*AmberEventSetup` classes
- **⚠️ BEHAVIOR CONSISTENCY**: Entity events have simple void callbacks, making cross-platform consistency easier to maintain

**Cross-Platform Behavior Validation:**
- ✅ **EntityEvent.ENTITY_DEATH**: Consistent timing and parameters across Fabric/Forge/NeoForge
- ✅ **EntityEvent.ENTITY_DAMAGE**: Consistent damage values and source information 
- ⚠️ **EntityEvent.ENTITY_SPAWN**: Forge/NeoForge only - Fabric implementation needed for full consistency

### **2025-07-25 - Block Events & Client Input Events**
- ✅ **BlockEvents.BLOCK_BREAK_BEFORE/AFTER** - Implemented using `PlayerBlockBreakEvents.BEFORE/AFTER` (Fabric), `BlockEvent.BreakEvent` (Forge/NeoForge)
- ✅ **BlockEvents.BLOCK_PLACE_BEFORE/AFTER** - Implemented using `BlockEvent.EntityPlaceEvent` (Forge/NeoForge), Fabric support planned
- ✅ **BlockEvents.BLOCK_INTERACT** - Implemented using `UseBlockCallback.EVENT` (Fabric), `PlayerInteractEvent.RightClickBlock` (Forge/NeoForge)
- ✅ **BlockEvents.BLOCK_CLICK** - Implemented using `AttackBlockCallback.EVENT` (Fabric), `PlayerInteractEvent.LeftClickBlock` (Forge/NeoForge)
- ✅ **InputEvents.MOUSE_SCROLL_PRE/POST** - Implemented using `MouseHandlerMixin` (Fabric), `ScreenEvent.MouseScrolled.Pre/Post` (Forge/NeoForge)
- ✅ **RenderEvents.BLOCK_OUTLINE_RENDER** - Implemented using `WorldRenderEvents.BLOCK_OUTLINE` (Fabric), `RenderHighlightEvent.Block` (Forge/NeoForge)

**Technical Notes:**
- All block events support both BEFORE (cancellable) and AFTER (informational) variants where applicable
- Mouse scroll event uses Mixin on Fabric for global coverage, native events on Forge/NeoForge
- Block outline rendering allows cancellation and custom rendering on all platforms
- Events follow EventBus 7 patterns on Forge while maintaining traditional @SubscribeEvent patterns on NeoForge
- **⚠️ BEHAVIOR CONSISTENCY**: All events use unified `InteractionResult` return values for consistent cancellation behavior

**Cross-Platform Behavior Validation:**
- ✅ **Block Events**: Consistent timing (before/after), cancellation, and parameter data across all platforms
- ✅ **Mouse Scroll**: Consistent scroll delta values and mouse coordinates on all platforms
- ✅ **Block Outline**: Consistent cancellation behavior and rendering context across all platforms

### **2025-01-XX - Item Events**
- ✅ **ItemEvents.ITEM_DROP** - Implemented using `ItemTossEvent` (Forge/NeoForge), `PlayerMixin` on `Player.drop()` (Fabric)
- ✅ **ItemEvents.ITEM_PICKUP** - Implemented using `EntityItemPickupEvent` (Forge), `ItemEntityPickupEvent.Pre` (NeoForge), `ItemEntityMixin` on `ItemEntity.playerTouch()` (Fabric)

**Technical Notes:**
- Both Forge and NeoForge have native events for item drop/pickup
- Fabric lacks native events (see https://github.com/FabricMC/fabric/issues/1130) - implemented via Mixins
- Fabric Mixins: `PlayerMixin` (drops) and `ItemEntityMixin` (pickup)
- All events use consistent callback interfaces: `ItemDrop`, `ItemPickup`
- Events follow Amber's established patterns with platform-specific registration in `*AmberEventSetup` classes
- **⚠️ BEHAVIOR CONSISTENCY**: NeoForge uses `TriState.FALSE` for cancellation, Forge uses boolean return, Fabric Mixin uses `CallbackInfo.cancel()`

**Cross-Platform Behavior Validation:**
- ✅ **ItemEvents.ITEM_DROP**: Consistent cancellation behavior across all platforms (Forge/NeoForge/Fabric)
- ✅ **ItemEvents.ITEM_PICKUP**: Consistent parameters (player, itemEntity, itemStack) across all platforms
- ✅ **Fabric Implementation**: Fully implemented via Mixins targeting `Player.drop()` and `ItemEntity.playerTouch()`
- ✅ **Server-Side Only**: All implementations fire only on server side for consistency