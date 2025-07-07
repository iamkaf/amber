# Networking Internals

**Understanding Amber's multi-platform networking architecture**

This document provides an in-depth look at how Amber's networking system works internally, including platform-specific implementations and architectural decisions.

## 🏗️ Architecture Overview

Amber's networking system uses a **service-based architecture** with platform abstraction:

```
┌─────────────────────────────────────────────────────────────┐
│                    Your Mod Code                            │
├─────────────────────────────────────────────────────────────┤
│              NetworkChannel API (Common)                    │
├─────────────────────────────────────────────────────────────┤
│           Service Loader (Platform Detection)               │
├─────────────────┬─────────────────┬─────────────────────────┤
│  Fabric Impl    │   NeoForge Impl │      Forge Impl         │
├─────────────────┼─────────────────┼─────────────────────────┤
│  Fabric Loader  │  NeoForge Loader│     Forge Loader        │
└─────────────────┴─────────────────┴─────────────────────────┘
```

## 🔍 Core Components

### 1. Service Loader Pattern

Amber uses Java's `ServiceLoader` to automatically detect and load the correct platform implementation:

```java
// In Services.java
public static final INetworkingService NETWORKING = load(INetworkingService.class);

private static <T> T load(Class<T> clazz) {
    return ServiceLoader.load(clazz)
        .findFirst()
        .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
}
```

Each platform provides its implementation via `META-INF/services/` files:

```
# fabric/src/main/resources/META-INF/services/com.iamkaf.amber.platform.services.INetworkingService
com.iamkaf.amber.platform.FabricNetworkingService

# neoforge/src/main/resources/META-INF/services/com.iamkaf.amber.platform.services.INetworkingService
com.iamkaf.amber.platform.NeoForgeNetworkingService

# forge/src/main/resources/META-INF/services/com.iamkaf.amber.platform.services.INetworkingService
com.iamkaf.amber.platform.ForgeNetworkingService
```

### 2. Channel Implementation

The `NetworkChannelImpl` class provides the unified API while delegating to platform-specific implementations:

```java
public class NetworkChannelImpl implements NetworkChannel {
    private final PlatformNetworkChannel platformChannel;
    
    public NetworkChannelImpl(ResourceLocation channelId) {
        this.platformChannel = Services.NETWORKING.createChannel(channelId);
    }
    
    @Override
    public <T extends Packet<T>> void register(/* ... */) {
        platformChannel.register(/* ... */);
    }
    
    @Override
    public <T extends Packet<T>> void sendToServer(T packet) {
        platformChannel.sendToServer(packet);
    }
    
    // ... other methods delegate similarly
}
```

## 🎯 Platform-Specific Implementations

### Fabric Implementation

Fabric uses the modern `PayloadTypeRegistry` system introduced in recent versions:

```java
// FabricNetworkChannelImpl.java
public class FabricNetworkChannelImpl implements PlatformNetworkChannel {
    
    @Override
    public <T extends Packet<T>> void register(
            Class<T> packetClass,
            PacketEncoder<T> encoder,
            PacketDecoder<T> decoder,
            PacketHandler<T> handler
    ) {
        // Create payload type
        CustomPacketPayload.Type<FabricPacketWrapper<T>> payloadType = 
            new CustomPacketPayload.Type<>(packetId);
        
        // Create stream codec
        StreamCodec<FriendlyByteBuf, FabricPacketWrapper<T>> streamCodec = 
            StreamCodec.of(
                (buffer, wrapper) -> encoder.encode(wrapper.packet, buffer),
                buffer -> new FabricPacketWrapper<>(decoder.decode(buffer), payloadType)
            );
        
        // Register payload type BEFORE handlers
        PayloadTypeRegistry.playS2C().register(payloadType, streamCodec);
        PayloadTypeRegistry.playC2S().register(payloadType, streamCodec);
        
        // Register server-side handler
        ServerPlayNetworking.registerGlobalReceiver(payloadType, (payload, context) -> {
            FabricPacketContext packetContext = new FabricPacketContext(false, context.player());
            handler.handle(payload.packet, packetContext);
        });
        
        // Register client-side handler (if in client environment)
        if (isClientEnvironment()) {
            FabricClientNetworking.registerClientReceiver(payloadType, handler);
        }
    }
}
```

**Key Fabric Features:**
- Uses `PayloadTypeRegistry` for packet registration
- Separates client-only code into `FabricClientNetworking` to avoid server loading issues
- Uses `@Environment(EnvType.CLIENT)` annotations for client-only classes
- Employs bidirectional registration (both C2S and S2C) for maximum compatibility

### NeoForge Implementation

NeoForge uses the `PayloadRegistrar` system with event-based registration:

