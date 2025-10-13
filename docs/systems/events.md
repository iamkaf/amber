# Event System

Amber provides a comprehensive event system that works consistently across Fabric, Forge, and NeoForge. The events are designed to be simple to use while providing powerful hooks into Minecraft's game loop.

## Overview

The event system includes:

- **Player Events**: Player interactions, joining/leaving, respawning
- **Block Events**: Block breaking, placing, interaction
- **Item Events**: Item dropping, picking up
- **Entity Events**: Entity spawning, interaction
- **Farming Events**: Crop growth, bone meal usage
- **Command Events**: Command execution
- **Loot Events**: Loot table modification
- **Client Events**: Rendering, input, HUD (client-side only)

## Player Events

Player events provide hooks for player-related actions and state changes.

### ENTITY_INTERACT

Fired when a player interacts with (right-clicks) an entity.

```java
PlayerEvents.ENTITY_INTERACT.register((player, level, hand, entity) -> {
    // Check if player is holding a special item
    ItemStack stack = player.getItemInHand(hand);
    if (stack.is(MyItems.MAGIC_WAND.get())) {
        // Cast a spell on the entity
        if (entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200));
        }
        return InteractionResult.SUCCESS; // Cancel default interaction
    }
    return InteractionResult.PASS; // Allow default interaction
});
```

### PLAYER_JOIN

Fired when a player joins the server.

```java
PlayerEvents.PLAYER_JOIN.register((player) -> {
    // Send welcome message
    player.sendSystemMessage(Component.literal("Welcome to the server!"));
    
    // Give new players a starter kit
    if (!player.getInventory().hasAnyOf(Set.of(Items.OAK_LOG))) {
        player.getInventory().add(new ItemStack(Items.OAK_LOG, 16));
        player.getInventory().add(new ItemStack(Items.APPLE, 4));
    }
    
    // Log join event
    Constants.LOG.info("{} joined the server", player.getName().getString());
});
```

### PLAYER_LEAVE

Fired when a player leaves the server.

```java
PlayerEvents.PLAYER_LEAVE.register((player) -> {
    // Save player-specific data
    PlayerData.save(player.getUUID());
    
    // Log leave event
    Constants.LOG.info("{} left the server", player.getName().getString());
});
```

### PLAYER_RESPAWN

Fired after a player respawns (from death or leaving the End).

```java
PlayerEvents.PLAYER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
    if (!alive) {
        // Player died and respawned
        newPlayer.sendSystemMessage(Component.literal("Don't die again!"));
        
        // Give respawn protection
        newPlayer.addEffect(new MobEffectInstance(
            MobEffects.DAMAGE_RESISTANCE, 100, 4
        ));
    } else {
        // Player respawned from leaving the End
        newPlayer.sendSystemMessage(Component.literal("Welcome back from the End!"));
    }
});
```

## Block Events

Block events provide hooks for block-related interactions and changes.

### BLOCK_BREAK_BEFORE

Fired before a block is broken. Can be cancelled to prevent breaking.

```java
BlockEvents.BLOCK_BREAK_BEFORE.register((level, player, pos, state, blockEntity) -> {
    // Prevent breaking certain blocks in protected areas
    if (isProtectedArea(level, pos)) {
        player.sendSystemMessage(Component.literal("This area is protected!"));
        return InteractionResult.FAIL; // Cancel block breaking
    }
    
    // Check for special tool requirements
    if (state.getBlock() == MyBlocks.MAGIC_ORE.get()) {
        ItemStack tool = player.getMainHandItem();
        if (!tool.is(MyItems.MAGIC_PICKAXE.get())) {
            player.sendSystemMessage(Component.literal("You need a magic pickaxe!"));
            return InteractionResult.FAIL; // Cancel block breaking
        }
    }
    
    return InteractionResult.PASS; // Allow breaking
});
```

### BLOCK_BREAK_AFTER

Fired after a block has been broken.

```java
BlockEvents.BLOCK_BREAK_AFTER.register((level, player, pos, state, blockEntity) -> {
    // Handle special block breaking effects
    if (state.getBlock() == MyBlocks.EXPLOSIVE_ORE.get()) {
        // Create explosion after breaking
        level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 3.0F, 
            Level.ExplosionInteraction.BLOCK);
    }
    
    // Track statistics
    if (player instanceof ServerPlayer serverPlayer) {
        MyStats.incrementBlocksMined(serverPlayer, state.getBlock());
    }
    
    // Spawn particles
    level.addParticle(ParticleTypes.CRIT, 
        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
        0, 0, 0);
});
```

