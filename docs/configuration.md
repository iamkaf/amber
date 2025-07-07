# Configuration System

Amber provides a powerful, type-safe JSON configuration system that automatically handles serialization, validation, and file management. The system works consistently across all platforms and provides a clean API for managing mod configurations.

## Overview

The configuration system features:
- **Type-safe JSON serialization**: Automatic conversion between Java objects and JSON
- **Default value handling**: Automatic creation of config files with sensible defaults
- **Error recovery**: Graceful fallback to defaults when config files are corrupted
- **Hot reloading**: Runtime configuration updates
- **Header comments**: Documentation within config files

## Basic Usage

### Creating a Configuration Class

Define your configuration as a simple Java class:

```java
public class MyModConfig {
    // Basic configuration fields with default values
    public int maxEnergy = 1000;
    public boolean enableMagicSystem = true;
    public double magicMultiplier = 1.5;
    public String playerWelcomeMessage = "Welcome to MyMod!";
    
    // Collections are supported
    public List<String> bannedSpells = List.of("fireball", "earthquake");
    public Map<String, Integer> spellCosts = Map.of(
        "heal", 50,
        "teleport", 100,
        "lightning", 150
    );
    
    // Nested objects work too
    public MagicSettings magicSettings = new MagicSettings();
    
    public static class MagicSettings {
        public int manaCap = 500;
        public float regenRate = 2.5f;
        public boolean allowPvpMagic = false;
    }
}
```

### Setting Up Configuration Management

Initialize the configuration system during mod startup:

```java
public class MyMod {
    private static MyModConfig config;
    private static JsonConfigManager<MyModConfig> configManager;
    
    public static void init() {
        // Create the config manager
        configManager = new JsonConfigManager<>(
            "mymod",                    // Mod ID
            new MyModConfig(),          // Initial config instance
            null,                       // Config path (null = default)
            null                        // Header comment (optional)
        );
        
        // Load the configuration
        config = configManager.getConfig();
        
        System.out.println("Loaded config: maxEnergy = " + config.maxEnergy);
    }
    
    // Getter for accessing configuration
    public static MyModConfig getConfig() {
        return config;
    }
    
    // Method to save configuration changes
    public static void saveConfig() {
        configManager.saveConfig();
    }
}
```

### Using Configuration Values

Access configuration values throughout your mod:

```java
public class MagicSystem {
    public static void castSpell(Player player, String spellName) {
        MyModConfig config = MyMod.getConfig();
        
        // Check if magic system is enabled
        if (!config.enableMagicSystem) {
            player.sendSystemMessage(Component.literal("Magic system is disabled!"));
            return;
        }
        
        // Check if spell is banned
        if (config.bannedSpells.contains(spellName)) {
            player.sendSystemMessage(Component.literal("That spell is banned!"));
            return;
        }
        
        // Get spell cost
        int cost = config.spellCosts.getOrDefault(spellName, 0);
        if (cost == 0) {
            player.sendSystemMessage(Component.literal("Unknown spell!"));
            return;
        }
        
        // Check mana
        int currentMana = getCurrentMana(player);
        if (currentMana < cost) {
            player.sendSystemMessage(Component.literal("Not enough mana!"));
            return;
        }
        
        // Cast the spell
        executeSpell(player, spellName, cost);
    }
    
    private static void executeSpell(Player player, String spellName, int cost) {
        // Spell execution logic
        consumeMana(player, cost);
        
        // Apply magic multiplier from config
        float damage = calculateBaseDamage(spellName) * MyMod.getConfig().magicMultiplier;
        // ... rest of spell logic
    }
}
```

## Advanced Configuration

### Complex Data Types

Handle complex configuration structures:

