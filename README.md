# 🟨 Amber

<div align="center">

**A comprehensive multiloader library for Minecraft mod development**

*Write once, run everywhere - Unified APIs for Fabric, Forge, and NeoForge*

[![Amber](https://img.shields.io/badge/Amber-iamkaf?style=for-the-badge&label=Requires&color=%23ebb134)](https://modrinth.com/mod/amber)
[![Issues](https://img.shields.io/github/issues/iamkaf/mod-issues?style=for-the-badge&color=%23eee)](https://github.com/iamkaf/mod-issues)
[![Discord](https://img.shields.io/discord/1207469438719492176?style=for-the-badge&logo=discord&label=DISCORD&color=%235865F2)](https://discord.gg/HV5WgTksaB)
[![KoFi](https://img.shields.io/badge/KoFi-iamkaf?style=for-the-badge&logo=kofi&logoColor=%2330d1e3&label=Support%20Me&color=%2330d1e3)](https://ko-fi.com/iamkaffe)

</div>

---

## 🎯 What is Amber?

Amber is a powerful foundation library that provides **unified APIs** for common modding tasks across **Fabric**, **Forge**, and **NeoForge** platforms. Built with cross-platform compatibility in mind, Amber eliminates the complexity of managing platform-specific code while offering modern, easy-to-use APIs for Minecraft mod development.

### ✨ Key Features

<table>
<tr>
<td width="50%">

**🌐 Cross-Platform Compatibility**
- Write once, run everywhere
- Unified APIs across all major mod loaders
- Automatic platform abstraction
- Modern Minecraft 1.21.7 support

</td>
<td width="50%">

**🔧 Comprehensive Toolkit**
- Registry system for items, blocks, entities
- Event handling with Fabric-inspired API
- JSON configuration management
- Type-safe networking system

</td>
</tr>
<tr>
<td width="50%">

**🎮 Game Integration**
- Command registration and handling
- Client-side HUD rendering utilities
- Cross-platform keybind system
- Sound system with custom parameters

</td>
<td width="50%">

**🛠️ Developer Utilities**
- Item helpers and tooltips
- World interaction utilities
- Player feedback systems
- Math and probability utilities

</td>
</tr>
</table>

---

## 🚀 Quick Start

### For End Users

Just install Amber and any mods that require it will work automatically! No additional configuration needed.

### For Developers

**Add to your project:**

```gradle
repositories {
    maven { url = 'https://raw.githubusercontent.com/iamkaf/modresources/main/maven/' }
}

dependencies {
    // Common
    implementation "com.iamkaf:amber-common:6.0.10+1.21.7"
    
    // Platform-specific (choose one)
    implementation "com.iamkaf:amber-fabric:6.0.10+1.21.7"
    implementation "com.iamkaf:amber-forge:6.0.10+1.21.7"
    implementation "com.iamkaf:amber-neoforge:6.0.10+1.21.7"
}
```

**Initialize in your mod:**

```java
public class YourMod {
    public static final String MOD_ID = "yourmod";
    
    public static void init() {
        // Initialize with Amber - automatically detects mod name and version
        AmberInitializer.initialize(MOD_ID);
        
        // Your mod initialization code here
        System.out.println("YourMod initialized with Amber!");
    }
}
```

📖 **[Full Documentation](docs/README.md)** | 🏁 **[Getting Started Guide](docs/getting-started.md)**

---

## 🏗️ Architecture

Amber follows a service-based architecture that abstracts platform differences:

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

---

## 📋 Maintained Versions

<div align="center">

| Status | Minecraft Version | Support Level |
|--------|------------------|---------------|
| 🟢 **Active** | 1.21.7 (Latest) | Full support + new features |
| 🟡 **Maintained** | 1.21.1 | Bug fixes only |
| 🟡 **Maintained** | 1.20.1 | Bug fixes only |
| 🔴 **Legacy** | Older versions | No support |

</div>

---

## ❓ Frequently Asked Questions

<details>
<summary><strong>Q: Can I include Amber in my modpack?</strong></summary>

**A:** Yes! No need to give credit or ask permission. Amber is designed to be freely distributed.
</details>

<details>
<summary><strong>Q: Can you port Amber to [MC version/Mod loader]?</strong></summary>

**A:** If enough people request it, I'll consider it! Keep in mind this is a one-person project, so it might take time.
</details>

<details>
<summary><strong>Q: I found a bug or have a feature request</strong></summary>

**A:** Please report it on [GitHub Issues](https://github.com/iamkaf/mod-issues) or join our [Discord](https://discord.gg/HV5WgTksaB)!
</details>

<details>
<summary><strong>Q: Is Amber compatible with other mods?</strong></summary>

**A:** Yes! Amber is designed to be compatible with other mods. If you find compatibility issues, please let us know.
</details>

---

## 🤝 Community & Support

<div align="center">

[![Join our Discord](https://raw.githubusercontent.com/iamkaf/modresources/refs/heads/main/pages/common/discord.png)](https://discord.gg/HV5WgTksaB)

**Join our community for:**
- 💬 Support and help
- 📢 Update announcements  
- 🎮 Showcase your creations
- 🤝 Connect with other developers

</div>

---

## 🙏 Credits & Acknowledgements

- **Architectury** - For the inspiration and architectural patterns
- **Fabric API** - Event system derived under [Apache 2.0 License](https://github.com/FabricMC/fabric/blob/1.21.4/LICENSE)
- **Aris** - For always being there ❤️
- **Community** - For feedback, testing, and support

---

<div align="center">

**Made with ❤️ by [iamkaf](https://github.com/iamkaf)**

*Amber - Lighting up Minecraft mod development since 2024*

</div>