# Amber

**A comprehensive multiloader library for Minecraft mod development**

Amber is a powerful foundation library that provides unified APIs for common modding tasks across **Fabric**, **Forge**, and **NeoForge** platforms. Built with cross-platform compatibility in mind, Amber eliminates the complexity of managing platform-specific code while offering modern, easy-to-use APIs for Minecraft mod development.

## 🎯 What Amber Offers

Amber provides a complete toolkit for Minecraft mod development:

### 🌐 Cross-Platform Compatibility
- **Write once, run everywhere**: Unified APIs that work identically across Fabric, Forge, and NeoForge
- **Platform abstraction**: Service-based architecture that handles platform differences automatically
- **Modern Minecraft support**: Built for Minecraft 1.21.7 with backwards compatibility

### 🔧 Core Systems
- **[Registry](registry.md)**: Deferred registration system for items, blocks, and entities
- **[Events](events.md)**: Fabric-inspired event system that works across all platforms
- **[Configuration](configuration.md)**: JSON-based configuration with automatic serialization
- **[Networking](networking.md)**: Type-safe client-server communication with multi-platform support

### 🎮 Game Integration
- **[Commands](commands.md)**: Simplified command registration and handling
- **[HUD Rendering](hud.md)**: Client-side HUD utilities with visibility management
- **[Keybinds](keybinds.md)**: Cross-platform keybind registration
- **[Sound System](sound.md)**: Easy sound playback with custom parameters

### 🛠️ Developer Utilities
- **[Item Helpers](items.md)**: Tooltips, repair, attributes, and inventory management
- **[Level Helpers](level.md)**: World interaction, scheduling, and item dropping
- **[Player Feedback](player.md)**: Chat messages and action bar notifications
- **[Math Utilities](math.md)**: Probability calculations and random events

## 🚀 Quick Start

### Adding Amber to Your Project

**Gradle (build.gradle)**:
```gradle
repositories {
    maven { url = 'https://raw.githubusercontent.com/iamkaf/modresources/main/maven/' }
}

dependencies {
    // Common
    implementation "com.iamkaf:amber-common:6.0.9+1.21.7"
    
    // Platform-specific (choose one)
    implementation "com.iamkaf:amber-fabric:6.0.9+1.21.7"
    implementation "com.iamkaf:amber-forge:6.0.9+1.21.7"
    implementation "com.iamkaf:amber-neoforge:6.0.9+1.21.7"
}
```

### Basic Setup

**Initialize Amber in your mod**:
```java
public class YourMod {
    public static final String MOD_ID = "yourmod";
    
    public static void init() {
        // Initialize with Amber - automatically detects mod name and version
        AmberInitializer.initialize(MOD_ID);
        // Register initialization callback
        AmberInitializer.register(MOD_ID, YourMod::onInitialize);
    }
    
    private static void onInitialize() {
        // Your mod initialization code here
        System.out.println("YourMod initialized with Amber!");
    }
}
```

## 📖 Documentation

### Getting Started
- **[Installation Guide](getting-started.md)** - Setting up Amber in your project
- **[Basic Usage](basic-usage.md)** - Common patterns and best practices
- **[Migration Guide](migration.md)** - Upgrading from older versions

### Core Features
- **[Registry System](registry.md)** - Unified item/block registration
- **[Event System](events.md)** - Comprehensive event handling
- **[Configuration](configuration.md)** - JSON configuration management
- **[Networking System](networking.md)** - Cross-platform client-server communication

### Game Integration
- **[Commands](commands.md)** - Command registration and handling
- **[Client-Side Features](client.md)** - HUD, keybinds, and rendering
- **[World Interaction](world.md)** - Level helpers and utilities

### Utilities
- **[Item System](items.md)** - Item helpers and utilities
- **[Player Interaction](player.md)** - Feedback and communication
- **[Math & Utilities](utilities.md)** - Common helper functions

## 🔄 Version Information

- **Current Version**: 6.0.9+1.21.7
- **Minecraft Version**: 1.21.7
- **Supported Platforms**: Fabric, Forge, NeoForge
- **License**: MIT

## 🏗️ Architecture

Amber follows a service-based architecture:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Your Mod      │    │   Your Mod      │    │   Your Mod      │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│  Amber Common   │    │  Amber Common   │    │  Amber Common   │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ Amber Fabric    │    │  Amber Forge    │    │ Amber NeoForge  │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│     Fabric      │    │     Forge       │    │    NeoForge     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🤝 Contributing

Amber is designed to be a community-driven project. Contributions are welcome!

## 📄 License

Amber is released under the MIT License. See [LICENSE](../LICENSE) for details.

---

**Ready to get started?** Check out the **[Getting Started Guide](getting-started.md)** to begin using Amber in your mod development projects.