```java
public class AdvancedConfig {
    // Enums are supported
    public DifficultyLevel difficulty = DifficultyLevel.NORMAL;
    public Set<BiomeType> enabledBiomes = Set.of(BiomeType.FOREST, BiomeType.PLAINS);
    
    // Custom objects with validation
    public WorldGenSettings worldGen = new WorldGenSettings();
    public List<CustomRecipe> customRecipes = new ArrayList<>();
    
    public enum DifficultyLevel {
        EASY, NORMAL, HARD, NIGHTMARE
    }
    
    public enum BiomeType {
        FOREST, PLAINS, DESERT, MOUNTAIN, OCEAN
    }
    
    public static class WorldGenSettings {
        public boolean generateCustomOres = true;
        public int oreSpawnRate = 5;
        public Range spawnHeight = new Range(5, 60);
        
        public static class Range {
            public int min;
            public int max;
            
            public Range() {} // Default constructor for JSON
            
            public Range(int min, int max) {
                this.min = min;
                this.max = max;
            }
        }
    }
    
    public static class CustomRecipe {
        public String name;
        public List<String> ingredients;
        public String result;
        public int count = 1;
        
        public CustomRecipe() {} // Default constructor
        
        public CustomRecipe(String name, List<String> ingredients, String result, int count) {
            this.name = name;
            this.ingredients = ingredients;
            this.result = result;
            this.count = count;
        }
    }
}
```

### Configuration Validation

Add validation to ensure configuration integrity:

```java
public class ValidatedConfig {
    public int maxPlayers = 20;
    public double experienceMultiplier = 1.0;
    public List<String> allowedItems = new ArrayList<>();
    
    // Validation method called after loading
    public void validate() {
        // Clamp values to valid ranges
        maxPlayers = Math.max(1, Math.min(maxPlayers, 100));
        experienceMultiplier = Math.max(0.1, Math.min(experienceMultiplier, 10.0));
        
        // Remove invalid items
        allowedItems.removeIf(item -> !isValidItem(item));
        
        // Ensure minimum entries
        if (allowedItems.isEmpty()) {
            allowedItems.addAll(getDefaultItems());
        }
    }
    
    private boolean isValidItem(String itemName) {
        // Validate item exists in registry
        try {
            ResourceLocation id = ResourceLocation.parse(itemName);
            return BuiltInRegistries.ITEM.containsKey(id);
        } catch (Exception e) {
            return false;
        }
    }
    
    private List<String> getDefaultItems() {
        return List.of("minecraft:diamond", "minecraft:emerald", "minecraft:gold_ingot");
    }
}

// Initialize with validation
public class ValidatedConfigManager {
    public static void init() {
        JsonConfigManager<ValidatedConfig> manager = new JsonConfigManager<>(
            "validated_config",
            new ValidatedConfig(),
            null,
            null
        );
        
        ValidatedConfig config = manager.getConfig();
        config.validate(); // Validate after loading
        manager.saveConfig(); // Save cleaned config
    }
}
```

### Configuration Versioning

Handle configuration migrations between mod versions:

```java
public class VersionedConfig {
    public int configVersion = 2; // Current version
    public String modVersion = "1.2.0";
    
    // New fields added in version 2
    public boolean newFeatureEnabled = true;
    public Map<String, Object> advancedSettings = new HashMap<>();
    
    // Migration method
    public void migrate() {
        if (configVersion < 2) {
            // Migrate from version 1 to 2
            migrateFromV1();
            configVersion = 2;
        }
        
        // Update mod version
        modVersion = getCurrentModVersion();
    }
    
    private void migrateFromV1() {
        // Add default values for new fields
        if (advancedSettings.isEmpty()) {
            advancedSettings.put("autoSave", true);
            advancedSettings.put("debugMode", false);
        }
        
        // Convert old settings if needed
        // ... migration logic
    }
    
    private String getCurrentModVersion() {
        // Get current mod version from mod metadata
        return "1.2.0";
    }
}
```

### Environment-Specific Configurations

Handle different configurations for client/server:

