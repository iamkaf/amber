# Event System

Amber provides a comprehensive, cross-platform event system that allows you to respond to game events in a unified way across Fabric, Forge, and NeoForge. The system is inspired by Fabric's event architecture but works consistently across all platforms.

## Overview

The event system allows you to:
- Listen to game events (player interactions, world changes, etc.)
- Register callbacks that execute when events occur
- Control event flow with return values
- Handle events with proper timing and thread safety

### 🎯 Cross-Platform Consistency

**Critical**: Amber's event system guarantees **identical behavior** across all platforms (Fabric, Forge, NeoForge). Events fire at the same times, with the same data, and handle cancellation identically. This allows you to write event handlers once and trust they'll work the same everywhere.

- ✅ **Same timing** - Events fire at identical game phases
- ✅ **Same data** - Identical parameters across platforms
- ✅ **Same cancellation** - `InteractionResult` works consistently
- ✅ **Same performance** - Minimal overhead on all platforms

## Event Registration

### Basic Event Handling

Register event listeners during mod initialization:

```java
import com.iamkaf.amber.api.event.v1.events.common.*;
import com.iamkaf.amber.api.event.v1.events.common.client.*;
import com.iamkaf.amber.api.platform.v1.Platform;
import net.minecraft.world.InteractionResult;

public class MyEventHandlers {
    public static void init() {
        // Player interaction events
        PlayerEvents.ENTITY_INTERACT.register((player, level, hand, entity) -> {
            if (player.getItemInHand(hand).is(MyItems.MAGIC_WAND.get())) {
                castSpell(player, entity);
                return InteractionResult.SUCCESS; // Cancel default interaction
            }
            return InteractionResult.PASS; // Allow default interaction
        });
        
        // Command registration events  
        CommandEvents.EVENT.register((dispatcher, context, selection) -> {
            MyCommands.register(dispatcher);
        });
        
        // Entity lifecycle events
        EntityEvent.ENTITY_DEATH.register((entity, source) -> {
            // Handle entity death
            System.out.println("Entity died: " + entity.getType());
        });
        
        // Client-side events
        if (Platform.isClient()) {
            ClientTickEvents.END_CLIENT_TICK.register(() -> {
                handleClientTick();
            });
        }
    }
    
    private static void castSpell(Player player, Entity target) {
        // Spell casting logic
        System.out.println("Casting spell on " + target.getName().getString());
    }
    
    private static void handleClientTick() {
        // Client tick logic
    }
}
```

### Event Return Values

Events use `InteractionResult` return values to control game behavior:

```java
public class InteractionHandlers {
    public static void init() {
        // Entity interactions use InteractionResult
        PlayerEvents.ENTITY_INTERACT.register((player, level, hand, entity) -> {
            if (entity instanceof Cow && player.getItemInHand(hand).is(Items.DIAMOND)) {
                // Custom cow interaction
                return InteractionResult.SUCCESS; // Cancels default interaction
            }
            return InteractionResult.PASS; // Allows default interaction
        });
        
        // Entity damage events support cancellation
        EntityEvent.ENTITY_DAMAGE.register((entity, source, amount) -> {
            if (entity.hasCustomName() && entity.getCustomName().getString().equals("Protected")) {
                return InteractionResult.FAIL; // Cancel damage completely
            }
            if (amount > 10.0f) {
                // Custom logic for high damage
                return InteractionResult.CONSUME; // Cancel with custom handling
            }
            return InteractionResult.PASS; // Allow damage normally
        });
    }
}
```

#### InteractionResult Values:
- **`PASS`** - Allow the action to proceed normally (default behavior)
- **`SUCCESS`** - Cancel the action but indicate success
- **`CONSUME`** - Cancel the action with consumption  
- **`FAIL`** - Cancel the action indicating failure

⚠️ **Important**: Return values work **identically** across Fabric, Forge, and NeoForge - this consistency is critical for cross-platform compatibility.

## Available Events

### Player Events

Handle player-related actions:

```java
public class PlayerEventHandlers {
    public static void init() {
        // Entity interactions
        PlayerEvents.ENTITY_INTERACT.register((player, level, hand, entity) -> {
            // Called when player interacts with entities
            return InteractionResult.PASS;
        });
    }
}
```

### Entity Events

Handle entity lifecycle and behavior:

