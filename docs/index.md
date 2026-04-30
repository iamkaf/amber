---
layout: home

hero:
  name: Amber
  text: A comprehensive multiloader library for Minecraft mod development
  tagline: Write once, run everywhere - Unified APIs for Fabric, Forge, and NeoForge
  image:
    src: /icon.png
    alt: Amber
  actions:
    - theme: brand
      text: Get Started
      link: /v9/guide/getting-started
    - theme: alt
      text: View on Modrinth
      link: https://modrinth.com/mod/amber
    - theme: alt
      text: View on CurseForge
      link: https://www.curseforge.com/minecraft/mc-mods/amber-lib
    - theme: alt
      text: View on GitHub
      link: https://github.com/iamkaf/amber

features:
  - icon: 🌐
    title: Cross-Platform Compatibility
    details: Unified APIs that work identically across Fabric, Forge, and NeoForge platforms
  - icon: 🔧
    title: Core Systems
    details: Registry, Events, Configuration, and Networking systems designed for modern Minecraft modding
  - icon: 🎮
    title: Game Integration
    details: Commands, HUD rendering, keybinds, and sound system utilities
  - icon: 🛠️
    title: Developer Utilities
    details: Item helpers, level utilities, player feedback, and math utilities
  - icon: 📦
    title: Modern Architecture
    details: Service-based architecture with platform abstraction handles differences automatically
  - icon: 🚀
    title: Easy to Use
    details: Simple, intuitive APIs that get you modding faster with less boilerplate

---

## Documentation Versions

::: tip Current Support Policy
Amber follows a Perfect Parity policy for supported Minecraft lines. The `+<mc-version>` suffix identifies the target Minecraft line only, not a reduced feature set.

- **Modern Supported** → Minecraft `1.20+`
- **Legacy Supported** → Minecraft `1.17-1.19.4`
- **EOL** → Minecraft `1.14.4-1.16.5`
:::

See the [Support Policy](/support-policy), [Support Matrix](/support-matrix), and [Parity Checks](/parity-checks) for current compatibility expectations.

## Quick Start

### 1. Add Amber to Your Project

```gradle
repositories {
    maven { url = 'https://raw.githubusercontent.com/iamkaf/modresources/main/maven/' }
}

dependencies {
    implementation "com.iamkaf.amber:amber-common:<amber-version>+<mc-version>"
    // Platform-specific dependencies
    implementation "com.iamkaf.amber:amber-fabric:<amber-version>+<mc-version>"
    // OR implementation "com.iamkaf.amber:amber-forge:<amber-version>+<mc-version>"
    // OR implementation "com.iamkaf.amber:amber-neoforge:<amber-version>+<mc-version>"
}
```

### 2. Initialize Amber

```java
public class YourMod {
    public static final String MOD_ID = "yourmod";

    public static void init() {
        AmberInitializer.initialize(MOD_ID);
        // Your mod initialization code here
    }
}
```

## Documentation

- **[Getting Started Guide](/v9/guide/getting-started)** - Set up your first Amber mod
- **[API Reference](/v9/api/core)** - Detailed API documentation
- **[Systems](/v9/systems/)** - Core system documentation
  - **[Registry](/v9/systems/registry)** - Object registration
  - **[Events](/v9/systems/events)** - Event handling
  - **[Commands](/v9/systems/commands)** - Command registration
  - **[Networking](/v9/systems/networking)** - Network communication
- **[Best Practices](/v9/advanced/best-practices)** - Tips for effective modding