```java
// NeoForgeNetworkChannelImpl.java
public class NeoForgeNetworkChannelImpl implements PlatformNetworkChannel {
    
    private void registerPendingPacket(Class<?> packetClass, PacketRegistration<?> registration) {
        // Create separate payload types for each direction (Architectury pattern)
        ResourceLocation c2sPacketId = ResourceLocation.fromNamespaceAndPath(
            channelId.getNamespace(), 
            channelId.getPath() + "/" + packetClass.getSimpleName().toLowerCase() + "_c2s"
        );
        ResourceLocation s2cPacketId = ResourceLocation.fromNamespaceAndPath(
            channelId.getNamespace(), 
            channelId.getPath() + "/" + packetClass.getSimpleName().toLowerCase() + "_s2c"
        );
        
        // Create separate payload types
        CustomPacketPayload.Type<NeoForgePacketWrapper<T>> c2sPayloadType = 
            new CustomPacketPayload.Type<>(c2sPacketId);
        CustomPacketPayload.Type<NeoForgePacketWrapper<T>> s2cPayloadType = 
            new CustomPacketPayload.Type<>(s2cPacketId);
        
        // Register client-to-server communication
        registrar.playToServer(
            c2sPayloadType,
            c2sStreamCodec,
            (payload, context) -> {
                NeoForgePacketContext packetContext = new NeoForgePacketContext(false, context.player());
                handler.handle(payload.packet, packetContext);
            }
        );
        
        // Register server-to-client communication
        registrar.playToClient(
            s2cPayloadType,
            s2cStreamCodec,
            (payload, context) -> {
                NeoForgePacketContext packetContext = new NeoForgePacketContext(true, context.player());
                handler.handle(payload.packet, packetContext);
            }
        );
    }
}
```

**Key NeoForge Features:**
- Uses `PayloadRegistrar` from `RegisterPayloadHandlersEvent`
- Implements separate payload types for each direction (`_c2s` and `_s2c` suffixes)
- Follows Architectury API patterns to avoid duplicate registration issues
- Uses deferred registration to wait for proper event timing

### Event Integration

The NeoForge main class sets up event handling:

```java
// AmberNeoForge.java
@Mod(Constants.MOD_ID)
public class AmberNeoForge {
    public AmberNeoForge(IEventBus eventBus) {
        // Register payload handler event
        eventBus.addListener(this::onRegisterPayloadHandlers);
        
        // Initialize mod
        AmberMod.init();
    }
    
    @SubscribeEvent
    public void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        INetworkingService networkingService = Services.NETWORKING;
        if (networkingService instanceof NeoForgeNetworkingService neoForgeService) {
            neoForgeService.setPayloadRegistrar(event.registrar(Constants.MOD_ID));
        }
    }
}
```

### Forge Implementation

Forge uses a simplified approach for compatibility:

```java
// ForgeNetworkChannelImpl.java
public class ForgeNetworkChannelImpl implements PlatformNetworkChannel {
    
    @Override
    public <T extends Packet<T>> void register(/* ... */) {
        // Simplified registration for demonstration
        // In a production implementation, this would use Forge's networking APIs
        registrations.put(packetClass, new PacketRegistration<>(encoder, decoder, handler));
    }
    
    @Override
    public <T extends Packet<T>> void sendToServer(T packet) {
        // Simplified sending - logs packet for testing
        System.out.println("[Forge] Sending packet to server: " + packet);
    }
}
```

## 🔧 Packet Wrapping System

Each platform wraps packets in its own `CustomPacketPayload` implementation:

### Fabric Wrapper

```java
public static class FabricPacketWrapper<T extends Packet<T>> implements CustomPacketPayload {
    public final T packet;
    private final Type<FabricPacketWrapper<T>> type;
    
    public FabricPacketWrapper(T packet, Type<FabricPacketWrapper<T>> type) {
        this.packet = packet;
        this.type = type;
    }
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return type;
    }
}
```

### NeoForge Wrapper

```java
public static class NeoForgePacketWrapper<T extends Packet<T>> implements CustomPacketPayload {
    public final T packet;
    private final Type<NeoForgePacketWrapper<T>> type;
    
    public NeoForgePacketWrapper(T packet, Type<NeoForgePacketWrapper<T>> type) {
        this.packet = packet;
        this.type = type;
    }
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return type;
    }
}
```

## 🧵 Thread Safety Implementation

Each platform provides thread-safe execution through its packet context:

### Fabric Thread Safety

```java
public class FabricPacketContext implements PacketContext {
    
    @Override
    public void execute(Runnable task) {
        if (isClientSide) {
            executeOnClient(task);
        } else {
            executeOnServer(task);
        }
    }
    
    @Environment(EnvType.CLIENT)
    private void executeOnClient(Runnable task) {
        net.minecraft.client.Minecraft.getInstance().execute(task);
    }
    
    private void executeOnServer(Runnable task) {
        net.minecraft.server.MinecraftServer server = player.getServer();
        if (server != null) {
            server.execute(task);
        }
    }
}
```