```java
public class EntityEventHandlers {
    public static void init() {
        // Entity spawning
        EntityEvent.ENTITY_SPAWN.register((entity, level) -> {
            // Called when entities spawn in the world
            System.out.println("Entity spawned: " + entity.getType());
        });
        
        // Entity death
        EntityEvent.ENTITY_DEATH.register((entity, source) -> {
            // Called when living entities die
            System.out.println("Entity died: " + entity.getType());
        });
        
        // Entity damage (cancellable)
        EntityEvent.ENTITY_DAMAGE.register((entity, source, amount) -> {
            // Called before entities take damage
            if (entity.hasCustomName() && entity.getCustomName().getString().equals("Protected")) {
                return InteractionResult.FAIL; // Cancel damage
            }
            return InteractionResult.PASS; // Allow damage
        });
        
        // Entity damage (after damage is applied)
        EntityEvent.AFTER_DAMAGE.register((entity, source, baseDamage, actualDamage, blocked) -> {
            // Called after damage is applied but entity didn't die
            System.out.println("Entity took " + actualDamage + " damage");
        });
    }
}
```

### Command Events

Register commands across platforms:

```java
public class CommandEventHandlers {
    public static void init() {
        CommandEvents.EVENT.register((dispatcher, context, selection) -> {
            // Register your commands here
            dispatcher.register(Commands.literal("mycommand")
                .executes(ctx -> {
                    ctx.getSource().sendSuccess(
                        () -> Component.literal("Command executed!"), 
                        false
                    );
                    return 1;
                }));
        });
    }
}
```

### Client Events

Handle client-side events:

```java
public class ClientEventHandlers {
    public static void init() {
        // Only register on client side
        if (Platform.isClient()) {
            
            // Client tick events
            ClientTickEvents.START_CLIENT_TICK.register(() -> {
                // Called at the start of each client tick
                handleTickStart();
            });
            
            ClientTickEvents.END_CLIENT_TICK.register(() -> {
                // Called at the end of each client tick
                handleTickEnd();
            });
            
            // HUD rendering
            HudEvents.RENDER_HUD.register((graphics, partialTick) -> {
                renderCustomHUD(graphics, partialTick);
            });
        }
    }
    
    private static void handleTickStart() {
        // Called at the start of each client tick
    }
    
    private static void handleTickEnd() {
        // Check for key presses, update client state, etc.
        Minecraft client = Minecraft.getInstance();
        if (MyKeybinds.MAGIC_KEY.isDown()) {
            // Handle key press
        }
    }
    
    private static void renderCustomHUD(GuiGraphics graphics, float partialTick) {
        // Render custom HUD elements
        if (shouldShowMagicHUD()) {
            graphics.drawString(
                Minecraft.getInstance().font,
                "Mana: " + getCurrentMana(),
                10, 10,
                0x00FF00
            );
        }
    }
    
    private static boolean shouldShowMagicHUD() {
        // Example logic - show HUD when player has magic items
        return true;
    }
    
    private static int getCurrentMana() {
        // Example mana value
        return 100;
    }
}
```

## Advanced Event Handling

### Conditional Event Registration

Register events conditionally based on configuration or platform:

```java
public class ConditionalEventHandlers {
    public static void init() {
        // Only register if feature is enabled
        if (MyModConfig.get().enableMagicSystem) {
            PlayerEvents.ENTITY_INTERACT.register(MagicHandlers::handleMagicInteraction);
        }
        
        // Platform-specific events
        if (Platform.isClient()) {
            ClientTickEvents.END_CLIENT_TICK.register(ClientHandlers::handleTick);
        }
        
        // Development-only events
        if (Platform.isDevelopmentEnvironment()) {
            CommandEvents.EVENT.register(DebugCommands::register);
        }
    }
}
```

### Event Data Access

Access comprehensive event data:

```java
public class DetailedEventHandlers {
    public static void init() {
        PlayerEvents.ENTITY_INTERACT.register((player, level, hand, entity) -> {
            // Access player data
            String playerName = player.getName().getString();
            UUID playerId = player.getUUID();
            
            // Access item data
            ItemStack heldItem = player.getItemInHand(hand);
            boolean isMainHand = hand == InteractionHand.MAIN_HAND;
            
            // Access entity data
            EntityType<?> entityType = entity.getType();
            Vec3 entityPos = entity.position();
            
            // Access world context
            boolean isClientSide = level.isClientSide;
            
            System.out.println(String.format(
                "Player %s (%s) interacted with %s at %s using %s in %s hand",
                playerName, playerId, entityType, entityPos, 
                heldItem.getDisplayName().getString(),
                isMainHand ? "main" : "off"
            ));
            
            return InteractionResult.PASS;
        });
    }
}
```

