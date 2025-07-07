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

---

## 📋 Implementation Priority

### 🔥 **Phase 1: Essential Player & Entity Events** (High Priority)

#### 💀 **Entity Lifecycle Events**
- [ ] **EntitySpawnEvent** - When entities spawn in the world
  - *Fabric:* Not directly available
  - *Forge:* `EntityJoinLevelEvent`
  - *NeoForge:* `EntityJoinLevelEvent`, `FinalizeSpawnEvent`
- [ ] **EntityDeathEvent** - When living entities die
  - *Fabric:* `ServerLivingEntityEvents.AFTER_DEATH`
  - *Forge:* `LivingDeathEvent`
  - *NeoForge:* Not directly listed but fundamental
- [ ] **EntityDamageEvent** - When entities take damage
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