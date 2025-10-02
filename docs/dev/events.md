# Event System

Amber's event system provides a unified way to listen and react to in-game actions across Fabric, Forge, and NeoForge. It's designed to be consistent and reliable, ensuring your code works the same everywhere.

---

## 1. Registering an Event

To listen to an event, you register a callback function. This is typically done in your mod's initializer.

Here’s how you can listen for a player interacting with an entity:

```java
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import net.minecraft.world.InteractionResult;

public class MyEventHandlers {
    public static void init() {
        // Register a callback for the ENTITY_INTERACT event
        PlayerEvents.ENTITY_INTERACT.register((player, level, hand, entity) -> {
            System.out.println(player.getName().getString() + " interacted with " + entity.getName().getString());

            // Return PASS to allow other mods (and the game) to process the event
            return InteractionResult.PASS;
        });
    }
}
```

---

## 2. Controlling Event Outcomes

Many events are **cancellable**. You can control the outcome of the game's action by returning a specific `InteractionResult`.

-   `InteractionResult.PASS`: The default. Allows the action to continue.
-   `InteractionResult.SUCCESS`: Stops the action and signals a successful outcome.
-   `InteractionResult.FAIL`: Stops the action and signals a failure.
-   `InteractionResult.CONSUME`: Stops the action.

Here's an example of cancelling damage to a protected entity:

```java
EntityEvent.ENTITY_DAMAGE.register((entity, source, amount) -> {
    if (entity.getTags().contains("protected")) {
        // Cancel the damage
        return InteractionResult.FAIL;
    }
    // Allow the damage
    return InteractionResult.PASS;
});
```

---

## 3. Common Event Categories

Amber provides a wide range of events. Here are some of the most common categories:

-   **`PlayerEvents`**: Actions performed by players (interacting, breaking blocks).
-   **`EntityEvents`**: Entity lifecycle events (spawning, death, taking damage).
-   **`BlockEvents`**: Block-related actions (placing, breaking).
-   **`CommandEvents`**: For registering server commands.
-   **`ClientTickEvents`**: For running code on the client each tick.
-   **`HudEvents`**: For rendering custom elements on the in-game HUD.

---

## 4. Handling Client-Side Events

Some events only exist on the client. Always wrap client-side event registrations in a `Platform.isClient()` check to prevent crashes on a dedicated server.

```java
import com.iamkaf.amber.api.platform.v1.Platform;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientTickEvents;

public class ClientHandlers {
    public static void init() {
        if (Platform.isClient()) {
            ClientTickEvents.END_CLIENT_TICK.register(() -> {
                // This code only runs on the client
                handleClientTick();
            });
        }
    }

    private static void handleClientTick() {
        // Your client-side logic here
    }
}
```

For a full list of available events and advanced features like creating your own custom events, please refer to the Amber API source code.