### BLOCK_PLACE

Fired when a player places a block. Can be cancelled.

```java
BlockEvents.BLOCK_PLACE.register((level, player, pos, state, context) -> {
    // Prevent placing blocks in protected areas
    if (isProtectedArea(level, pos)) {
        player.sendSystemMessage(Component.literal("Cannot place blocks here!"));
        return InteractionResult.FAIL;
    }
    
    // Check for special placement conditions
    if (state.getBlock() == MyBlocks.MAGIC_PLANT.get()) {
        // Only allow placing on specific blocks
        BlockState below = level.getBlockState(pos.below());
        if (!below.is(BlockTags.DIRT)) {
            player.sendSystemMessage(Component.literal("Magic plants need dirt!"));
            return InteractionResult.FAIL;
        }
    }
    
    return InteractionResult.PASS;
});
```

### BLOCK_INTERACT

Fired when a player right-clicks on a block.

```java
BlockEvents.BLOCK_INTERACT.register((player, level, hand, hitResult) -> {
    BlockPos pos = hitResult.getBlockPos();
    BlockState state = level.getBlockState(pos);
    
    // Custom block interactions
    if (state.is(MyBlocks.MAGIC_BLOCK.get())) {
        if (!level.isClientSide()) {
            // Toggle magic block state
            boolean powered = state.getValue(MagicBlockBlock.POWERED);
            level.setBlock(pos, state.setValue(MagicBlockBlock.POWERED, !powered), 3);
            
            // Play sound
            level.playSound(null, pos, SoundEvents.LEVER_CLICK, 
                SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.SUCCESS; // Cancel default interaction
    }
    
    return InteractionResult.PASS;
});
```

## Item Events

Item events provide hooks for item-related actions.

### ITEM_DROP

Fired when a player drops an item (informational only).

```java
ItemEvents.ITEM_DROP.register((player, itemEntity) -> {
    ItemStack stack = itemEntity.getItem();
    
    // Log valuable item drops
    if (stack.getItem() == Items.DIAMOND) {
        Constants.LOG.info("{} dropped {} diamonds at {}", 
            player.getName().getString(), 
            stack.getCount(),
            itemEntity.blockPosition());
    }
    
    // Apply special effects to certain items
    if (stack.is(MyItems.CURSED_ITEM.get())) {
        // Apply curse to player
        player.addEffect(new MobEffectInstance(
            MobEffects.UNLUCK, 6000, 0));
    }
});
```

### ITEM_PICKUP

Fired when a player picks up an item (informational only).

```java
ItemEvents.ITEM_PICKUP.register((player, itemEntity, itemStack) -> {
    // Track item collection achievements
    if (itemStack.getItem() == Items.DIAMOND) {
        PlayerProgress.trackDiamondsCollected(player, itemStack.getCount());
    }
    
    // Special pickup effects
    if (itemStack.is(MyItems.MAGIC_ORB.get())) {
        // Restore mana/energy
        if (player instanceof ServerPlayer serverPlayer) {
            PlayerMana.restore(serverPlayer, 50);
            player.sendSystemMessage(Component.literal("Mana restored!"));
        }
    }
});
```

## Entity Events

Entity events provide hooks for entity-related actions.

### ENTITY_SPAWN

Fired when an entity spawns in the world.

```java
EntityEvents.ENTITY_SPAWN.register((entity, level, x, y, z) -> {
    // Modify spawning conditions
    if (entity instanceof Zombie zombie) {
        // Give zombies random armor in hard mode
        if (level.getDifficulty() == Difficulty.HARD && level.random.nextFloat() < 0.3F) {
            zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
        }
    }
    
    // Track entity spawning for analytics
    if (level instanceof ServerLevel serverLevel) {
        SpawnTracker.recordSpawn(serverLevel, entity.getType(), new Vec3(x, y, z));
    }
});
```

## Farming Events

Farming events provide hooks for agriculture-related actions.

### BONE_MEAL_USE

Fired when bone meal is used on a block.

```java
FarmingEvents.BONE_MEAL_USE.register((level, pos, state, player, stack) -> {
    // Custom bone meal effects
    if (state.is(MyBlocks.MAGIC_CROP.get())) {
        if (!level.isClientSide()) {
            // Magic crops grow instantly with bone meal
            ((BonemealableBlock) state.getBlock()).performBonemeal(
                (ServerLevel) level, level.random, pos, state);
            
            // Consume extra bone meal for magic effect
            if (player != null && !player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            
            // Create particle effect
            for (int i = 0; i < 10; i++) {
                level.addParticle(ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + level.random.nextDouble(),
                    pos.getY() + level.random.nextDouble(),
                    pos.getZ() + level.random.nextDouble(),
                    0, 0, 0);
            }
        }
        return InteractionResult.SUCCESS; // Cancel default bone meal behavior
    }
    
    return InteractionResult.PASS;
});
```

