# Event System

Amber provides a comprehensive, cross-platform event system that allows you to respond to game events in a unified way across Fabric, Forge, and NeoForge. The system is inspired by Fabric's event architecture but works consistently across all platforms.

## Overview

The event system allows you to:
- Listen to game events (player interactions, world changes, etc.)
- Register callbacks that execute when events occur
- Control event flow with return values
- Handle events with proper timing and thread safety

## Event Registration

### Basic Event Handling

Register event listeners during mod initialization:

```java
import com.iamkaf.amber.api.event.v1.*;

public class MyEventHandlers {
    public static void init() {
        // Player interaction events
        PlayerEvents.INTERACT_ENTITY.register((player, entity, hand) -> {
            if (player.getItemInHand(hand).is(MyItems.MAGIC_WAND.get())) {
                castSpell(player, entity);
                return true; // Cancel default interaction
            }
            return false; // Allow default interaction
        });
        
        // Command registration events  
        CommandEvents.REGISTER.register((dispatcher, context, selection) -> {
            MyCommands.register(dispatcher);
        });
        
        // Client-side events
        if (Platform.getInstance().isClient()) {
            ClientEvents.CLIENT_TICK_END.register(client -> {
                handleClientTick(client);
            });
        }
    }
    
    private static void castSpell(Player player, Entity target) {
        // Spell casting logic
        System.out.println("Casting spell on " + target.getName().getString());
    }
    
    private static void handleClientTick(Minecraft client) {
        // Client tick logic
    }
}
```

### Event Return Values

Many events support return values to control game behavior:

```java
public class InteractionHandlers {
    public static void init() {
        // Return true to cancel/override default behavior
        PlayerEvents.INTERACT_ENTITY.register((player, entity, hand) -> {
            if (entity instanceof Cow && player.getItemInHand(hand).is(Items.DIAMOND)) {
                // Custom cow interaction
                return true; // Prevents default interaction
            }
            return false; // Allows default interaction
        });
        
        // Some events use different return patterns
        PlayerEvents.INTERACT_BLOCK.register((player, level, hand, hit) -> {
            Block block = level.getBlockState(hit.getBlockPos()).getBlock();
            if (block == MyBlocks.MAGIC_BLOCK.get()) {
                handleMagicBlock(player, hit.getBlockPos());
                return InteractionResult.SUCCESS; // Specific result type
            }
            return InteractionResult.PASS; // Let other handlers try
        });
    }
}
```

## Available Events

### Player Events

Handle player-related actions:

```java
public class PlayerEventHandlers {
    public static void init() {
        // Entity interactions
        PlayerEvents.INTERACT_ENTITY.register((player, entity, hand) -> {
            // Called when player interacts with entities
            return false;
        });
        
        // Block interactions  
        PlayerEvents.INTERACT_BLOCK.register((player, level, hand, hit) -> {
            // Called when player interacts with blocks
            return InteractionResult.PASS;
        });
        
        // Item usage
        PlayerEvents.USE_ITEM.register((player, level, hand) -> {
            // Called when player uses an item
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        });
    }
}
```

### Command Events

Register commands across platforms:

```java
public class CommandEventHandlers {
    public static void init() {
        CommandEvents.REGISTER.register((dispatcher, context, selection) -> {
            // Register your commands here
            dispatcher.register(Commands.literal("mycommand")
                .executes(context -> {
                    context.getSource().sendSuccess(
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
        if (Platform.getInstance().isClient()) {
            
            // Client tick events
            ClientEvents.CLIENT_TICK_END.register(client -> {
                // Called every client tick
                handleClientTick(client);
            });
            
            // HUD rendering
            HudEvents.RENDER_HUD.register((graphics, partialTick) -> {
                renderCustomHUD(graphics, partialTick);
            });
        }
    }
    
    private static void handleClientTick(Minecraft client) {
        // Check for key presses, update client state, etc.
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
}
```

## Advanced Event Handling

### Event Phases and Ordering

Some events support phases for controlling execution order:

```java
public class AdvancedEventHandlers {
    public static void init() {
        // Early phase - executes before most other handlers
        PlayerEvents.INTERACT_ENTITY.register(EventPriority.HIGH, (player, entity, hand) -> {
            // High priority logic
            return false;
        });
        
        // Normal phase - default priority
        PlayerEvents.INTERACT_ENTITY.register((player, entity, hand) -> {
            // Normal priority logic
            return false;
        });
        
        // Late phase - executes after most other handlers  
        PlayerEvents.INTERACT_ENTITY.register(EventPriority.LOW, (player, entity, hand) -> {
            // Low priority logic - cleanup, logging, etc.
            return false;
        });
    }
}
```

### Conditional Event Registration

Register events conditionally based on configuration or platform:

```java
public class ConditionalEventHandlers {
    public static void init() {
        // Only register if feature is enabled
        if (MyModConfig.get().enableMagicSystem) {
            PlayerEvents.INTERACT_ENTITY.register(MagicHandlers::handleMagicInteraction);
        }
        
        // Platform-specific events
        if (Platform.getInstance().isClient()) {
            ClientEvents.CLIENT_TICK_END.register(ClientHandlers::handleTick);
        }
        
        // Development-only events
        if (Platform.getInstance().isDevelopmentEnvironment()) {
            CommandEvents.REGISTER.register(DebugCommands::register);
        }
    }
}
```

### Event Data Access

Access comprehensive event data:

```java
public class DetailedEventHandlers {
    public static void init() {
        PlayerEvents.INTERACT_ENTITY.register((player, entity, hand) -> {
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
            Level level = player.level();
            boolean isClientSide = level.isClientSide;
            
            System.out.println(String.format(
                "Player %s (%s) interacted with %s at %s using %s in %s hand",
                playerName, playerId, entityType, entityPos, 
                heldItem.getDisplayName().getString(),
                isMainHand ? "main" : "off"
            ));
            
            return false;
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

## Event-Driven Architecture

Use events to create loosely coupled mod features:

```java
// Feature modules that communicate via events
public class MagicModule {
    public static void init() {
        // Listen for player interactions
        PlayerEvents.INTERACT_ENTITY.register((player, entity, hand) -> {
            if (isMagicUser(player)) {
                return handleMagicInteraction(player, entity, hand);
            }
            return false;
        });
        
        // Listen for custom events from other modules
        QuestEvents.QUEST_COMPLETED.register((player, quest) -> {
            if (quest.getType() == QuestType.MAGIC) {
                grantMagicReward(player, quest);
            }
        });
    }
}

public class QuestModule {
    public static void init() {
        // Listen for entity defeats
        EntityEvents.ENTITY_DEATH.register((entity, damageSource) -> {
            if (damageSource.getEntity() instanceof Player player) {
                checkQuestProgress(player, entity);
            }
        });
    }
    
    private static void checkQuestProgress(Player player, Entity entity) {
        // Check if this completes a quest
        Quest quest = getActiveQuest(player);
        if (quest != null && quest.checkCompletion(entity)) {
            // Fire quest completion event
            QuestEvents.QUEST_COMPLETED.invoker().onQuestCompleted(player, quest);
        }
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
        PlayerEvents.INTERACT_ENTITY.register((player, entity, hand) -> {
            ItemStack heldItem = player.getItemInHand(hand);
            
            // Fast check using cached set
            if (!MAGIC_ITEMS.contains(heldItem.getItem())) {
                return false; // Early exit for non-magic items
            }
            
            // Only do expensive operations for relevant items
            return handleMagicInteraction(player, entity, hand, heldItem);
        });
    }
    
    // Avoid repeated expensive operations
    private static final Map<EntityType<?>, MagicEffect> ENTITY_EFFECTS = Map.of(
        EntityType.COW, MagicEffect.MILK_BOOST,
        EntityType.SHEEP, MagicEffect.WOOL_GROWTH,
        EntityType.CHICKEN, MagicEffect.EGG_PRODUCTION
    );
    
    private static boolean handleMagicInteraction(Player player, Entity entity, 
                                                 InteractionHand hand, ItemStack item) {
        // Use cached lookup instead of complex logic
        MagicEffect effect = ENTITY_EFFECTS.get(entity.getType());
        if (effect != null) {
            effect.apply(entity, player);
            return true;
        }
        return false;
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
        if (Platform.getInstance().isClient()) {
            registerClientEvents();
        }
    }
    
    private static void registerAdvancedMagicEvents() {
        // Expensive magic system events
        PlayerEvents.INTERACT_ENTITY.register(AdvancedMagicHandlers::handleInteraction);
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

## Migration from Platform-Specific Events

### From Fabric Events

```java
// Old Fabric way
UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
    // Handle interaction
    return ActionResult.PASS;
});

// New Amber way
PlayerEvents.INTERACT_ENTITY.register((player, entity, hand) -> {
    // Handle interaction  
    return false; // false = PASS, true = cancel
});
```

### From Forge Events

```java
// Old Forge way
@SubscribeEvent
public static void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {
    // Handle interaction
    event.setCanceled(true);
}

// New Amber way
PlayerEvents.INTERACT_ENTITY.register((player, entity, hand) -> {
    // Handle interaction
    return true; // true cancels the event
});
```

The Amber event system provides a cleaner, more consistent API while maintaining the functionality you expect from platform-specific event systems.

For more information about specific event types, see the [API documentation](../common/src/main/java/com/iamkaf/amber/api/event/v1/) in the source code.