# Networking API

**A unified networking system for cross-platform Minecraft mod development**

Amber's networking system provides a clean, type-safe API for client-server communication that works identically across Fabric, Forge, and NeoForge platforms. The system abstracts away platform-specific networking complexities while maintaining high performance and reliability.

## 🌟 Features

- **Multi-Platform**: Single API that works across all mod loaders
- **Type-Safe**: Compile-time type checking for packet registration and handling
- **Thread-Safe**: Proper context execution for client/server environments
- **Latency Tracking**: Built-in ping-pong system for network diagnostics
- **Easy to Use**: Simple functional interface-based design

## 🚀 Quick Start

### 1. Create a Network Channel

```java
import com.iamkaf.amber.api.networking.v1.NetworkChannel;
import net.minecraft.resources.ResourceLocation;

public class MyNetworking {
    private static final NetworkChannel CHANNEL = NetworkChannel.create(
        ResourceLocation.fromNamespaceAndPath("mymod", "networking")
    );
}
```

### 2. Define Your Packet

```java
import com.iamkaf.amber.api.networking.v1.Packet;
import net.minecraft.network.FriendlyByteBuf;

public class MyPacket implements Packet<MyPacket> {
    private final String message;
    private final int number;
    
    public MyPacket(String message, int number) {
        this.message = message;
        this.number = number;
    }
    
    // Getters
    public String getMessage() { return message; }
    public int getNumber() { return number; }
}
```

### 3. Register the Packet

```java
public class MyNetworking {
    public static void initialize() {
        CHANNEL.register(
            MyPacket.class,
            // Encoder: How to write packet data
            (packet, buf) -> {
                buf.writeUtf(packet.getMessage());
                buf.writeInt(packet.getNumber());
            },
            // Decoder: How to read packet data
            (buf) -> new MyPacket(buf.readUtf(), buf.readInt()),
            // Handler: What to do when packet is received
            (packet, context) -> {
                System.out.println("Received: " + packet.getMessage() + " - " + packet.getNumber());
                
                // Execute on proper thread
                context.execute(() -> {
                    // Your packet handling logic here
                    Player player = context.getPlayer();
                    if (player != null) {
                        player.sendSystemMessage(Component.literal("Got your message: " + packet.getMessage()));
                    }
                });
            }
        );
    }
}
```

### 4. Send Packets

```java
// Send to server (from client)
CHANNEL.sendToServer(new MyPacket("Hello Server!", 42));

// Send to specific player (from server)
CHANNEL.sendToPlayer(new MyPacket("Hello Client!", 123), serverPlayer);

// Send to all players (from server)
CHANNEL.sendToAllPlayers(new MyPacket("Hello Everyone!", 999));
```

## 📖 Core Concepts

### NetworkChannel

The main entry point for networking operations. Created using `NetworkChannel.create()` with a unique `ResourceLocation` identifier.

```java
// Create a channel for your mod
NetworkChannel channel = NetworkChannel.create(
    ResourceLocation.fromNamespaceAndPath("yourmod", "main")
);
```

### Packet Interface

All network packets must implement the `Packet<T>` interface:

```java
public interface Packet<T extends Packet<T>> {
    // Marker interface - no methods required
}
```

The generic type parameter ensures type safety throughout the system.

### Functional Interfaces

The networking system uses functional interfaces for clean, concise code:

- **`PacketEncoder<T>`**: `(T packet, FriendlyByteBuf buffer) -> void`
- **`PacketDecoder<T>`**: `(FriendlyByteBuf buffer) -> T`
- **`PacketHandler<T>`**: `(T packet, PacketContext context) -> void`

### PacketContext

Provides context information and thread-safe execution:

```java
public interface PacketContext {
    // Get the player who sent/received the packet
    Player getPlayer();
    
    // Execute code on the proper thread
    void execute(Runnable task);
    
    // Check if this is the client environment
    boolean isClientSide();
}
```

## 🏗️ Advanced Usage

### Bidirectional Communication

Many use cases require bidirectional communication. Here's a complete ping-pong example:

```java
public class PingPongExample {
    private static final NetworkChannel CHANNEL = NetworkChannel.create(
        ResourceLocation.fromNamespaceAndPath("example", "pingpong")
    );
    
    public static void initialize() {
        // Register ping packet (client -> server)
        CHANNEL.register(
            PingPacket.class,
            (packet, buf) -> buf.writeLong(packet.getTimestamp()),
            (buf) -> new PingPacket(buf.readLong()),
            (packet, context) -> {
                // Server responds with pong
                context.execute(() -> {
                    ServerPlayer player = (ServerPlayer) context.getPlayer();
                    CHANNEL.sendToPlayer(new PongPacket(packet.getTimestamp()), player);
                });
            }
        );
        
        // Register pong packet (server -> client)
        CHANNEL.register(
            PongPacket.class,
            (packet, buf) -> buf.writeLong(packet.getTimestamp()),
            (buf) -> new PongPacket(buf.readLong()),
            (packet, context) -> {
                // Calculate latency
                context.execute(() -> {
                    long latency = System.currentTimeMillis() - packet.getTimestamp();
                    System.out.println("Ping: " + latency + "ms");
                });
            }
        );
    }
    
    // Send ping from client
    public static void sendPing() {
        CHANNEL.sendToServer(new PingPacket(System.currentTimeMillis()));
    }
}
```

