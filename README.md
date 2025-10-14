# Amber

<div align="center">

**A comprehensive multiloader library for Minecraft mod development**

_Write once, run everywhere - Unified APIs for Fabric, Forge, and NeoForge_

[![Amber](https://img.shields.io/badge/Amber-iamkaf?style=for-the-badge&label=Requires&color=%23ebb134)](https://modrinth.com/mod/amber)
[![Discord](https://img.shields.io/discord/1207469438719492176?style=for-the-badge&logo=discord&label=DISCORD&color=%235865F2)](https://discord.gg/HV5WgTksaB)
[![KoFi](https://img.shields.io/badge/KoFi-iamkaf?style=for-the-badge&logo=kofi&logoColor=%2330d1e3&label=Support%20Me&color=%2330d1e3)](https://ko-fi.com/iamkaffe)

</div>

---

## What is Amber?

Amber is a foundation library that provides **unified APIs** for common modding tasks across **Fabric**, **Forge**, and **NeoForge** platforms. Built with cross-platform compatibility in mind, Amber eliminates the complexity of managing platform-specific code.

### Key Features

- Cross-Platform Compatibility - Write once, run everywhere
- Comprehensive Toolkit - Registry system, event handling, configuration, networking
- Game Integration - Commands, HUD rendering, keybinds, sound system
- Developer Utilities - Item helpers, world interaction, player feedback

---

## Documentation

For comprehensive documentation, guides, and API references, please visit:

**[https://iamkaf.github.io/amber/](https://iamkaf.github.io/amber/)**

The documentation includes getting started guides, API documentation, system guides, and best practices.

---

## Quick Start

### For End Users

Just install Amber and any mods that require it will work automatically!

### For Developers

**Add to your project:**

```gradle
repositories {
    maven { url = 'https://raw.githubusercontent.com/iamkaf/modresources/main/maven/' }
}

dependencies {
    implementation "com.iamkaf:amber-common:${amber_version}"
    implementation "com.iamkaf:amber-fabric:${amber_version}"
}
```

**Initialize in your mod:**

```java
public class YourMod {
    public static final String MOD_ID = "yourmod";

    public static void init() {
        AmberInitializer.initialize(MOD_ID);
        System.out.println("YourMod initialized with Amber!");
    }
}
```

---

## Maintained Versions

| Status     | Minecraft Version        | Support Level               |
| ---------- | ------------------------ | --------------------------- |
| Active     | Latest Minecraft Version | Full support + new features |
| Maintained | 1.21.1                   | Bug fixes only              |
| Maintained | 1.20.1                   | Bug fixes only              |

---

## FAQ

<details>
<summary><strong>Q: Can I include Amber in my modpack?</strong></summary>
**A:** Yes! No need to give credit or ask permission.
</details>

<details>
<summary><strong>Q: I found a bug or have a feature request</strong></summary>
**A:** Please report it on [GitHub Issues](https://github.com/iamkaf/mod-issues) or join our [Discord](https://discord.gg/HV5WgTksaB)!
</details>

---

## Community & Support

<div align="center">

[![Join our Discord](https://raw.githubusercontent.com/iamkaf/modresources/refs/heads/main/pages/common/discord.png)](https://discord.gg/HV5WgTksaB)

**Join our community for support, updates, and to connect with other developers**

</div>

---

<div align="center">

**Made with care by [iamkaf](https://iamkaf.com)**

_Amber - Lighting up Minecraft mod development since 2024_

</div>