### NeoForge Thread Safety

```java
public class NeoForgePacketContext implements PacketContext {
    
    @Override
    public void execute(Runnable task) {
        if (isClientSide) {
            executeOnClient(task);
        } else {
            executeOnServer(task);
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    private void executeOnClient(Runnable task) {
        net.minecraft.client.Minecraft.getInstance().execute(task);
    }
    
    private void executeOnServer(Runnable task) {
        net.minecraft.server.MinecraftServer server = player.getServer();
        if (server != null) {
            server.execute(task);
        }
    }
}
```

## 📊 Diagnostic System

Amber includes a built-in diagnostic system using the same networking APIs:

```java
// AmberNetworking.java
public class AmberNetworking {
    private static final NetworkChannel CHANNEL = NetworkChannel.create(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "internal")
    );
    
    private static long totalLatency = 0;
    private static int pingCount = 0;
    
    public static void initialize() {
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
        
        CHANNEL.register(
            PongPacket.class,
            (packet, buf) -> buf.writeLong(packet.getTimestamp()),
            (buf) -> new PongPacket(buf.readLong()),
            (packet, context) -> {
                // Calculate and track latency
                context.execute(() -> {
                    long latency = System.currentTimeMillis() - packet.getTimestamp();
                    totalLatency += latency;
                    pingCount++;
                    
                    Player player = context.getPlayer();
                    player.sendSystemMessage(Component.literal("Ping: " + latency + "ms"));
                });
            }
        );
    }
    
    public static void sendPing() {
        CHANNEL.sendToServer(new PingPacket(System.currentTimeMillis()));
    }
    
    public static double getAverageLatency() {
        return pingCount > 0 ? (double) totalLatency / pingCount : 0.0;
    }
}
```

## 🔍 Debugging and Troubleshooting

### Common Issues and Solutions

1. **Packet Registration Errors**
   - **Fabric**: Ensure `PayloadTypeRegistry.register()` is called before handlers
   - **NeoForge**: Verify `PayloadRegistrar` is set during `RegisterPayloadHandlersEvent`
   - **All Platforms**: Check that packet class implements `Packet<T>` correctly

2. **Client-Only Class Loading**
   - **Fabric**: Use `@Environment(EnvType.CLIENT)` and separate client classes
   - **NeoForge**: Use `@OnlyIn(Dist.CLIENT)` for client-only methods
   - **All Platforms**: Never import client-only classes directly in common code

3. **Thread Safety Issues**
   - Always use `context.execute()` for game logic
   - Never access game objects directly from packet handlers
   - Use `context.isClientSide()` to determine execution environment

### Debug Logging

Enable debug logging to see networking internals:

```java
// In your logging configuration
Logger.getLogger("com.iamkaf.amber.networking").setLevel(Level.DEBUG);
```

## 🎯 Performance Considerations

### Packet Size Optimization

- Use efficient encoding (e.g., `VarInt` instead of `Int` for small numbers)
- Avoid sending large data structures frequently
- Consider delta updates for frequently changing data

### Registration Timing

- Register packets early in mod initialization
- Avoid dynamic packet registration during gameplay
- Use deferred registration for platform-specific timing requirements

### Memory Management

- Packet instances are temporary - avoid holding references
- Use immutable packet data where possible
- Consider object pooling for frequently sent packets

## 📋 Platform Compatibility Matrix

| Feature | Fabric | NeoForge | Forge |
|---------|--------|----------|-------|
| Bidirectional Communication | ✅ | ✅ | ✅ |
| Thread Safety | ✅ | ✅ | ✅ |
| Client-Only Classes | ✅ | ✅ | ✅ |
| Latency Tracking | ✅ | ✅ | ✅ |
| Event Integration | ✅ | ✅ | ✅ |
| Production Ready | ✅ | ✅ | ⚠️ |

**Note**: The Forge implementation is simplified for demonstration purposes. A production implementation would use Forge's actual networking APIs.

## 🔗 Related Files

### Core API Files
- `common/src/main/java/com/iamkaf/amber/api/networking/v1/`
- `common/src/main/java/com/iamkaf/amber/networking/v1/`

### Platform Implementations
- `fabric/src/main/java/com/iamkaf/amber/networking/fabric/`
- `neoforge/src/main/java/com/iamkaf/amber/networking/neoforge/`
- `forge/src/main/java/com/iamkaf/amber/networking/forge/`

### Service Registration
- `*/src/main/resources/META-INF/services/com.iamkaf.amber.platform.services.INetworkingService`

---

This internal documentation provides the foundation for understanding and extending Amber's networking system. For usage examples, see the main [Networking API](../networking.md) documentation.