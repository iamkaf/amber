# Configuration System

Amber provides a powerful, type-safe JSON configuration system that automatically handles serialization and file management. It's designed to be simple to use while offering advanced features for more complex scenarios.

---

## 1. Creating a Configuration Class

To get started, define your configuration options in a simple Java class. Fields in this class will be automatically serialized to a JSON file.

-   Public fields are automatically saved.
-   Default values are used to generate the initial config file.
-   Supports primitives, collections, and nested objects.

```java
// Located at: <gamedir>/config/your-mod-id.json
public class MyModConfig {
    public int maxEnergy = 1000;
    public boolean enableMagic = true;
    public double magicMultiplier = 1.5;

    // Nested objects are supported
    public MagicSettings magicSettings = new MagicSettings();

    public static class MagicSettings {
        public int manaCap = 500;
        public float regenRate = 2.5f;
    }
}
```

---

## 2. Loading Your Configuration

In your mod's initializer, create a `JsonConfigManager` to load and manage your configuration class.

```java
public class MyMod {
    public static final String MOD_ID = "mymod";
    private static MyModConfig config;
    private static JsonConfigManager<MyModConfig> configManager;

    public static void init() {
        // Create the config manager and load the config
        configManager = new JsonConfigManager<>(MOD_ID, new MyModConfig());
        config = configManager.getConfig();

        System.out.println("Loaded config: maxEnergy = " + config.maxEnergy);
    }

    // Provide a static getter for easy access
    public static MyModConfig getConfig() {
        return config;
    }
}
```

The `JsonConfigManager` handles everything:
- If the config file doesn't exist, it's created with the default values.
- If the file is corrupted, it's backed up, and a new default config is created.

---

## 3. Using Configuration Values

Access your configuration values from anywhere in your code using the static getter you created.

```java
public class MagicSystem {
    public void castSpell(Player player) {
        // Get the current config state
        MyModConfig config = MyMod.getConfig();

        if (!config.enableMagic) {
            player.sendSystemMessage(Component.literal("Magic is disabled!"));
            return;
        }

        float manaCost = 10 * config.magicSettings.regenRate;
        // ...
    }
}
```

---

## 4. Saving Configuration Changes

If you modify configuration values at runtime (e.g., through an in-game command), you must save the changes to disk.

```java
public static void updateMaxEnergy(int newMaxEnergy) {
    config.maxEnergy = newMaxEnergy;
    configManager.saveConfig();
}
```

---

## Advanced Features

Amber's configuration system also supports more advanced use cases:

-   **Validation:** Add a `validate()` method to your config class to clamp values or correct invalid entries after loading.
-   **Versioning & Migration:** Include a `configVersion` field and a `migrate()` method to handle updates to your configuration structure between mod versions.
-   **Environment-Specific Configs:** Use `Platform.isClient()` to load different configuration files for the client and server.

For detailed examples of these features, please refer to the Amber source code.