## Custom Events

Create your own events for mod-to-mod communication:

```java
// Define your event interface
@FunctionalInterface
public interface MagicSpellCastEvent {
    /**
     * Called when a magic spell is cast.
     * @param caster The entity casting the spell
     * @param spell The spell being cast
     * @param target The target of the spell (can be null)
     * @return true to cancel the spell, false to allow
     */
    boolean onSpellCast(LivingEntity caster, MagicSpell spell, @Nullable Entity target);
}

// Create the event instance
public class MyEvents {
    public static final Event<MagicSpellCastEvent> MAGIC_SPELL_CAST = 
        EventFactory.createArrayBacked(MagicSpellCastEvent.class, 
            (listeners) -> (caster, spell, target) -> {
                for (MagicSpellCastEvent listener : listeners) {
                    if (listener.onSpellCast(caster, spell, target)) {
                        return true; // Cancel if any listener returns true
                    }
                }
                return false;
            });
}

// Register listeners for your custom event
public class CustomEventHandlers {
    public static void init() {
        MyEvents.MAGIC_SPELL_CAST.register((caster, spell, target) -> {
            // Handle spell casting
            System.out.println("Spell cast: " + spell.getName());
            return false; // Don't cancel
        });
    }
}

// Fire your custom event
public class MagicSystem {
    public static boolean castSpell(LivingEntity caster, MagicSpell spell, @Nullable Entity target) {
        // Fire the event
        boolean cancelled = MyEvents.MAGIC_SPELL_CAST.invoker().onSpellCast(caster, spell, target);
        
        if (!cancelled) {
            // Execute the spell
            spell.execute(caster, target);
            return true;
        }
        
        return false; // Spell was cancelled
    }
}
```

## Performance Considerations

### Efficient Event Handlers

Write efficient event handlers:

```java
public class EfficientEventHandlers {
    // Cache frequently accessed objects
    private static final Set<Item> MAGIC_ITEMS = Set.of(
        MyItems.MAGIC_WAND.get(),
        MyItems.SPELL_BOOK.get(),
        MyItems.CRYSTAL_ORB.get()
    );
    
    public static void init() {
        PlayerEvents.ENTITY_INTERACT.register((player, level, hand, entity) -> {
            ItemStack heldItem = player.getItemInHand(hand);
            
            // Fast check using cached set
            if (!MAGIC_ITEMS.contains(heldItem.getItem())) {
                return InteractionResult.PASS; // Early exit for non-magic items
            }
            
            // Only do expensive operations for relevant items
            return handleMagicInteraction(player, level, hand, entity, heldItem);
        });
    }
    
    // Avoid repeated expensive operations
    private static final Map<EntityType<?>, MagicEffect> ENTITY_EFFECTS = Map.of(
        EntityType.COW, MagicEffect.MILK_BOOST,
        EntityType.SHEEP, MagicEffect.WOOL_GROWTH,
        EntityType.CHICKEN, MagicEffect.EGG_PRODUCTION
    );
    
    private static InteractionResult handleMagicInteraction(Player player, Level level,
                                                 InteractionHand hand, Entity entity, ItemStack item) {
        // Use cached lookup instead of complex logic
        MagicEffect effect = ENTITY_EFFECTS.get(entity.getType());
        if (effect != null) {
            effect.apply(entity, player);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
```

### Conditional Registration

Only register events when needed:

```java
public class ConditionalRegistration {
    public static void init() {
        // Only register expensive handlers when the feature is enabled
        if (MyModConfig.get().enableAdvancedMagic) {
            registerAdvancedMagicEvents();
        }
        
        // Only register client events on the client
        if (Platform.isClient()) {
            registerClientEvents();
        }
    }
    
    private static void registerAdvancedMagicEvents() {
        // Expensive magic system events
        PlayerEvents.ENTITY_INTERACT.register(AdvancedMagicHandlers::handleInteraction);
    }
    
    private static void registerClientEvents() {
        // Client-only events
        ClientEvents.CLIENT_TICK_END.register(ClientMagicHandlers::handleTick);
    }
}
```