### Complex Data Structures

For more complex packets, you can serialize any data that FriendlyByteBuf supports:

```java
public class ComplexPacket implements Packet<ComplexPacket> {
    private final List<String> items;
    private final Map<String, Integer> counts;
    private final BlockPos position;
    
    public ComplexPacket(List<String> items, Map<String, Integer> counts, BlockPos position) {
        this.items = items;
        this.counts = counts;
        this.position = position;
    }
    
    // Registration
    CHANNEL.register(
        ComplexPacket.class,
        (packet, buf) -> {
            // Write list
            buf.writeInt(packet.items.size());
            for (String item : packet.items) {
                buf.writeUtf(item);
            }
            
            // Write map
            buf.writeInt(packet.counts.size());
            for (Map.Entry<String, Integer> entry : packet.counts.entrySet()) {
                buf.writeUtf(entry.getKey());
                buf.writeInt(entry.getValue());
            }
            
            // Write position
            buf.writeBlockPos(packet.position);
        },
        (buf) -> {
            // Read list
            int listSize = buf.readInt();
            List<String> items = new ArrayList<>();
            for (int i = 0; i < listSize; i++) {
                items.add(buf.readUtf());
            }
            
            // Read map
            int mapSize = buf.readInt();
            Map<String, Integer> counts = new HashMap<>();
            for (int i = 0; i < mapSize; i++) {
                counts.put(buf.readUtf(), buf.readInt());
            }
            
            // Read position
            BlockPos position = buf.readBlockPos();
            
            return new ComplexPacket(items, counts, position);
        },
        (packet, context) -> {
            // Handle the complex packet
            context.execute(() -> {
                System.out.println("Items: " + packet.items);
                System.out.println("Counts: " + packet.counts);
                System.out.println("Position: " + packet.position);
            });
        }
    );
}
```

## 🔍 Built-in Diagnostics

Amber includes built-in networking diagnostics accessible via commands:

```bash
# Test connectivity to server
/amber ping

# Show networking statistics
/amber doctor
```

These commands use the same networking API and can help diagnose connectivity issues.

## ⚠️ Important Notes

### Thread Safety

Always use `context.execute()` for game logic to ensure proper thread execution:

```java
// ❌ Incorrect - might cause thread issues
(packet, context) -> {
    Player player = context.getPlayer();
    player.sendSystemMessage(Component.literal("Hello"));
}

// ✅ Correct - thread-safe execution
(packet, context) -> {
    context.execute(() -> {
        Player player = context.getPlayer();
        player.sendSystemMessage(Component.literal("Hello"));
    });
}
```

### Client vs Server

Use `context.isClientSide()` to determine execution environment:

```java
(packet, context) -> {
    context.execute(() -> {
        if (context.isClientSide()) {
            // Client-side handling
            handleClientSide(packet);
        } else {
            // Server-side handling
            handleServerSide(packet, (ServerPlayer) context.getPlayer());
        }
    });
}
```

### Resource Locations

Use unique resource locations for each channel to avoid conflicts:

```java
// ✅ Good - unique namespace
ResourceLocation.fromNamespaceAndPath("yourmod", "main")

// ❌ Bad - might conflict with other mods
ResourceLocation.fromNamespaceAndPath("networking", "packets")
```

## 🔧 Platform-Specific Details

The networking system handles platform differences automatically:

- **Fabric**: Uses `PayloadTypeRegistry` and `ClientPlayNetworking`/`ServerPlayNetworking`
- **NeoForge**: Uses `PayloadRegistrar` with separate C2S/S2C payload types
- **Forge**: Uses a simplified implementation for compatibility

You don't need to worry about these details - the unified API handles everything.

## 🎯 Best Practices

1. **Initialize Early**: Register packets during mod initialization
2. **Use Context**: Always use `context.execute()` for game logic
3. **Error Handling**: Wrap packet handlers in try-catch blocks
4. **Unique Identifiers**: Use unique resource locations for channels
5. **Data Validation**: Validate packet data before processing
6. **Thread Safety**: Never access game objects directly from packet handlers

## 📚 Related Documentation

- [Getting Started](getting-started.md) - Basic Amber setup
- [Events](events.md) - Event system integration
- [Configuration](configuration.md) - Config synchronization via networking

## 🔗 API Reference

### NetworkChannel

| Method | Description |
|--------|-------------|
| `create(ResourceLocation)` | Create a new network channel |
| `register(Class, PacketEncoder, PacketDecoder, PacketHandler)` | Register a packet type |
| `sendToServer(Packet)` | Send packet to server (client-side) |
| `sendToPlayer(Packet, ServerPlayer)` | Send packet to specific player |
| `sendToAllPlayers(Packet)` | Send packet to all players |
| `sendToAllPlayersExcept(Packet, ServerPlayer)` | Send packet to all except one player |

### PacketContext

| Method | Description |
|--------|-------------|
| `getPlayer()` | Get the player associated with this packet |
| `execute(Runnable)` | Execute code on the proper thread |
| `isClientSide()` | Check if this is the client environment |

---

Ready to implement networking in your mod? Start with the [Quick Start](#-quick-start) guide above!