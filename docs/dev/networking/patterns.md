# Networking Patterns

**Design patterns and best practices for Amber's networking system**

This document outlines common design patterns, architectural approaches, and best practices for building robust networking solutions with Amber's networking API.

## 📋 Table of Contents

1. [Packet Design Patterns](#packet-design-patterns)
2. [Communication Patterns](#communication-patterns)
3. [Synchronization Patterns](#synchronization-patterns)
4. [Error Handling Patterns](#error-handling-patterns)
5. [Performance Patterns](#performance-patterns)
6. [Security Patterns](#security-patterns)
7. [Testing Patterns](#testing-patterns)

## 🏗️ Packet Design Patterns

### 1. Command Pattern

Use the command pattern for actions that can be executed on both client and server:

```java
// Command interface
public interface NetworkCommand extends Packet<NetworkCommand> {
    void execute(PacketContext context);
    boolean canExecute(Player player);
}

// Concrete command
public class TeleportCommand implements NetworkCommand {
    private final BlockPos destination;
    private final UUID playerId;
    
    public TeleportCommand(BlockPos destination, UUID playerId) {
        this.destination = destination;
        this.playerId = playerId;
    }
    
    @Override
    public void execute(PacketContext context) {
        context.execute(() -> {
            Player player = context.getPlayer();
            if (player != null && canExecute(player)) {
                player.teleportTo(destination.getX(), destination.getY(), destination.getZ());
            }
        });
    }
    
    @Override
    public boolean canExecute(Player player) {
        return player.getUUID().equals(playerId) || player.hasPermissions(2);
    }
}

// Command handler registration
public class CommandNetworking {
    private static final NetworkChannel CHANNEL = NetworkChannel.create(
        ResourceLocation.fromNamespaceAndPath("example", "commands")
    );
    
    public static void initialize() {
        CHANNEL.register(
            TeleportCommand.class,
            CommandNetworking::encodeCommand,
            CommandNetworking::decodeCommand,
            (packet, context) -> packet.execute(context)
        );
    }
    
    private static void encodeCommand(TeleportCommand command, FriendlyByteBuf buf) {
        buf.writeBlockPos(command.destination);
        buf.writeUUID(command.playerId);
    }
    
    private static TeleportCommand decodeCommand(FriendlyByteBuf buf) {
        return new TeleportCommand(buf.readBlockPos(), buf.readUUID());
    }
}
```

### 2. State Pattern

Use the state pattern for packets that represent different states:

```java
// State interface
public interface PlayerState {
    String getStateName();
    void handleTransition(Player player, PlayerState newState);
}

// Concrete states
public class IdleState implements PlayerState {
    @Override
    public String getStateName() { return "idle"; }
    
    @Override
    public void handleTransition(Player player, PlayerState newState) {
        // Handle transition from idle to new state
    }
}

public class CombatState implements PlayerState {
    private final long combatStartTime;
    
    public CombatState() {
        this.combatStartTime = System.currentTimeMillis();
    }
    
    @Override
    public String getStateName() { return "combat"; }
    
    @Override
    public void handleTransition(Player player, PlayerState newState) {
        if (newState instanceof IdleState) {
            long combatDuration = System.currentTimeMillis() - combatStartTime;
            // Handle end of combat
        }
    }
}

// State change packet
public class PlayerStatePacket implements Packet<PlayerStatePacket> {
    private final UUID playerId;
    private final String stateName;
    private final Map<String, Object> stateData;
    
    public PlayerStatePacket(UUID playerId, PlayerState state) {
        this.playerId = playerId;
        this.stateName = state.getStateName();
        this.stateData = serializeState(state);
    }
    
    public PlayerStatePacket(UUID playerId, String stateName, Map<String, Object> stateData) {
        this.playerId = playerId;
        this.stateName = stateName;
        this.stateData = stateData;
    }
    
    public PlayerState createState() {
        switch (stateName) {
            case "idle": return new IdleState();
            case "combat": return new CombatState();
            default: return new IdleState();
        }
    }
    
    private Map<String, Object> serializeState(PlayerState state) {
        // Serialize state-specific data
        return new HashMap<>();
    }
}
```

### 3. Builder Pattern

Use the builder pattern for complex packets with many optional fields:

```java
public class ComplexDataPacket implements Packet<ComplexDataPacket> {
    private final String requiredField;
    private final String optionalField1;
    private final Integer optionalField2;
    private final List<String> optionalList;
    private final Map<String, Object> optionalMap;
    
    private ComplexDataPacket(Builder builder) {
        this.requiredField = builder.requiredField;
        this.optionalField1 = builder.optionalField1;
        this.optionalField2 = builder.optionalField2;
        this.optionalList = builder.optionalList;
        this.optionalMap = builder.optionalMap;
    }
    
    public static class Builder {
        private final String requiredField;
        private String optionalField1;
        private Integer optionalField2;
        private List<String> optionalList = new ArrayList<>();
        private Map<String, Object> optionalMap = new HashMap<>();
        
        public Builder(String requiredField) {
            this.requiredField = requiredField;
        }
        
        public Builder withOptionalField1(String value) {
            this.optionalField1 = value;
            return this;
        }
        
        public Builder withOptionalField2(Integer value) {
            this.optionalField2 = value;
            return this;
        }
        
        public Builder withListItem(String item) {
            this.optionalList.add(item);
            return this;
        }
        
        public Builder withMapEntry(String key, Object value) {
            this.optionalMap.put(key, value);
            return this;
        }
        
        public ComplexDataPacket build() {
            return new ComplexDataPacket(this);
        }
    }
    
    // Usage
    public static ComplexDataPacket createPacket() {
        return new ComplexDataPacket.Builder("required")
            .withOptionalField1("optional1")
            .withOptionalField2(42)
            .withListItem("item1")
            .withListItem("item2")
            .withMapEntry("key1", "value1")
            .build();
    }
}
```

### 4. Factory Pattern

Use the factory pattern for creating different types of packets:

```java
public abstract class GameEventPacket implements Packet<GameEventPacket> {
    protected final String eventType;
    protected final long timestamp;
    
    protected GameEventPacket(String eventType) {
        this.eventType = eventType;
        this.timestamp = System.currentTimeMillis();
    }
    
    public abstract void handle(PacketContext context);
    
    public static class Factory {
        public static GameEventPacket createPlayerJoin(UUID playerId, String playerName) {
            return new PlayerJoinEvent(playerId, playerName);
        }
        
        public static GameEventPacket createPlayerLeave(UUID playerId, String reason) {
            return new PlayerLeaveEvent(playerId, reason);
        }
        
        public static GameEventPacket createItemPickup(UUID playerId, ItemStack item) {
            return new ItemPickupEvent(playerId, item);
        }
    }
    
    // Concrete implementations
    private static class PlayerJoinEvent extends GameEventPacket {
        private final UUID playerId;
        private final String playerName;
        
        public PlayerJoinEvent(UUID playerId, String playerName) {
            super("player_join");
            this.playerId = playerId;
            this.playerName = playerName;
        }
        
        @Override
        public void handle(PacketContext context) {
            context.execute(() -> {
                // Handle player join event
                System.out.println(playerName + " joined the game");
            });
        }
    }
    
    private static class PlayerLeaveEvent extends GameEventPacket {
        private final UUID playerId;
        private final String reason;
        
        public PlayerLeaveEvent(UUID playerId, String reason) {
            super("player_leave");
            this.playerId = playerId;
            this.reason = reason;
        }
        
        @Override
        public void handle(PacketContext context) {
            context.execute(() -> {
                // Handle player leave event
                System.out.println("Player left: " + reason);
            });
        }
    }
}
```

## 🔄 Communication Patterns

### 1. Request-Response Pattern

Implement request-response communication with correlation IDs:

```java
public class RequestResponseSystem {
    private static final Map<UUID, CompletableFuture<ResponsePacket>> pendingRequests = new ConcurrentHashMap<>();
    
    // Request packet
    public static class RequestPacket implements Packet<RequestPacket> {
        private final UUID requestId;
        private final String requestType;
        private final Map<String, Object> requestData;
        
        public RequestPacket(String requestType, Map<String, Object> requestData) {
            this.requestId = UUID.randomUUID();
            this.requestType = requestType;
            this.requestData = requestData;
        }
        
        public RequestPacket(UUID requestId, String requestType, Map<String, Object> requestData) {
            this.requestId = requestId;
            this.requestType = requestType;
            this.requestData = requestData;
        }
        
        public UUID getRequestId() { return requestId; }
        public String getRequestType() { return requestType; }
        public Map<String, Object> getRequestData() { return requestData; }
    }
    
    // Response packet
    public static class ResponsePacket implements Packet<ResponsePacket> {
        private final UUID requestId;
        private final boolean success;
        private final Map<String, Object> responseData;
        private final String errorMessage;
        
        public ResponsePacket(UUID requestId, Map<String, Object> responseData) {
            this.requestId = requestId;
            this.success = true;
            this.responseData = responseData;
            this.errorMessage = null;
        }
        
        public ResponsePacket(UUID requestId, String errorMessage) {
            this.requestId = requestId;
            this.success = false;
            this.responseData = Collections.emptyMap();
            this.errorMessage = errorMessage;
        }
        
        public UUID getRequestId() { return requestId; }
        public boolean isSuccess() { return success; }
        public Map<String, Object> getResponseData() { return responseData; }
        public String getErrorMessage() { return errorMessage; }
    }
    
    // Send request and get future response
    public static CompletableFuture<ResponsePacket> sendRequest(String requestType, Map<String, Object> requestData) {
        RequestPacket request = new RequestPacket(requestType, requestData);
        CompletableFuture<ResponsePacket> future = new CompletableFuture<>();
        
        pendingRequests.put(request.getRequestId(), future);
        
        // Send request
        CHANNEL.sendToServer(request);
        
        // Set timeout
        CompletableFuture.delayedExecutor(30, TimeUnit.SECONDS).execute(() -> {
            CompletableFuture<ResponsePacket> pendingFuture = pendingRequests.remove(request.getRequestId());
            if (pendingFuture != null && !pendingFuture.isDone()) {
                pendingFuture.completeExceptionally(new TimeoutException("Request timed out"));
            }
        });
        
        return future;
    }
    
    // Handle response
    public static void handleResponse(ResponsePacket response) {
        CompletableFuture<ResponsePacket> future = pendingRequests.remove(response.getRequestId());
        if (future != null) {
            future.complete(response);
        }
    }
    
    // Usage example
    public static void exampleUsage() {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("playerId", UUID.randomUUID());
        
        sendRequest("player_stats", requestData)
            .thenAccept(response -> {
                if (response.isSuccess()) {
                    System.out.println("Received stats: " + response.getResponseData());
                } else {
                    System.err.println("Request failed: " + response.getErrorMessage());
                }
            })
            .exceptionally(throwable -> {
                System.err.println("Request exception: " + throwable.getMessage());
                return null;
            });
    }
}
```

### 2. Publish-Subscribe Pattern

Implement a publish-subscribe system for event broadcasting:

```java
public class EventBroadcastSystem {
    private static final Map<String, Set<UUID>> subscriptions = new ConcurrentHashMap<>();
    
    // Subscription packet
    public static class SubscriptionPacket implements Packet<SubscriptionPacket> {
        private final String eventType;
        private final boolean subscribe;
        private final UUID subscriberId;
        
        public SubscriptionPacket(String eventType, boolean subscribe, UUID subscriberId) {
            this.eventType = eventType;
            this.subscribe = subscribe;
            this.subscriberId = subscriberId;
        }
        
        public String getEventType() { return eventType; }
        public boolean isSubscribe() { return subscribe; }
        public UUID getSubscriberId() { return subscriberId; }
    }
    
    // Event packet
    public static class EventPacket implements Packet<EventPacket> {
        private final String eventType;
        private final Map<String, Object> eventData;
        private final long timestamp;
        
        public EventPacket(String eventType, Map<String, Object> eventData) {
            this.eventType = eventType;
            this.eventData = eventData;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getEventType() { return eventType; }
        public Map<String, Object> getEventData() { return eventData; }
        public long getTimestamp() { return timestamp; }
    }
    
    // Subscribe to event type
    public static void subscribe(String eventType, UUID subscriberId) {
        subscriptions.computeIfAbsent(eventType, k -> ConcurrentHashMap.newKeySet()).add(subscriberId);
        
        // Send subscription packet to server
        SubscriptionPacket packet = new SubscriptionPacket(eventType, true, subscriberId);
        CHANNEL.sendToServer(packet);
    }
    
    // Unsubscribe from event type
    public static void unsubscribe(String eventType, UUID subscriberId) {
        Set<UUID> subscribers = subscriptions.get(eventType);
        if (subscribers != null) {
            subscribers.remove(subscriberId);
            if (subscribers.isEmpty()) {
                subscriptions.remove(eventType);
            }
        }
        
        // Send unsubscription packet to server
        SubscriptionPacket packet = new SubscriptionPacket(eventType, false, subscriberId);
        CHANNEL.sendToServer(packet);
    }
    
    // Publish event
    public static void publishEvent(String eventType, Map<String, Object> eventData) {
        EventPacket packet = new EventPacket(eventType, eventData);
        
        // Send to all subscribers
        Set<UUID> subscribers = subscriptions.get(eventType);
        if (subscribers != null) {
            for (UUID subscriberId : subscribers) {
                ServerPlayer player = getPlayerById(subscriberId);
                if (player != null) {
                    CHANNEL.sendToPlayer(packet, player);
                }
            }
        }
    }
    
    private static ServerPlayer getPlayerById(UUID playerId) {
        // Implementation to get player by UUID
        return null;
    }
}
```

### 3. Message Queue Pattern

Implement a message queue for reliable delivery:

```java
public class MessageQueue {
    private static final Map<UUID, Queue<QueuedMessage>> playerQueues = new ConcurrentHashMap<>();
    
    // Queued message
    public static class QueuedMessage {
        private final Packet<?> packet;
        private final long timestamp;
        private final int retryCount;
        private final int maxRetries;
        
        public QueuedMessage(Packet<?> packet, int maxRetries) {
            this.packet = packet;
            this.timestamp = System.currentTimeMillis();
            this.retryCount = 0;
            this.maxRetries = maxRetries;
        }
        
        private QueuedMessage(Packet<?> packet, long timestamp, int retryCount, int maxRetries) {
            this.packet = packet;
            this.timestamp = timestamp;
            this.retryCount = retryCount;
            this.maxRetries = maxRetries;
        }
        
        public QueuedMessage retry() {
            return new QueuedMessage(packet, System.currentTimeMillis(), retryCount + 1, maxRetries);
        }
        
        public boolean shouldRetry() {
            return retryCount < maxRetries;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > 30000; // 30 seconds
        }
    }
    
    // Acknowledgment packet
    public static class AckPacket implements Packet<AckPacket> {
        private final UUID messageId;
        private final boolean success;
        
        public AckPacket(UUID messageId, boolean success) {
            this.messageId = messageId;
            this.success = success;
        }
        
        public UUID getMessageId() { return messageId; }
        public boolean isSuccess() { return success; }
    }
    
    // Queue message for player
    public static void queueMessage(UUID playerId, Packet<?> packet, int maxRetries) {
        QueuedMessage message = new QueuedMessage(packet, maxRetries);
        playerQueues.computeIfAbsent(playerId, k -> new ConcurrentLinkedQueue<>()).offer(message);
    }
    
    // Process message queue
    public static void processQueue() {
        for (Map.Entry<UUID, Queue<QueuedMessage>> entry : playerQueues.entrySet()) {
            UUID playerId = entry.getKey();
            Queue<QueuedMessage> queue = entry.getValue();
            
            ServerPlayer player = getPlayerById(playerId);
            if (player != null) {
                processPlayerQueue(player, queue);
            }
        }
    }
    
    private static void processPlayerQueue(ServerPlayer player, Queue<QueuedMessage> queue) {
        QueuedMessage message;
        while ((message = queue.poll()) != null) {
            if (message.isExpired()) {
                continue; // Skip expired messages
            }
            
            try {
                // Send message
                sendMessage(player, message.packet);
                
                // Wait for acknowledgment or retry
                scheduleRetryIfNeeded(player.getUUID(), message);
                
            } catch (Exception e) {
                if (message.shouldRetry()) {
                    queue.offer(message.retry());
                }
            }
        }
    }
    
    private static void sendMessage(ServerPlayer player, Packet<?> packet) {
        // Send packet using appropriate channel
        CHANNEL.sendToPlayer(packet, player);
    }
    
    private static void scheduleRetryIfNeeded(UUID playerId, QueuedMessage message) {
        // Schedule retry logic
        CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS).execute(() -> {
            // Check if acknowledgment was received
            // If not, retry the message
        });
    }
    
    private static ServerPlayer getPlayerById(UUID playerId) {
        // Implementation to get player by UUID
        return null;
    }
}
```

## 🔄 Synchronization Patterns

### 1. Delta Synchronization

Only send changes instead of full state:

```java
public class DeltaSynchronization {
    
    // Delta update packet
    public static class DeltaUpdatePacket implements Packet<DeltaUpdatePacket> {
        private final String objectId;
        private final Map<String, Object> changes;
        private final long version;
        
        public DeltaUpdatePacket(String objectId, Map<String, Object> changes, long version) {
            this.objectId = objectId;
            this.changes = changes;
            this.version = version;
        }
        
        public String getObjectId() { return objectId; }
        public Map<String, Object> getChanges() { return changes; }
        public long getVersion() { return version; }
    }
    
    // Synchronized object
    public static class SynchronizedObject {
        private final String id;
        private final Map<String, Object> properties = new HashMap<>();
        private long version = 0;
        private final Map<String, Object> lastSyncedState = new HashMap<>();
        
        public SynchronizedObject(String id) {
            this.id = id;
        }
        
        public void setProperty(String key, Object value) {
            properties.put(key, value);
            version++;
        }
        
        public Object getProperty(String key) {
            return properties.get(key);
        }
        
        public DeltaUpdatePacket createDelta() {
            Map<String, Object> changes = new HashMap<>();
            
            // Find changed properties
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                String key = entry.getKey();
                Object currentValue = entry.getValue();
                Object lastValue = lastSyncedState.get(key);
                
                if (!Objects.equals(currentValue, lastValue)) {
                    changes.put(key, currentValue);
                }
            }
            
            // Find removed properties
            for (String key : lastSyncedState.keySet()) {
                if (!properties.containsKey(key)) {
                    changes.put(key, null); // null indicates removal
                }
            }
            
            if (!changes.isEmpty()) {
                // Update last synced state
                lastSyncedState.clear();
                lastSyncedState.putAll(properties);
                
                return new DeltaUpdatePacket(id, changes, version);
            }
            
            return null; // No changes
        }
        
        public void applyDelta(DeltaUpdatePacket delta) {
            if (delta.getVersion() > this.version) {
                for (Map.Entry<String, Object> entry : delta.getChanges().entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    
                    if (value == null) {
                        properties.remove(key); // Remove property
                    } else {
                        properties.put(key, value); // Update property
                    }
                }
                
                this.version = delta.getVersion();
            }
        }
    }
}
```

### 2. Optimistic Locking

Prevent conflicts with version-based locking:

```java
public class OptimisticLocking {
    
    // Versioned update packet
    public static class VersionedUpdatePacket implements Packet<VersionedUpdatePacket> {
        private final String objectId;
        private final long expectedVersion;
        private final Map<String, Object> updates;
        private final UUID updaterId;
        
        public VersionedUpdatePacket(String objectId, long expectedVersion, Map<String, Object> updates, UUID updaterId) {
            this.objectId = objectId;
            this.expectedVersion = expectedVersion;
            this.updates = updates;
            this.updaterId = updaterId;
        }
        
        public String getObjectId() { return objectId; }
        public long getExpectedVersion() { return expectedVersion; }
        public Map<String, Object> getUpdates() { return updates; }
        public UUID getUpdaterId() { return updaterId; }
    }
    
    // Update result packet
    public static class UpdateResultPacket implements Packet<UpdateResultPacket> {
        private final String objectId;
        private final boolean success;
        private final long currentVersion;
        private final String errorMessage;
        
        public UpdateResultPacket(String objectId, boolean success, long currentVersion, String errorMessage) {
            this.objectId = objectId;
            this.success = success;
            this.currentVersion = currentVersion;
            this.errorMessage = errorMessage;
        }
        
        public String getObjectId() { return objectId; }
        public boolean isSuccess() { return success; }
        public long getCurrentVersion() { return currentVersion; }
        public String getErrorMessage() { return errorMessage; }
    }
    
    // Versioned object
    public static class VersionedObject {
        private final String id;
        private final Map<String, Object> data = new HashMap<>();
        private long version = 0;
        private final Object lock = new Object();
        
        public VersionedObject(String id) {
            this.id = id;
        }
        
        public UpdateResultPacket tryUpdate(long expectedVersion, Map<String, Object> updates) {
            synchronized (lock) {
                if (version != expectedVersion) {
                    return new UpdateResultPacket(id, false, version, "Version conflict");
                }
                
                // Apply updates
                data.putAll(updates);
                version++;
                
                return new UpdateResultPacket(id, true, version, null);
            }
        }
        
        public long getVersion() {
            synchronized (lock) {
                return version;
            }
        }
        
        public Map<String, Object> getData() {
            synchronized (lock) {
                return new HashMap<>(data);
            }
        }
    }
    
    // Update manager
    private static final Map<String, VersionedObject> objects = new ConcurrentHashMap<>();
    
    public static void handleVersionedUpdate(VersionedUpdatePacket packet, PacketContext context) {
        context.execute(() -> {
            VersionedObject object = objects.get(packet.getObjectId());
            if (object == null) {
                // Send error response
                UpdateResultPacket result = new UpdateResultPacket(
                    packet.getObjectId(), false, 0, "Object not found"
                );
                sendUpdateResult(result, (ServerPlayer) context.getPlayer());
                return;
            }
            
            UpdateResultPacket result = object.tryUpdate(packet.getExpectedVersion(), packet.getUpdates());
            sendUpdateResult(result, (ServerPlayer) context.getPlayer());
            
            if (result.isSuccess()) {
                // Broadcast update to other clients
                broadcastUpdate(packet.getObjectId(), object.getData(), object.getVersion());
            }
        });
    }
    
    private static void sendUpdateResult(UpdateResultPacket result, ServerPlayer player) {
        CHANNEL.sendToPlayer(result, player);
    }
    
    private static void broadcastUpdate(String objectId, Map<String, Object> data, long version) {
        // Broadcast to all relevant clients
        DeltaSynchronization.DeltaUpdatePacket update = 
            new DeltaSynchronization.DeltaUpdatePacket(objectId, data, version);
        CHANNEL.sendToAllPlayers(update);
    }
}
```

## ⚠️ Error Handling Patterns

### 1. Circuit Breaker Pattern

Prevent cascading failures with circuit breakers:

```java
public class CircuitBreaker {
    private final int failureThreshold;
    private final long timeoutDuration;
    private int failureCount;
    private long lastFailureTime;
    private State state;
    
    public enum State {
        CLOSED, OPEN, HALF_OPEN
    }
    
    public CircuitBreaker(int failureThreshold, long timeoutDuration) {
        this.failureThreshold = failureThreshold;
        this.timeoutDuration = timeoutDuration;
        this.state = State.CLOSED;
    }
    
    public boolean allowRequest() {
        if (state == State.OPEN) {
            if (System.currentTimeMillis() - lastFailureTime > timeoutDuration) {
                state = State.HALF_OPEN;
                return true;
            }
            return false;
        }
        return true;
    }
    
    public void recordSuccess() {
        failureCount = 0;
        state = State.CLOSED;
    }
    
    public void recordFailure() {
        failureCount++;
        lastFailureTime = System.currentTimeMillis();
        
        if (failureCount >= failureThreshold) {
            state = State.OPEN;
        }
    }
    
    // Networking with circuit breaker
    public static class ReliableNetworking {
        private static final CircuitBreaker circuitBreaker = new CircuitBreaker(5, 30000);
        
        public static boolean sendPacketSafely(Packet<?> packet, ServerPlayer player) {
            if (!circuitBreaker.allowRequest()) {
                System.err.println("Circuit breaker is open, rejecting request");
                return false;
            }
            
            try {
                CHANNEL.sendToPlayer(packet, player);
                circuitBreaker.recordSuccess();
                return true;
            } catch (Exception e) {
                circuitBreaker.recordFailure();
                System.err.println("Packet send failed: " + e.getMessage());
                return false;
            }
        }
    }
}
```

### 2. Retry Pattern

Implement automatic retry with exponential backoff:

```java
public class RetryPattern {
    
    public static class RetryConfiguration {
        private final int maxAttempts;
        private final long baseDelayMs;
        private final double backoffMultiplier;
        private final long maxDelayMs;
        
        public RetryConfiguration(int maxAttempts, long baseDelayMs, double backoffMultiplier, long maxDelayMs) {
            this.maxAttempts = maxAttempts;
            this.baseDelayMs = baseDelayMs;
            this.backoffMultiplier = backoffMultiplier;
            this.maxDelayMs = maxDelayMs;
        }
        
        public long calculateDelay(int attempt) {
            long delay = (long) (baseDelayMs * Math.pow(backoffMultiplier, attempt - 1));
            return Math.min(delay, maxDelayMs);
        }
    }
    
    public static class RetryableOperation {
        private final Packet<?> packet;
        private final ServerPlayer player;
        private final RetryConfiguration config;
        private int attempt;
        
        public RetryableOperation(Packet<?> packet, ServerPlayer player, RetryConfiguration config) {
            this.packet = packet;
            this.player = player;
            this.config = config;
            this.attempt = 0;
        }
        
        public CompletableFuture<Boolean> execute() {
            return executeWithRetry();
        }
        
        private CompletableFuture<Boolean> executeWithRetry() {
            attempt++;
            
            return CompletableFuture.supplyAsync(() -> {
                try {
                    CHANNEL.sendToPlayer(packet, player);
                    return true;
                } catch (Exception e) {
                    if (attempt < config.maxAttempts) {
                        long delay = config.calculateDelay(attempt);
                        System.out.println("Attempt " + attempt + " failed, retrying in " + delay + "ms");
                        
                        return CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS)
                            .execute(() -> executeWithRetry())
                            .join();
                    } else {
                        System.err.println("All retry attempts failed");
                        return false;
                    }
                }
            });
        }
    }
    
    // Usage
    public static void sendWithRetry(Packet<?> packet, ServerPlayer player) {
        RetryConfiguration config = new RetryConfiguration(3, 1000, 2.0, 10000);
        RetryableOperation operation = new RetryableOperation(packet, player, config);
        
        operation.execute().thenAccept(success -> {
            if (success) {
                System.out.println("Packet sent successfully");
            } else {
                System.err.println("Failed to send packet after all retries");
            }
        });
    }
}
```

## 🚀 Performance Patterns

### 1. Batching Pattern

Batch multiple updates into single packets:

```java
public class BatchingPattern {
    
    // Batch update packet
    public static class BatchUpdatePacket implements Packet<BatchUpdatePacket> {
        private final List<IndividualUpdate> updates;
        private final long timestamp;
        
        public BatchUpdatePacket(List<IndividualUpdate> updates) {
            this.updates = new ArrayList<>(updates);
            this.timestamp = System.currentTimeMillis();
        }
        
        public List<IndividualUpdate> getUpdates() { return updates; }
        public long getTimestamp() { return timestamp; }
    }
    
    public static class IndividualUpdate {
        private final String type;
        private final String objectId;
        private final Map<String, Object> data;
        
        public IndividualUpdate(String type, String objectId, Map<String, Object> data) {
            this.type = type;
            this.objectId = objectId;
            this.data = data;
        }
        
        public String getType() { return type; }
        public String getObjectId() { return objectId; }
        public Map<String, Object> getData() { return data; }
    }
    
    // Batch manager
    public static class BatchManager {
        private final Map<UUID, List<IndividualUpdate>> pendingUpdates = new ConcurrentHashMap<>();
        private final int batchSize = 10;
        private final long batchTimeoutMs = 100;
        
        public void addUpdate(UUID playerId, String type, String objectId, Map<String, Object> data) {
            IndividualUpdate update = new IndividualUpdate(type, objectId, data);
            
            List<IndividualUpdate> playerUpdates = pendingUpdates.computeIfAbsent(playerId, k -> new ArrayList<>());
            
            synchronized (playerUpdates) {
                playerUpdates.add(update);
                
                if (playerUpdates.size() >= batchSize) {
                    flushUpdates(playerId);
                } else {
                    scheduleFlush(playerId);
                }
            }
        }
        
        private void flushUpdates(UUID playerId) {
            List<IndividualUpdate> updates = pendingUpdates.remove(playerId);
            if (updates != null && !updates.isEmpty()) {
                BatchUpdatePacket packet = new BatchUpdatePacket(updates);
                ServerPlayer player = getPlayerById(playerId);
                if (player != null) {
                    CHANNEL.sendToPlayer(packet, player);
                }
            }
        }
        
        private void scheduleFlush(UUID playerId) {
            CompletableFuture.delayedExecutor(batchTimeoutMs, TimeUnit.MILLISECONDS)
                .execute(() -> flushUpdates(playerId));
        }
        
        private ServerPlayer getPlayerById(UUID playerId) {
            // Implementation to get player by UUID
            return null;
        }
    }
}
```

### 2. Compression Pattern

Compress large packets to reduce bandwidth:

```java
public class CompressionPattern {
    
    public static class CompressedPacket implements Packet<CompressedPacket> {
        private final byte[] compressedData;
        private final String originalType;
        private final boolean isCompressed;
        
        public CompressedPacket(Packet<?> originalPacket) {
            this.originalType = originalPacket.getClass().getSimpleName();
            
            try {
                byte[] originalData = serializePacket(originalPacket);
                
                if (originalData.length > 1024) { // Compress if larger than 1KB
                    this.compressedData = compress(originalData);
                    this.isCompressed = true;
                } else {
                    this.compressedData = originalData;
                    this.isCompressed = false;
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to create compressed packet", e);
            }
        }
        
        public CompressedPacket(byte[] compressedData, String originalType, boolean isCompressed) {
            this.compressedData = compressedData;
            this.originalType = originalType;
            this.isCompressed = isCompressed;
        }
        
        public byte[] getDecompressedData() {
            try {
                return isCompressed ? decompress(compressedData) : compressedData;
            } catch (Exception e) {
                throw new RuntimeException("Failed to decompress packet", e);
            }
        }
        
        public String getOriginalType() { return originalType; }
        public boolean isCompressed() { return isCompressed; }
        
        private byte[] compress(byte[] data) throws Exception {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
                gzip.write(data);
            }
            return bos.toByteArray();
        }
        
        private byte[] decompress(byte[] compressedData) throws Exception {
            ByteArrayInputStream bis = new ByteArrayInputStream(compressedData);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            
            try (GZIPInputStream gzip = new GZIPInputStream(bis)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = gzip.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
            }
            
            return bos.toByteArray();
        }
        
        private byte[] serializePacket(Packet<?> packet) {
            // Serialize packet to byte array
            // Implementation depends on your serialization method
            return new byte[0];
        }
    }
}
```

## 🔒 Security Patterns

### 1. Authentication Pattern

Verify player identity for sensitive operations:

```java
public class AuthenticationPattern {
    
    // Authenticated packet
    public static abstract class AuthenticatedPacket implements Packet<AuthenticatedPacket> {
        private final UUID playerId;
        private final String authToken;
        private final long timestamp;
        
        protected AuthenticatedPacket(UUID playerId, String authToken) {
            this.playerId = playerId;
            this.authToken = authToken;
            this.timestamp = System.currentTimeMillis();
        }
        
        public UUID getPlayerId() { return playerId; }
        public String getAuthToken() { return authToken; }
        public long getTimestamp() { return timestamp; }
        
        public abstract void executeAuthenticated(PacketContext context);
        
        public final void execute(PacketContext context) {
            if (isAuthenticated(context)) {
                executeAuthenticated(context);
            } else {
                handleAuthenticationFailure(context);
            }
        }
        
        private boolean isAuthenticated(PacketContext context) {
            // Verify player identity
            Player player = context.getPlayer();
            if (player == null || !player.getUUID().equals(playerId)) {
                return false;
            }
            
            // Verify auth token
            if (!verifyAuthToken(playerId, authToken)) {
                return false;
            }
            
            // Check timestamp (prevent replay attacks)
            long currentTime = System.currentTimeMillis();
            if (currentTime - timestamp > 30000) { // 30 seconds
                return false;
            }
            
            return true;
        }
        
        private boolean verifyAuthToken(UUID playerId, String authToken) {
            // Verify the authentication token
            // Implementation depends on your auth system
            return true;
        }
        
        private void handleAuthenticationFailure(PacketContext context) {
            System.err.println("Authentication failed for packet: " + getClass().getSimpleName());
            // Optionally disconnect the player or take other action
        }
    }
    
    // Example authenticated packet
    public static class AdminCommandPacket extends AuthenticatedPacket {
        private final String command;
        
        public AdminCommandPacket(UUID playerId, String authToken, String command) {
            super(playerId, authToken);
            this.command = command;
        }
        
        @Override
        public void executeAuthenticated(PacketContext context) {
            context.execute(() -> {
                Player player = context.getPlayer();
                if (player.hasPermissions(4)) { // Admin level
                    // Execute admin command
                    executeAdminCommand(command);
                } else {
                    System.err.println("Player lacks admin permissions");
                }
            });
        }
        
        private void executeAdminCommand(String command) {
            // Execute the admin command
            System.out.println("Executing admin command: " + command);
        }
    }
}
```

### 2. Rate Limiting Pattern

Prevent spam and abuse with rate limiting:

```java
public class RateLimitingPattern {
    
    public static class RateLimiter {
        private final Map<UUID, TokenBucket> playerBuckets = new ConcurrentHashMap<>();
        private final int capacity;
        private final int refillRate;
        private final long refillPeriodMs;
        
        public RateLimiter(int capacity, int refillRate, long refillPeriodMs) {
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.refillPeriodMs = refillPeriodMs;
        }
        
        public boolean allowRequest(UUID playerId) {
            TokenBucket bucket = playerBuckets.computeIfAbsent(playerId, 
                k -> new TokenBucket(capacity, refillRate, refillPeriodMs));
            return bucket.tryConsume();
        }
        
        private static class TokenBucket {
            private final int capacity;
            private final int refillRate;
            private final long refillPeriodMs;
            private int tokens;
            private long lastRefillTime;
            
            public TokenBucket(int capacity, int refillRate, long refillPeriodMs) {
                this.capacity = capacity;
                this.refillRate = refillRate;
                this.refillPeriodMs = refillPeriodMs;
                this.tokens = capacity;
                this.lastRefillTime = System.currentTimeMillis();
            }
            
            public synchronized boolean tryConsume() {
                refill();
                
                if (tokens > 0) {
                    tokens--;
                    return true;
                }
                return false;
            }
            
            private void refill() {
                long currentTime = System.currentTimeMillis();
                long timePassed = currentTime - lastRefillTime;
                
                if (timePassed >= refillPeriodMs) {
                    int tokensToAdd = (int) (timePassed / refillPeriodMs) * refillRate;
                    tokens = Math.min(capacity, tokens + tokensToAdd);
                    lastRefillTime = currentTime;
                }
            }
        }
    }
    
    // Rate limited packet handler
    public static class RateLimitedHandler {
        private static final RateLimiter chatLimiter = new RateLimiter(10, 2, 1000); // 10 tokens, refill 2 per second
        private static final RateLimiter commandLimiter = new RateLimiter(5, 1, 1000); // 5 tokens, refill 1 per second
        
        public static void handleChatPacket(ChatPacket packet, PacketContext context) {
            UUID playerId = context.getPlayer().getUUID();
            
            if (!chatLimiter.allowRequest(playerId)) {
                // Rate limit exceeded
                context.execute(() -> {
                    context.getPlayer().sendSystemMessage(
                        Component.literal("You are sending messages too quickly!").withStyle(ChatFormatting.RED)
                    );
                });
                return;
            }
            
            // Process chat packet normally
            handleChatPacketInternal(packet, context);
        }
        
        public static void handleCommandPacket(CommandPacket packet, PacketContext context) {
            UUID playerId = context.getPlayer().getUUID();
            
            if (!commandLimiter.allowRequest(playerId)) {
                // Rate limit exceeded
                context.execute(() -> {
                    context.getPlayer().sendSystemMessage(
                        Component.literal("You are executing commands too quickly!").withStyle(ChatFormatting.RED)
                    );
                });
                return;
            }
            
            // Process command packet normally
            handleCommandPacketInternal(packet, context);
        }
        
        private static void handleChatPacketInternal(ChatPacket packet, PacketContext context) {
            // Normal chat handling
        }
        
        private static void handleCommandPacketInternal(CommandPacket packet, PacketContext context) {
            // Normal command handling
        }
    }
}
```

## 🧪 Testing Patterns

### 1. Mock Networking

Create mock implementations for testing:

```java
public class MockNetworking {
    
    public static class MockNetworkChannel implements NetworkChannel {
        private final Map<Class<?>, PacketHandler<? extends Packet<?>>> handlers = new HashMap<>();
        private final List<SentPacket> sentPackets = new ArrayList<>();
        
        @Override
        public <T extends Packet<T>> void register(
                Class<T> packetClass,
                PacketEncoder<T> encoder,
                PacketDecoder<T> decoder,
                PacketHandler<T> handler
        ) {
            handlers.put(packetClass, handler);
        }
        
        @Override
        public <T extends Packet<T>> void sendToServer(T packet) {
            sentPackets.add(new SentPacket(packet, SentPacket.Target.SERVER));
        }
        
        @Override
        public <T extends Packet<T>> void sendToPlayer(T packet, ServerPlayer player) {
            sentPackets.add(new SentPacket(packet, SentPacket.Target.PLAYER, player.getUUID()));
        }
        
        @Override
        public <T extends Packet<T>> void sendToAllPlayers(T packet) {
            sentPackets.add(new SentPacket(packet, SentPacket.Target.ALL_PLAYERS));
        }
        
        @Override
        public <T extends Packet<T>> void sendToAllPlayersExcept(T packet, ServerPlayer except) {
            sentPackets.add(new SentPacket(packet, SentPacket.Target.ALL_EXCEPT, except.getUUID()));
        }
        
        // Test methods
        public void simulateReceive(Packet<?> packet, PacketContext context) {
            @SuppressWarnings("unchecked")
            PacketHandler<Packet<?>> handler = (PacketHandler<Packet<?>>) handlers.get(packet.getClass());
            if (handler != null) {
                handler.handle(packet, context);
            }
        }
        
        public List<SentPacket> getSentPackets() {
            return new ArrayList<>(sentPackets);
        }
        
        public void clearSentPackets() {
            sentPackets.clear();
        }
        
        public <T extends Packet<T>> List<T> getSentPacketsOfType(Class<T> packetClass) {
            return sentPackets.stream()
                .map(SentPacket::getPacket)
                .filter(packetClass::isInstance)
                .map(packetClass::cast)
                .collect(Collectors.toList());
        }
    }
    
    public static class SentPacket {
        public enum Target { SERVER, PLAYER, ALL_PLAYERS, ALL_EXCEPT }
        
        private final Packet<?> packet;
        private final Target target;
        private final UUID playerId;
        
        public SentPacket(Packet<?> packet, Target target) {
            this(packet, target, null);
        }
        
        public SentPacket(Packet<?> packet, Target target, UUID playerId) {
            this.packet = packet;
            this.target = target;
            this.playerId = playerId;
        }
        
        public Packet<?> getPacket() { return packet; }
        public Target getTarget() { return target; }
        public UUID getPlayerId() { return playerId; }
    }
    
    public static class MockPacketContext implements PacketContext {
        private final boolean isClientSide;
        private final Player player;
        private final List<Runnable> executedTasks = new ArrayList<>();
        
        public MockPacketContext(boolean isClientSide, Player player) {
            this.isClientSide = isClientSide;
            this.player = player;
        }
        
        @Override
        public Player getPlayer() { return player; }
        
        @Override
        public void execute(Runnable task) {
            executedTasks.add(task);
            task.run(); // Execute immediately in tests
        }
        
        @Override
        public boolean isClientSide() { return isClientSide; }
        
        public List<Runnable> getExecutedTasks() { return executedTasks; }
    }
}
```

### 2. Integration Testing

Test complete networking flows:

```java
public class NetworkingIntegrationTest {
    
    @Test
    public void testPingPongFlow() {
        MockNetworking.MockNetworkChannel channel = new MockNetworking.MockNetworkChannel();
        
        // Register ping-pong packets
        channel.register(
            PingPacket.class,
            (packet, buf) -> buf.writeLong(packet.getTimestamp()),
            (buf) -> new PingPacket(buf.readLong()),
            (packet, context) -> {
                // Server responds with pong
                context.execute(() -> {
                    ServerPlayer player = (ServerPlayer) context.getPlayer();
                    channel.sendToPlayer(new PongPacket(packet.getTimestamp()), player);
                });
            }
        );
        
        channel.register(
            PongPacket.class,
            (packet, buf) -> buf.writeLong(packet.getTimestamp()),
            (buf) -> new PongPacket(buf.readLong()),
            (packet, context) -> {
                // Client calculates latency
                context.execute(() -> {
                    long latency = System.currentTimeMillis() - packet.getTimestamp();
                    System.out.println("Latency: " + latency + "ms");
                });
            }
        );
        
        // Create mock players
        MockPlayer client = new MockPlayer(UUID.randomUUID(), "TestClient");
        MockPlayer server = new MockPlayer(UUID.randomUUID(), "TestServer");
        
        // Test flow
        long startTime = System.currentTimeMillis();
        PingPacket ping = new PingPacket(startTime);
        
        // Client sends ping
        channel.sendToServer(ping);
        
        // Verify ping was sent
        List<MockNetworking.SentPacket> sentPackets = channel.getSentPackets();
        assertEquals(1, sentPackets.size());
        assertEquals(MockNetworking.SentPacket.Target.SERVER, sentPackets.get(0).getTarget());
        
        // Simulate server receiving ping
        MockNetworking.MockPacketContext serverContext = 
            new MockNetworking.MockPacketContext(false, server);
        channel.simulateReceive(ping, serverContext);
        
        // Verify server sent pong
        List<PongPacket> pongs = channel.getSentPacketsOfType(PongPacket.class);
        assertEquals(1, pongs.size());
        assertEquals(startTime, pongs.get(0).getTimestamp());
        
        // Simulate client receiving pong
        MockNetworking.MockPacketContext clientContext = 
            new MockNetworking.MockPacketContext(true, client);
        channel.simulateReceive(pongs.get(0), clientContext);
        
        // Verify all tasks were executed
        assertFalse(serverContext.getExecutedTasks().isEmpty());
        assertFalse(clientContext.getExecutedTasks().isEmpty());
    }
    
    // Mock player for testing
    private static class MockPlayer implements ServerPlayer {
        private final UUID uuid;
        private final String name;
        
        public MockPlayer(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }
        
        @Override
        public UUID getUUID() { return uuid; }
        
        @Override
        public Component getName() { return Component.literal(name); }
        
        // Implement other required methods...
    }
}
```

---

These patterns provide a comprehensive foundation for building robust, scalable, and maintainable networking solutions with Amber's networking API. Choose the patterns that best fit your specific use case and combine them as needed.