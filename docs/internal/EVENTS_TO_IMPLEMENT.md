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
- [ ] **PlayerInteractBlockEvent** - When players interact with blocks
  - *Fabric:* `UseBlockCallback.EVENT`
  - *Forge:* `PlayerInteractEvent.RightClickBlock`
  - *NeoForge:* `RightClickBlock`
- [ ] **PlayerUseItemEvent** - When players use items
  - *Fabric:* `UseItemCallback.EVENT`
  - *Forge:* `ItemUseEvent`, `RightClickItem`
  - *NeoForge:* `RightClickItem`, `UseItemOnBlockEvent`
- [ ] **PlayerAttackEvent** - When players attack entities/blocks
  - *Fabric:* `AttackEntityCallback.EVENT`, `AttackBlockCallback.EVENT`
  - *Forge:* `AttackEntityEvent`
  - *NeoForge:* `AttackEntityEvent`

#### 🧱 **World/Block Events**
- [ ] **BlockBreakEvent** - When players break blocks
  - *Fabric:* `PlayerBlockBreakEvents.BEFORE` / `AFTER`
  - *Forge:* `BlockEvent.Break`
  - *NeoForge:* `BreakEvent`
- [ ] **BlockPlaceEvent** - When players place blocks
  - *Fabric:* Not directly listed
  - *Forge:* `BlockEvent.Place`
  - *NeoForge:* Not directly listed

---

### ⚡ **Phase 2: Gameplay Mechanics** (Medium Priority)

#### 👥 **Player Lifecycle Events**
- [ ] **PlayerJoinEvent** - When players join the server
  - *Fabric:* `ServerPlayerEvents.JOIN`
  - *Forge:* `PlayerJoinEvent`
  - *NeoForge:* Not directly listed
- [ ] **PlayerLeaveEvent** - When players leave the server
  - *Fabric:* `ServerPlayerEvents.LEAVE`
  - *Forge:* `PlayerLeaveEvent` (implied)
  - *NeoForge:* Not directly listed
- [ ] **PlayerRespawnEvent** - When players respawn
  - *Fabric:* `ServerPlayerEvents.AFTER_RESPAWN`
  - *Forge:* `PlayerEvent.PlayerRespawnEvent`
  - *NeoForge:* Not directly listed

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
- [ ] **ItemDropEvent** - When items are dropped/tossed
  - *Fabric:* Not directly listed
  - *Forge:* `ItemTossEvent`
  - *NeoForge:* `ItemTossEvent`
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

*Last Updated: 2025-01-07*  
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