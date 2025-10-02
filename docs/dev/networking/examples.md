# Networking Examples

**Complete working examples for Amber's networking system**

This document provides comprehensive, real-world examples of using Amber's networking API for various common use cases in Minecraft mod development.

## 📋 Table of Contents

1. [Basic Packet Communication](#basic-packet-communication)
2. [Player State Synchronization](#player-state-synchronization)
3. [Inventory Synchronization](#inventory-synchronization)
4. [Configuration Synchronization](#configuration-synchronization)
5. [Chat System](#chat-system)
6. [GUI Data Updates](#gui-data-updates)
7. [Block Entity Synchronization](#block-entity-synchronization)
8. [Performance Monitoring](#performance-monitoring)

## 🚀 Basic Packet Communication

### Simple Message Packet

```java
// MessagePacket.java
public class MessagePacket implements Packet<MessagePacket> {
    private final String message;
    private final UUID senderId;
    private final long timestamp;
    
    public MessagePacket(String message, UUID senderId) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = System.currentTimeMillis();
    }
    
    public MessagePacket(String message, UUID senderId, long timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }
    
    public String getMessage() { return message; }
    public UUID getSenderId() { return senderId; }
    public long getTimestamp() { return timestamp; }
}

// MessageNetworking.java
public class MessageNetworking {
    private static final NetworkChannel CHANNEL = NetworkChannel.create(
        ResourceLocation.fromNamespaceAndPath("example", "messages")
    );
    
    public static void initialize() {
        CHANNEL.register(
            MessagePacket.class,
            // Encoder
            (packet, buf) -> {
                buf.writeUtf(packet.getMessage());
                buf.writeUUID(packet.getSenderId());
                buf.writeLong(packet.getTimestamp());
            },
            // Decoder
            (buf) -> new MessagePacket(
                buf.readUtf(),
                buf.readUUID(),
                buf.readLong()
            ),
            // Handler
            (packet, context) -> {
                context.execute(() -> {
                    Player player = context.getPlayer();
                    if (player != null) {
                        Component message = Component.literal(packet.getMessage())
                            .withStyle(ChatFormatting.BLUE);
                        player.sendSystemMessage(message);
                    }
                });
            }
        );
    }
    
    public static void sendMessage(String message, UUID senderId) {
        CHANNEL.sendToServer(new MessagePacket(message, senderId));
    }
    
    public static void broadcastMessage(String message, UUID senderId) {
        CHANNEL.sendToAllPlayers(new MessagePacket(message, senderId));
    }
}
```

## 👤 Player State Synchronization

### Player Stats Packet

```java
// PlayerStatsPacket.java
public class PlayerStatsPacket implements Packet<PlayerStatsPacket> {
    private final UUID playerId;
    private final Map<String, Integer> stats;
    private final int level;
    private final float experience;
    
    public PlayerStatsPacket(UUID playerId, Map<String, Integer> stats, int level, float experience) {
        this.playerId = playerId;
        this.stats = new HashMap<>(stats);
        this.level = level;
        this.experience = experience;
    }
    
    public UUID getPlayerId() { return playerId; }
    public Map<String, Integer> getStats() { return stats; }
    public int getLevel() { return level; }
    public float getExperience() { return experience; }
}

// PlayerStatsNetworking.java
public class PlayerStatsNetworking {
    private static final NetworkChannel CHANNEL = NetworkChannel.create(
        ResourceLocation.fromNamespaceAndPath("example", "playerstats")
    );
    
    // Client-side cache
    private static final Map<UUID, PlayerStatsPacket> playerStatsCache = new HashMap<>();
    
    public static void initialize() {
        CHANNEL.register(
            PlayerStatsPacket.class,
            // Encoder
            (packet, buf) -> {
                buf.writeUUID(packet.getPlayerId());
                buf.writeInt(packet.getLevel());
                buf.writeFloat(packet.getExperience());
                
                // Write stats map
                buf.writeInt(packet.getStats().size());
                for (Map.Entry<String, Integer> entry : packet.getStats().entrySet()) {
                    buf.writeUtf(entry.getKey());
                    buf.writeInt(entry.getValue());
                }
            },
            // Decoder
            (buf) -> {
                UUID playerId = buf.readUUID();
                int level = buf.readInt();
                float experience = buf.readFloat();
                
                // Read stats map
                int statsSize = buf.readInt();
                Map<String, Integer> stats = new HashMap<>();
                for (int i = 0; i < statsSize; i++) {
                    stats.put(buf.readUtf(), buf.readInt());
                }
                
                return new PlayerStatsPacket(playerId, stats, level, experience);
            },
            // Handler
            (packet, context) -> {
                context.execute(() -> {
                    if (context.isClientSide()) {
                        // Update client-side cache
                        playerStatsCache.put(packet.getPlayerId(), packet);
                        
                        // Update GUI if open
                        updateStatsGUI(packet);
                    } else {
                        // Server-side: validate and process
                        ServerPlayer player = (ServerPlayer) context.getPlayer();
                        if (player != null && player.getUUID().equals(packet.getPlayerId())) {
                            processStatsUpdate(player, packet);
                        }
                    }
                });
            }
        );
    }
    
    public static void requestPlayerStats(UUID playerId) {
        // Send request to server
        CHANNEL.sendToServer(new PlayerStatsPacket(playerId, Collections.emptyMap(), 0, 0));
    }
    
    public static void syncPlayerStats(ServerPlayer player) {
        // Get player's current stats
        Map<String, Integer> stats = getPlayerStats(player);
        int level = player.experienceLevel;
        float experience = player.experienceProgress;
        
        PlayerStatsPacket packet = new PlayerStatsPacket(player.getUUID(), stats, level, experience);
        CHANNEL.sendToPlayer(packet, player);
    }
    
    private static Map<String, Integer> getPlayerStats(ServerPlayer player) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("kills", getPlayerKills(player));
        stats.put("deaths", getPlayerDeaths(player));
        stats.put("blocks_mined", getBlocksMined(player));
        // Add more stats as needed
        return stats;
    }
    
    private static void updateStatsGUI(PlayerStatsPacket packet) {
        // Update client-side GUI with new stats
        // Implementation depends on your GUI system
    }
    
    private static void processStatsUpdate(ServerPlayer player, PlayerStatsPacket packet) {
        // Process server-side stats update
        // Validate and apply changes
    }
    
    // Helper methods for stats retrieval
    private static int getPlayerKills(ServerPlayer player) { return 0; } // Implement
    private static int getPlayerDeaths(ServerPlayer player) { return 0; } // Implement
    private static int getBlocksMined(ServerPlayer player) { return 0; } // Implement
}
```

## 🎒 Inventory Synchronization

### Inventory Update Packet

```java
// InventoryUpdatePacket.java
public class InventoryUpdatePacket implements Packet<InventoryUpdatePacket> {
    private final int containerId;
    private final int slot;
    private final ItemStack itemStack;
    private final InventoryAction action;
    
    public enum InventoryAction {
        SET, ADD, REMOVE, CLEAR
    }
    
    public InventoryUpdatePacket(int containerId, int slot, ItemStack itemStack, InventoryAction action) {
        this.containerId = containerId;
        this.slot = slot;
        this.itemStack = itemStack;
        this.action = action;
    }
    
    public int getContainerId() { return containerId; }
    public int getSlot() { return slot; }
    public ItemStack getItemStack() { return itemStack; }
    public InventoryAction getAction() { return action; }
}

// InventoryNetworking.java
public class InventoryNetworking {
    private static final NetworkChannel CHANNEL = NetworkChannel.create(
        ResourceLocation.fromNamespaceAndPath("example", "inventory")
    );
    
    public static void initialize() {
        CHANNEL.register(
            InventoryUpdatePacket.class,
            // Encoder
            (packet, buf) -> {
                buf.writeInt(packet.getContainerId());
                buf.writeInt(packet.getSlot());
                buf.writeItem(packet.getItemStack());
                buf.writeEnum(packet.getAction());
            },
            // Decoder
            (buf) -> new InventoryUpdatePacket(
                buf.readInt(),
                buf.readInt(),
                buf.readItem(),
                buf.readEnum(InventoryUpdatePacket.InventoryAction.class)
            ),
            // Handler
            (packet, context) -> {
                context.execute(() -> {
                    Player player = context.getPlayer();
                    if (player != null) {
                        handleInventoryUpdate(player, packet);
                    }
                });
            }
        );
    }
    
    public static void syncInventorySlot(ServerPlayer player, int containerId, int slot, ItemStack itemStack) {
        InventoryUpdatePacket packet = new InventoryUpdatePacket(
            containerId, slot, itemStack, InventoryUpdatePacket.InventoryAction.SET
        );
        CHANNEL.sendToPlayer(packet, player);
    }
    
    public static void requestInventoryUpdate(int containerId, int slot, ItemStack itemStack, InventoryUpdatePacket.InventoryAction action) {
        InventoryUpdatePacket packet = new InventoryUpdatePacket(containerId, slot, itemStack, action);
        CHANNEL.sendToServer(packet);
    }
    
    private static void handleInventoryUpdate(Player player, InventoryUpdatePacket packet) {
        // Find the container
        AbstractContainerMenu menu = player.containerMenu;
        if (menu.containerId == packet.getContainerId()) {
            // Apply the inventory update
            switch (packet.getAction()) {
                case SET:
                    menu.setItem(packet.getSlot(), packet.getItemStack());
                    break;
                case ADD:
                    // Add items to existing stack
                    ItemStack existing = menu.getSlot(packet.getSlot()).getItem();
                    if (existing.isEmpty()) {
                        menu.setItem(packet.getSlot(), packet.getItemStack());
                    } else if (ItemStack.isSameItemSameTags(existing, packet.getItemStack())) {
                        existing.grow(packet.getItemStack().getCount());
                    }
                    break;
                case REMOVE:
                    // Remove items from stack
                    ItemStack current = menu.getSlot(packet.getSlot()).getItem();
                    if (!current.isEmpty()) {
                        current.shrink(packet.getItemStack().getCount());
                    }
                    break;
                case CLEAR:
                    menu.setItem(packet.getSlot(), ItemStack.EMPTY);
                    break;
            }
        }
    }
}
```

## ⚙️ Configuration Synchronization

### Config Sync Packet

```java
// ConfigSyncPacket.java
public class ConfigSyncPacket implements Packet<ConfigSyncPacket> {
    private final String configType;
    private final Map<String, Object> configData;
    private final boolean isRequest;
    
    public ConfigSyncPacket(String configType, Map<String, Object> configData, boolean isRequest) {
        this.configType = configType;
        this.configData = new HashMap<>(configData);
        this.isRequest = isRequest;
    }
    
    public String getConfigType() { return configType; }
    public Map<String, Object> getConfigData() { return configData; }
    public boolean isRequest() { return isRequest; }
}

// ConfigNetworking.java
public class ConfigNetworking {
    private static final NetworkChannel CHANNEL = NetworkChannel.create(
        ResourceLocation.fromNamespaceAndPath("example", "config")
    );
    
    private static final Map<String, Object> clientConfig = new HashMap<>();
    
    public static void initialize() {
        CHANNEL.register(
            ConfigSyncPacket.class,
            // Encoder
            (packet, buf) -> {
                buf.writeUtf(packet.getConfigType());
                buf.writeBoolean(packet.isRequest());
                
                // Write config data as JSON
                String json = serializeConfig(packet.getConfigData());
                buf.writeUtf(json);
            },
            // Decoder
            (buf) -> {
                String configType = buf.readUtf();
                boolean isRequest = buf.readBoolean();
                
                // Read config data from JSON
                String json = buf.readUtf();
                Map<String, Object> configData = deserializeConfig(json);
                
                return new ConfigSyncPacket(configType, configData, isRequest);
            },
            // Handler
            (packet, context) -> {
                context.execute(() -> {
                    if (context.isClientSide()) {
                        handleClientConfigSync(packet);
                    } else {
                        handleServerConfigSync(packet, (ServerPlayer) context.getPlayer());
                    }
                });
            }
        );
    }
    
    public static void requestConfig(String configType) {
        ConfigSyncPacket packet = new ConfigSyncPacket(configType, Collections.emptyMap(), true);
        CHANNEL.sendToServer(packet);
    }
    
    public static void syncConfigToPlayer(ServerPlayer player, String configType, Map<String, Object> config) {
        ConfigSyncPacket packet = new ConfigSyncPacket(configType, config, false);
        CHANNEL.sendToPlayer(packet, player);
    }
    
    public static void syncConfigToAllPlayers(String configType, Map<String, Object> config) {
        ConfigSyncPacket packet = new ConfigSyncPacket(configType, config, false);
        CHANNEL.sendToAllPlayers(packet);
    }
    
    private static void handleClientConfigSync(ConfigSyncPacket packet) {
        if (!packet.isRequest()) {
            // Update client config
            clientConfig.putAll(packet.getConfigData());
            
            // Notify listeners
            onConfigUpdated(packet.getConfigType(), packet.getConfigData());
        }
    }
    
    private static void handleServerConfigSync(ConfigSyncPacket packet, ServerPlayer player) {
        if (packet.isRequest()) {
            // Send requested config to player
            Map<String, Object> config = getServerConfig(packet.getConfigType());
            syncConfigToPlayer(player, packet.getConfigType(), config);
        }
    }
    
    private static String serializeConfig(Map<String, Object> config) {
        // Simple JSON serialization - use your preferred JSON library
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            if (!first) json.append(",");
            json.append("\"").append(entry.getKey()).append("\":");
            json.append(serializeValue(entry.getValue()));
            first = false;
        }
        json.append("}");
        return json.toString();
    }
    
    private static String serializeValue(Object value) {
        if (value instanceof String) {
            return "\"" + value + "\"";
        } else if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        } else {
            return "\"" + value.toString() + "\"";
        }
    }
    
    private static Map<String, Object> deserializeConfig(String json) {
        // Simple JSON deserialization - use your preferred JSON library
        Map<String, Object> config = new HashMap<>();
        // Implementation depends on your JSON library
        return config;
    }
    
    private static Map<String, Object> getServerConfig(String configType) {
        // Get server config based on type
        Map<String, Object> config = new HashMap<>();
        // Implementation depends on your config system
        return config;
    }
    
    private static void onConfigUpdated(String configType, Map<String, Object> config) {
        // Notify config listeners
        System.out.println("Config updated: " + configType);
    }
}
```

## 💬 Chat System

### Custom Chat Packet

```java
// ChatPacket.java
public class ChatPacket implements Packet<ChatPacket> {
    private final String message;
    private final String channel;
    private final UUID senderId;
    private final String senderName;
    private final ChatFormatting color;
    private final long timestamp;
    
    public ChatPacket(String message, String channel, UUID senderId, String senderName, ChatFormatting color) {
        this.message = message;
        this.channel = channel;
        this.senderId = senderId;
        this.senderName = senderName;
        this.color = color;
        this.timestamp = System.currentTimeMillis();
    }
    
    public ChatPacket(String message, String channel, UUID senderId, String senderName, ChatFormatting color, long timestamp) {
        this.message = message;
        this.channel = channel;
        this.senderId = senderId;
        this.senderName = senderName;
        this.color = color;
        this.timestamp = timestamp;
    }
    
    public String getMessage() { return message; }
    public String getChannel() { return channel; }
    public UUID getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public ChatFormatting getColor() { return color; }
    public long getTimestamp() { return timestamp; }
}

// ChatNetworking.java
public class ChatNetworking {
    private static final NetworkChannel CHANNEL = NetworkChannel.create(
        ResourceLocation.fromNamespaceAndPath("example", "chat")
    );
    
    private static final Map<String, Set<UUID>> channelSubscriptions = new HashMap<>();
    
    public static void initialize() {
        CHANNEL.register(
            ChatPacket.class,
            // Encoder
            (packet, buf) -> {
                buf.writeUtf(packet.getMessage());
                buf.writeUtf(packet.getChannel());
                buf.writeUUID(packet.getSenderId());
                buf.writeUtf(packet.getSenderName());
                buf.writeEnum(packet.getColor());
                buf.writeLong(packet.getTimestamp());
            },
            // Decoder
            (buf) -> new ChatPacket(
                buf.readUtf(),
                buf.readUtf(),
                buf.readUUID(),
                buf.readUtf(),
                buf.readEnum(ChatFormatting.class),
                buf.readLong()
            ),
            // Handler
            (packet, context) -> {
                context.execute(() -> {
                    if (context.isClientSide()) {
                        handleClientChatMessage(packet);
                    } else {
                        handleServerChatMessage(packet, (ServerPlayer) context.getPlayer());
                    }
                });
            }
        );
    }
    
    public static void sendChatMessage(String message, String channel) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            ChatPacket packet = new ChatPacket(
                message, channel, player.getUUID(), player.getName().getString(), ChatFormatting.WHITE
            );
            CHANNEL.sendToServer(packet);
        }
    }
    
    public static void subscribeToChannel(String channel, UUID playerId) {
        channelSubscriptions.computeIfAbsent(channel, k -> new HashSet<>()).add(playerId);
    }
    
    public static void unsubscribeFromChannel(String channel, UUID playerId) {
        Set<UUID> subscribers = channelSubscriptions.get(channel);
        if (subscribers != null) {
            subscribers.remove(playerId);
            if (subscribers.isEmpty()) {
                channelSubscriptions.remove(channel);
            }
        }
    }
    
    private static void handleClientChatMessage(ChatPacket packet) {
        // Display chat message on client
        Component message = Component.literal("[" + packet.getChannel() + "] ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal(packet.getSenderName())
                .withStyle(packet.getColor()))
            .append(Component.literal(": " + packet.getMessage())
                .withStyle(ChatFormatting.WHITE));
        
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.sendSystemMessage(message);
        }
    }
    
    private static void handleServerChatMessage(ChatPacket packet, ServerPlayer sender) {
        // Validate sender
        if (!sender.getUUID().equals(packet.getSenderId())) {
            return; // Invalid sender
        }
        
        // Get channel subscribers
        Set<UUID> subscribers = channelSubscriptions.get(packet.getChannel());
        if (subscribers == null) {
            return; // No subscribers
        }
        
        // Send to all subscribers
        MinecraftServer server = sender.getServer();
        if (server != null) {
            for (UUID subscriberId : subscribers) {
                ServerPlayer subscriber = server.getPlayerList().getPlayer(subscriberId);
                if (subscriber != null) {
                    CHANNEL.sendToPlayer(packet, subscriber);
                }
            }
        }
    }
}
```

## 🖥️ GUI Data Updates

### GUI Update Packet

```java
// GUIUpdatePacket.java
public class GUIUpdatePacket implements Packet<GUIUpdatePacket> {
    private final String guiType;
    private final int windowId;
    private final Map<String, Object> data;
    private final UpdateType updateType;
    
    public enum UpdateType {
        FULL_UPDATE, PARTIAL_UPDATE, CLOSE_GUI
    }
    
    public GUIUpdatePacket(String guiType, int windowId, Map<String, Object> data, UpdateType updateType) {
        this.guiType = guiType;
        this.windowId = windowId;
        this.data = new HashMap<>(data);
        this.updateType = updateType;
    }
    
    public String getGuiType() { return guiType; }
    public int getWindowId() { return windowId; }
    public Map<String, Object> getData() { return data; }
    public UpdateType getUpdateType() { return updateType; }
}

// GUINetworking.java
public class GUINetworking {
    private static final NetworkChannel CHANNEL = NetworkChannel.create(
        ResourceLocation.fromNamespaceAndPath("example", "gui")
    );
    
    private static final Map<Integer, Screen> openGUIs = new HashMap<>();
    
    public static void initialize() {
        CHANNEL.register(
            GUIUpdatePacket.class,
            // Encoder
            (packet, buf) -> {
                buf.writeUtf(packet.getGuiType());
                buf.writeInt(packet.getWindowId());
                buf.writeEnum(packet.getUpdateType());
                
                // Serialize data
                String json = serializeGUIData(packet.getData());
                buf.writeUtf(json);
            },
            // Decoder
            (buf) -> {
                String guiType = buf.readUtf();
                int windowId = buf.readInt();
                UpdateType updateType = buf.readEnum(GUIUpdatePacket.UpdateType.class);
                
                String json = buf.readUtf();
                Map<String, Object> data = deserializeGUIData(json);
                
                return new GUIUpdatePacket(guiType, windowId, data, updateType);
            },
            // Handler
            (packet, context) -> {
                context.execute(() -> {
                    if (context.isClientSide()) {
                        handleClientGUIUpdate(packet);
                    } else {
                        handleServerGUIUpdate(packet, (ServerPlayer) context.getPlayer());
                    }
                });
            }
        );
    }
    
    public static void openGUI(ServerPlayer player, String guiType, Map<String, Object> data) {
        int windowId = player.containerMenu.containerId;
        GUIUpdatePacket packet = new GUIUpdatePacket(guiType, windowId, data, GUIUpdatePacket.UpdateType.FULL_UPDATE);
        CHANNEL.sendToPlayer(packet, player);
    }
    
    public static void updateGUI(ServerPlayer player, String guiType, Map<String, Object> data) {
        int windowId = player.containerMenu.containerId;
        GUIUpdatePacket packet = new GUIUpdatePacket(guiType, windowId, data, GUIUpdatePacket.UpdateType.PARTIAL_UPDATE);
        CHANNEL.sendToPlayer(packet, player);
    }
    
    public static void closeGUI(ServerPlayer player, String guiType) {
        int windowId = player.containerMenu.containerId;
        GUIUpdatePacket packet = new GUIUpdatePacket(guiType, windowId, Collections.emptyMap(), GUIUpdatePacket.UpdateType.CLOSE_GUI);
        CHANNEL.sendToPlayer(packet, player);
    }
    
    private static void handleClientGUIUpdate(GUIUpdatePacket packet) {
        switch (packet.getUpdateType()) {
            case FULL_UPDATE:
                // Open new GUI
                openClientGUI(packet.getGuiType(), packet.getWindowId(), packet.getData());
                break;
            case PARTIAL_UPDATE:
                // Update existing GUI
                updateClientGUI(packet.getWindowId(), packet.getData());
                break;
            case CLOSE_GUI:
                // Close GUI
                closeClientGUI(packet.getWindowId());
                break;
        }
    }
    
    private static void handleServerGUIUpdate(GUIUpdatePacket packet, ServerPlayer player) {
        // Handle GUI actions from client
        processGUIAction(player, packet.getGuiType(), packet.getData());
    }
    
    private static void openClientGUI(String guiType, int windowId, Map<String, Object> data) {
        // Create and open GUI based on type
        Screen gui = createGUI(guiType, windowId, data);
        if (gui != null) {
            openGUIs.put(windowId, gui);
            Minecraft.getInstance().setScreen(gui);
        }
    }
    
    private static void updateClientGUI(int windowId, Map<String, Object> data) {
        Screen gui = openGUIs.get(windowId);
        if (gui instanceof UpdatableGUI) {
            ((UpdatableGUI) gui).updateData(data);
        }
    }
    
    private static void closeClientGUI(int windowId) {
        Screen gui = openGUIs.remove(windowId);
        if (gui != null && Minecraft.getInstance().screen == gui) {
            Minecraft.getInstance().setScreen(null);
        }
    }
    
    private static Screen createGUI(String guiType, int windowId, Map<String, Object> data) {
        // Factory method to create GUI based on type
        switch (guiType) {
            case "example_gui":
                return new ExampleGUI(windowId, data);
            default:
                return null;
        }
    }
    
    private static void processGUIAction(ServerPlayer player, String guiType, Map<String, Object> data) {
        // Process GUI actions on server
        System.out.println("GUI action from " + player.getName().getString() + ": " + data);
    }
    
    private static String serializeGUIData(Map<String, Object> data) {
        // Simple JSON serialization
        return "{}"; // Implement based on your JSON library
    }
    
    private static Map<String, Object> deserializeGUIData(String json) {
        // Simple JSON deserialization
        return new HashMap<>(); // Implement based on your JSON library
    }
    
    // Interface for updatable GUIs
    public interface UpdatableGUI {
        void updateData(Map<String, Object> data);
    }
    
    // Example GUI implementation
    public static class ExampleGUI extends Screen implements UpdatableGUI {
        private final int windowId;
        private Map<String, Object> data;
        
        public ExampleGUI(int windowId, Map<String, Object> data) {
            super(Component.literal("Example GUI"));
            this.windowId = windowId;
            this.data = data;
        }
        
        @Override
        public void updateData(Map<String, Object> data) {
            this.data = data;
            // Update GUI elements based on new data
        }
    }
}
```

## 🎯 Performance Monitoring

### Performance Metrics Packet

```java
// PerformanceMetricsPacket.java
public class PerformanceMetricsPacket implements Packet<PerformanceMetricsPacket> {
    private final double tps;
    private final long memoryUsed;
    private final long memoryMax;
    private final int playerCount;
    private final int entityCount;
    private final Map<String, Double> customMetrics;
    
    public PerformanceMetricsPacket(double tps, long memoryUsed, long memoryMax, int playerCount, int entityCount, Map<String, Double> customMetrics) {
        this.tps = tps;
        this.memoryUsed = memoryUsed;
        this.memoryMax = memoryMax;
        this.playerCount = playerCount;
        this.entityCount = entityCount;
        this.customMetrics = new HashMap<>(customMetrics);
    }
    
    public double getTps() { return tps; }
    public long getMemoryUsed() { return memoryUsed; }
    public long getMemoryMax() { return memoryMax; }
    public int getPlayerCount() { return playerCount; }
    public int getEntityCount() { return entityCount; }
    public Map<String, Double> getCustomMetrics() { return customMetrics; }
}

// PerformanceNetworking.java
public class PerformanceNetworking {
    private static final NetworkChannel CHANNEL = NetworkChannel.create(
        ResourceLocation.fromNamespaceAndPath("example", "performance")
    );
    
    private static final Map<UUID, PerformanceMetricsPacket> latestMetrics = new HashMap<>();
    
    public static void initialize() {
        CHANNEL.register(
            PerformanceMetricsPacket.class,
            // Encoder
            (packet, buf) -> {
                buf.writeDouble(packet.getTps());
                buf.writeLong(packet.getMemoryUsed());
                buf.writeLong(packet.getMemoryMax());
                buf.writeInt(packet.getPlayerCount());
                buf.writeInt(packet.getEntityCount());
                
                // Write custom metrics
                buf.writeInt(packet.getCustomMetrics().size());
                for (Map.Entry<String, Double> entry : packet.getCustomMetrics().entrySet()) {
                    buf.writeUtf(entry.getKey());
                    buf.writeDouble(entry.getValue());
                }
            },
            // Decoder
            (buf) -> {
                double tps = buf.readDouble();
                long memoryUsed = buf.readLong();
                long memoryMax = buf.readLong();
                int playerCount = buf.readInt();
                int entityCount = buf.readInt();
                
                int metricsSize = buf.readInt();
                Map<String, Double> customMetrics = new HashMap<>();
                for (int i = 0; i < metricsSize; i++) {
                    customMetrics.put(buf.readUtf(), buf.readDouble());
                }
                
                return new PerformanceMetricsPacket(tps, memoryUsed, memoryMax, playerCount, entityCount, customMetrics);
            },
            // Handler
            (packet, context) -> {
                context.execute(() -> {
                    if (context.isClientSide()) {
                        handleClientMetrics(packet);
                    } else {
                        handleServerMetrics(packet, (ServerPlayer) context.getPlayer());
                    }
                });
            }
        );
    }
    
    public static void sendMetricsToPlayer(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server != null) {
            PerformanceMetricsPacket packet = collectServerMetrics(server);
            CHANNEL.sendToPlayer(packet, player);
        }
    }
    
    public static void sendMetricsToAll() {
        MinecraftServer server = getServer();
        if (server != null) {
            PerformanceMetricsPacket packet = collectServerMetrics(server);
            CHANNEL.sendToAllPlayers(packet);
        }
    }
    
    private static PerformanceMetricsPacket collectServerMetrics(MinecraftServer server) {
        // Collect TPS
        double tps = calculateTPS(server);
        
        // Collect memory metrics
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();
        long memoryMax = runtime.maxMemory();
        
        // Collect player and entity counts
        int playerCount = server.getPlayerList().getPlayerCount();
        int entityCount = countEntities(server);
        
        // Collect custom metrics
        Map<String, Double> customMetrics = new HashMap<>();
        customMetrics.put("chunks_loaded", (double) countLoadedChunks(server));
        customMetrics.put("ticks_behind", (double) getTicksBehind(server));
        
        return new PerformanceMetricsPacket(tps, memoryUsed, memoryMax, playerCount, entityCount, customMetrics);
    }
    
    private static void handleClientMetrics(PerformanceMetricsPacket packet) {
        // Update client-side performance display
        displayMetrics(packet);
    }
    
    private static void handleServerMetrics(PerformanceMetricsPacket packet, ServerPlayer player) {
        // Store metrics for admin players
        if (player.hasPermissions(4)) { // Admin level
            latestMetrics.put(player.getUUID(), packet);
        }
    }
    
    private static double calculateTPS(MinecraftServer server) {
        // Calculate TPS based on server tick times
        long[] recentTicks = server.getTickTimes();
        if (recentTicks.length > 0) {
            long averageTickTime = Arrays.stream(recentTicks).sum() / recentTicks.length;
            return Math.min(20.0, 1000000000.0 / averageTickTime);
        }
        return 20.0;
    }
    
    private static int countEntities(MinecraftServer server) {
        int count = 0;
        for (ServerLevel level : server.getAllLevels()) {
            count += level.getEntityCount();
        }
        return count;
    }
    
    private static int countLoadedChunks(MinecraftServer server) {
        int count = 0;
        for (ServerLevel level : server.getAllLevels()) {
            count += level.getChunkSource().getLoadedChunksCount();
        }
        return count;
    }
    
    private static int getTicksBehind(MinecraftServer server) {
        // Calculate how many ticks behind the server is
        long currentTime = System.currentTimeMillis();
        long expectedTime = server.getNextTickTime();
        return (int) Math.max(0, (currentTime - expectedTime) / 50); // 50ms per tick
    }
    
    private static void displayMetrics(PerformanceMetricsPacket packet) {
        // Display metrics on client GUI or HUD
        System.out.println("TPS: " + String.format("%.2f", packet.getTps()));
        System.out.println("Memory: " + (packet.getMemoryUsed() / 1024 / 1024) + "MB / " + (packet.getMemoryMax() / 1024 / 1024) + "MB");
        System.out.println("Players: " + packet.getPlayerCount());
        System.out.println("Entities: " + packet.getEntityCount());
        
        for (Map.Entry<String, Double> entry : packet.getCustomMetrics().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
    
    private static MinecraftServer getServer() {
        // Get server instance
        return null; // Implement based on your platform
    }
}
```

## 📚 Usage Tips

### 1. Error Handling

Always wrap packet handlers in try-catch blocks:

```java
(packet, context) -> {
    context.execute(() -> {
        try {
            // Your packet handling logic
            handlePacket(packet);
        } catch (Exception e) {
            System.err.println("Error handling packet: " + e.getMessage());
            e.printStackTrace();
        }
    });
}
```

### 2. Data Validation

Validate packet data before processing:

```java
(packet, context) -> {
    context.execute(() -> {
        if (packet.getMessage() == null || packet.getMessage().length() > 256) {
            return; // Invalid data
        }
        
        if (packet.getSenderId() == null) {
            return; // Invalid sender
        }
        
        // Process valid packet
        handleValidPacket(packet);
    });
}
```

### 3. Performance Optimization

Use efficient encoding for frequently sent packets:

```java
// Use VarInt for small numbers
buf.writeVarInt(smallNumber);

// Use compression for large strings
String compressed = compress(largeString);
buf.writeUtf(compressed);

// Batch multiple updates
List<Update> updates = collectUpdates();
sendBatchedUpdates(updates);
```

### 4. Testing

Create test packets for debugging:

```java
public static void sendTestPacket() {
    TestPacket packet = new TestPacket("test", 42);
    CHANNEL.sendToServer(packet);
}
```

---

These examples provide a solid foundation for implementing networking in your Minecraft mod using Amber's networking API. Remember to always test your implementations thoroughly across all target platforms!