## Debugging Events

Debug event handling issues:

```java
public class EventDebugging {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventDebugging.class);
    
    public static void init() {
        if (MyModConfig.get().enableEventDebugging) {
            registerDebugHandlers();
        }
    }
    
    private static void registerDebugHandlers() {
        // Log all player interactions
        PlayerEvents.INTERACT_ENTITY.register(EventPriority.HIGHEST, (player, entity, hand) -> {
            LOGGER.debug("Player {} interacted with {} using {} hand", 
                        player.getName().getString(), 
                        entity.getType(), 
                        hand);
            return false; // Don't interfere with other handlers
        });
        
        // Track event performance
        PlayerEvents.INTERACT_ENTITY.register(EventPriority.LOWEST, (player, entity, hand) -> {
            long startTime = System.nanoTime();
            // Event handling occurs here
            long duration = System.nanoTime() - startTime;
            
            if (duration > 1_000_000) { // More than 1ms
                LOGGER.warn("Slow event handling: {}ns for {}", duration, entity.getType());
            }
            
            return false;
        });
    }
}
```

## Platform-Specific Notes

### Fabric
- Uses Fabric's event system internally
- Events fire on appropriate sides automatically
- Full compatibility with Fabric event phases

### Forge/NeoForge  
- Translates to Forge's event bus system
- Proper event cancellation handling
- Compatible with Forge's event priorities

The event system provides a unified interface while handling platform differences transparently.

## Complete Event Reference

### Currently Available Events

#### Player Events (`PlayerEvents`)
- **`ENTITY_INTERACT`** - When players interact with entities
  - Parameters: `Player player, Level level, InteractionHand hand, Entity entity`
  - Returns: `InteractionResult` (cancellable)

#### Entity Events (`EntityEvent`)
- **`ENTITY_SPAWN`** - When entities spawn in the world
  - Parameters: `Entity entity, Level level`
  - Returns: `void` (non-cancellable)
  
- **`ENTITY_DEATH`** - When living entities die
  - Parameters: `LivingEntity entity, DamageSource source`
  - Returns: `void` (non-cancellable)
  
- **`ENTITY_DAMAGE`** - When entities take damage (before damage is applied)
  - Parameters: `LivingEntity entity, DamageSource source, float amount`
  - Returns: `InteractionResult` (cancellable)
  
- **`AFTER_DAMAGE`** - After entities take damage (but don't die)
  - Parameters: `LivingEntity entity, DamageSource source, float baseDamage, float actualDamage, boolean blocked`
  - Returns: `void` (non-cancellable)

#### Command Events (`CommandEvents`)
- **`EVENT`** - Server command registration
  - Parameters: `CommandDispatcher dispatcher, CommandBuildContext context, CommandSelection selection`
  - Returns: `void` (non-cancellable)

#### Client Events (`ClientCommandEvents`, `ClientTickEvents`, `HudEvents`)
- **`ClientCommandEvents.EVENT`** - Client command registration
- **`ClientTickEvents.START_CLIENT_TICK`** - Start of client tick
- **`ClientTickEvents.END_CLIENT_TICK`** - End of client tick  
- **`HudEvents.RENDER_HUD`** - HUD rendering

#### Loot Events (`LootEvents`)
- **`MODIFY`** - Loot table modification
  - Parameters: `ResourceLocation lootTable, Consumer<LootPool.Builder> poolAdder`
  - Returns: `void` (non-cancellable)

### Cross-Platform Implementation Status

| Event | Fabric | Forge | NeoForge | Notes |
|-------|--------|-------|----------|-------|
| `ENTITY_INTERACT` | ✅ | ✅ | ✅ | Fully consistent |
| `ENTITY_SPAWN` | ✅ (Mixin) | ✅ | ✅ | Fabric uses custom Mixin |
| `ENTITY_DEATH` | ✅ | ✅ | ✅ | Fully consistent |
| `ENTITY_DAMAGE` | ✅ | ✅ | ✅ | Cancellation works consistently |
| `AFTER_DAMAGE` | ✅ | ✅ | ✅ | Fully consistent |

For more information about specific event types, see the [API documentation](../common/src/main/java/com/iamkaf/amber/api/event/v1/) in the source code.