## Client Events

Client events only fire on the client side and are useful for rendering and input handling.

### HUD_RENDER

Fired when the HUD is being rendered.

```java
// This would typically be registered through a client-specific entry point
HudEvents.HUD_RENDER.register((graphics, partialTick) -> {
    Minecraft minecraft = Minecraft.getInstance();
    
    // Render custom HUD element
    if (shouldShowHUD()) {
        graphics.drawString(minecraft.font, 
            "Energy: " + getCurrentEnergy(),
            10, 10, 0xFFFFFF);
    }
});
```

### KEY_PRESS

Fired when a key is pressed.

```java
InputEvents.KEY_PRESS.register((keyCode, scanCode, action, modifiers) -> {
    // Handle custom keybinds
    if (keyCode == MyKeybinds.TOGGLE_FEATURE.getKey().getValue() && action == 1) {
        // Toggle feature
        toggleFeature();
        return true; // Cancel further processing
    }
    
    return false;
});
```

## Event Best Practices

### 1. Return Values

Most events return an `InteractionResult`:

- `PASS` - Allow default behavior to continue
- `SUCCESS` - Cancel default behavior, indicate success
- `FAIL` - Cancel default behavior, indicate failure
- `CONSUME` - Cancel default behavior, without specific result

### 2. Side Checks

Always check which side you're on before accessing side-specific code:

```java
PlayerEvents.ENTITY_INTERACT.register((player, level, hand, entity) -> {
    // Server-only logic
    if (!level.isClientSide()) {
        // Only run on server
        processInteraction(player, entity);
    }
    
    // Client-only effects
    if (level.isClientSide()) {
        // Only run on client
        spawnParticles(entity);
    }
    
    return InteractionResult.PASS;
});
```

### 3. Performance Considerations

- Keep event handlers fast, especially for frequently fired events
- Avoid expensive operations in client events
- Use caching for expensive calculations

### 4. Event Registration

Register events during mod initialization:

```java
public class MyMod {
    public static void init() {
        AmberInitializer.initialize(MOD_ID);
        
        // Register events
        registerEvents();
        
        // Other initialization
    }
    
    private static void registerEvents() {
        // Register all event handlers
        PlayerEvents.ENTITY_INTERACT.register(MyEventHandlers::handleEntityInteract);
        BlockEvents.BLOCK_BREAK_AFTER.register(MyEventHandlers::handleBlockBreak);
        ItemEvents.ITEM_PICKUP.register(MyEventHandlers::handleItemPickup);
    }
}
```

## Event Reference

### Player Events

| Event | When Fired | Cancellable | Parameters |
|-------|------------|--------------|------------|
| `ENTITY_INTERACT` | Player right-clicks entity | Yes | `(Player, Level, InteractionHand, Entity)` |
| `PLAYER_JOIN` | Player joins server | No | `(ServerPlayer)` |
| `PLAYER_LEAVE` | Player leaves server | No | `(ServerPlayer)` |
| `PLAYER_RESPAWN` | Player respawns | No | `(ServerPlayer, ServerPlayer, boolean)` |

### Block Events

| Event | When Fired | Cancellable | Parameters |
|-------|------------|--------------|------------|
| `BLOCK_BREAK_BEFORE` | Before block broken | Yes | `(Level, Player, BlockPos, BlockState, BlockEntity)` |
| `BLOCK_BREAK_AFTER` | After block broken | No | `(Level, Player, BlockPos, BlockState, BlockEntity)` |
| `BLOCK_PLACE` | Block placed | Yes | `(Level, Player, BlockPos, BlockState, ItemStack)` |
| `BLOCK_INTERACT` | Block right-clicked | Yes | `(Player, Level, InteractionHand, BlockHitResult)` |

### Item Events

| Event | When Fired | Cancellable | Parameters |
|-------|------------|--------------|------------|
| `ITEM_DROP` | Item dropped | No | `(Player, ItemEntity)` |
| `ITEM_PICKUP` | Item picked up | No | `(Player, ItemEntity, ItemStack)` |

The event system provides a clean, consistent API across all platforms, making it easy to handle game events without worrying about platform-specific differences.