```java
public class EnvironmentConfig {
    // Shared settings
    public boolean enableFeature = true;
    public int globalSetting = 100;
    
    // Client-only settings
    public ClientSettings client = new ClientSettings();
    
    // Server-only settings  
    public ServerSettings server = new ServerSettings();
    
    public static class ClientSettings {
        public boolean showParticles = true;
        public float soundVolume = 1.0f;
        public int renderDistance = 16;
        public boolean enableShaders = false;
    }
    
    public static class ServerSettings {
        public int maxConnections = 50;
        public boolean enableLogging = true;
        public int backupInterval = 3600; // seconds
        public List<String> adminPlayers = new ArrayList<>();
    }
}

// Load appropriate settings based on environment
public class EnvironmentConfigManager {
    private static EnvironmentConfig config;
    
    public static void init() {
        JsonConfigManager<EnvironmentConfig> manager = new JsonConfigManager<>(
            Platform.isClient() ? "mymod_client" : "mymod_server",
            new EnvironmentConfig(),
            null,
            null
        );
        
        config = manager.getConfig();
    }
    
    public static ClientSettings getClientSettings() {
        return config != null ? config.client : new ClientSettings();
    }
    
    public static ServerSettings getServerSettings() {
        return config != null ? config.server : new ServerSettings();
    }
}
```

## Dynamic Configuration

### Runtime Configuration Updates

Update configuration at runtime:

```java
public class DynamicConfigManager {
    private static JsonConfigManager<MyModConfig> manager;
    private static MyModConfig config;
    
    public static void init() {
        manager = new JsonConfigManager<>("mymod", new MyModConfig(), null, null);
        config = manager.getConfig();
    }
    
    // Update a specific setting
    public static void updateSetting(String key, Object value) {
        switch (key) {
            case "maxEnergy" -> {
                if (value instanceof Number num) {
                    config.maxEnergy = num.intValue();
                }
            }
            case "enableMagicSystem" -> {
                if (value instanceof Boolean bool) {
                    config.enableMagicSystem = bool;
                }
            }
            case "magicMultiplier" -> {
                if (value instanceof Number num) {
                    config.magicMultiplier = num.doubleValue();
                }
            }
        }
        
        // Save changes immediately
        manager.saveConfig();
        
        // Notify other systems of the change
        notifyConfigChange(key, value);
    }
    
    private static void notifyConfigChange(String key, Object value) {
        // Fire configuration change events
        ConfigEvents.CONFIG_CHANGED.invoker().onConfigChanged(key, value);
    }
}
```

### Configuration Commands

Create commands to modify configuration in-game:

```java
public class ConfigCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("mymod")
            .then(Commands.literal("config")
                .requires(source -> source.hasPermission(2)) // OP level
                .then(Commands.literal("get")
                    .then(Commands.argument("setting", StringArgumentType.string())
                        .executes(ConfigCommands::getConfigValue)))
                .then(Commands.literal("set")
                    .then(Commands.argument("setting", StringArgumentType.string())
                        .then(Commands.argument("value", StringArgumentType.string())
                            .executes(ConfigCommands::setConfigValue))))
                .then(Commands.literal("reload")
                    .executes(ConfigCommands::reloadConfig))));
    }
    
    private static int getConfigValue(CommandContext<CommandSourceStack> context) {
        String setting = StringArgumentType.getString(context, "setting");
        Object value = getConfigValueByKey(setting);
        
        context.getSource().sendSuccess(
            () -> Component.literal(setting + " = " + value),
            false
        );
        
        return 1;
    }
    
    private static int setConfigValue(CommandContext<CommandSourceStack> context) {
        String setting = StringArgumentType.getString(context, "setting");
        String valueStr = StringArgumentType.getString(context, "value");
        
        try {
            Object value = parseValue(setting, valueStr);
            DynamicConfigManager.updateSetting(setting, value);
            
            context.getSource().sendSuccess(
                () -> Component.literal("Updated " + setting + " to " + value),
                true
            );
        } catch (Exception e) {
            context.getSource().sendFailure(
                Component.literal("Failed to update setting: " + e.getMessage())
            );
        }
        
        return 1;
    }
    
    private static int reloadConfig(CommandContext<CommandSourceStack> context) {
        try {
            MyMod.reloadConfig();
            context.getSource().sendSuccess(
                () -> Component.literal("Configuration reloaded successfully"),
                true
            );
        } catch (Exception e) {
            context.getSource().sendFailure(
                Component.literal("Failed to reload config: " + e.getMessage())
            );
        }
        
        return 1;
    }
}
```

## Best Practices

### 1. Configuration Organization

Organize configuration logically:

```java
// Good - grouped by feature
public class OrganizedConfig {
    public MagicSettings magic = new MagicSettings();
    public CombatSettings combat = new CombatSettings();
    public WorldGenSettings worldGen = new WorldGenSettings();
    public UISettings ui = new UISettings();
}

// Avoid - flat structure for complex mods
public class FlatConfig {
    public int magicMaxMana;
    public boolean magicPvpEnabled;
    public float combatDamageMultiplier;
    public boolean combatFriendlyFire;
    public int worldGenOreRate;
    // ... 50+ more fields
}
```

### 2. Default Values

Provide sensible defaults:

```java
public class ConfigWithDefaults {
    // Provide reasonable defaults
    public int maxEnergy = 1000;        // Not 0 or Integer.MAX_VALUE
    public double multiplier = 1.0;     // Neutral multiplier
    public boolean enableFeature = true; // Enable by default for better UX
    
    // Use empty collections, not null
    public List<String> whitelist = new ArrayList<>();
    public Map<String, Integer> costs = new HashMap<>();
}
```

### 3. Documentation

Document your configuration:

```java
public class DocumentedConfig {
    // Use clear, descriptive names
    public int maxEnergyCapacity = 1000;  // Not just "energy"
    public boolean enablePvpMagicSpells = false;  // Be specific
    
    // Add validation ranges in comments
    public double damageMultiplier = 1.0; // Range: 0.1 - 10.0
    public int spawnRate = 5; // Per chunk, range: 1-20
}
```

### 4. Performance Considerations

Cache frequently accessed values:

```java
public class PerformantConfig {
    private static MyModConfig config;
    
    // Cache expensive operations
    private static boolean cachedMagicEnabled;
    private static Set<String> cachedBannedSpells;
    private static long lastCacheUpdate = 0;
    private static final long CACHE_DURATION = 5000; // 5 seconds
    
    public static boolean isMagicEnabled() {
        updateCacheIfNeeded();
        return cachedMagicEnabled;
    }
    
    public static boolean isSpellBanned(String spell) {
        updateCacheIfNeeded();
        return cachedBannedSpells.contains(spell);
    }
    
    private static void updateCacheIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - lastCacheUpdate > CACHE_DURATION) {
            cachedMagicEnabled = config.enableMagicSystem;
            cachedBannedSpells = Set.copyOf(config.bannedSpells);
            lastCacheUpdate = now;
        }
    }
}
```

## Troubleshooting

### Common Issues

1. **Config file not created**
   ```java
   // Ensure you call getConfig() to trigger file creation
   JsonConfigManager<MyConfig> manager = new JsonConfigManager<>(...);
   MyConfig config = manager.getConfig(); // This creates the file
   ```

2. **Values not persisting**
   ```java
   // Make sure to call saveConfig() after changes
   config.maxEnergy = 2000;
   configManager.saveConfig(); // Don't forget this!
   ```

3. **Parsing errors**
   ```java
   // Ensure all config classes have default constructors
   public static class ConfigSection {
       public ConfigSection() {} // Required for JSON deserialization
       
       public int value = 10;
   }
   ```

### Error Recovery

Handle configuration errors gracefully:

```java
public class RobustConfigManager {
    public static MyModConfig loadConfig() {
        try {
            JsonConfigManager<MyModConfig> manager = new JsonConfigManager<>(
                "mymod", new MyModConfig(), null, null);
            return manager.getConfig();
        } catch (Exception e) {
            System.err.println("Failed to load config, using defaults: " + e.getMessage());
            
            // Create backup of corrupted file
            backupCorruptedConfig();
            
            // Return default configuration
            return new MyModConfig();
        }
    }
    
    private static void backupCorruptedConfig() {
        // Move corrupted config to backup file
        Path configFile = Platform.getConfigFolder().resolve("mymod.json");
        Path backupFile = Platform.getConfigFolder().resolve("mymod.json.backup");
        
        try {
            Files.move(configFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to backup corrupted config: " + e.getMessage());
        }
    }
}
```

The configuration system provides a robust, flexible way to manage mod settings while maintaining type safety and ease of use across all platforms.

For more configuration examples, see the [Amber source code](../common/src/main/java/com/iamkaf/amber/api/config/v1/) and other feature documentation.