# Mochila

A colorful backpack mod for Minecraft with multiple tiers and customization options.

![License](https://img.shields.io/badge/license-MIT-blue.svg)

## 🎒 About

Mochila (Spanish for "backpack") adds customizable backpacks to Minecraft with different material tiers and 16 color variants each. Built using a multi-loader architecture supporting Fabric, Forge, and NeoForge.

## 📦 Features

- **5 Material Tiers**: Leather, Iron, Gold, Diamond, and Netherite
- **Special Variant**: Ender Backpack with unique functionality
- **16 Colors**: All standard Minecraft dye colors available for each tier
- **85 Total Combinations**: Mix and match materials and colors
- **Quality of Life**:
  - Quick stash functionality
  - Backpack upgrading system
  - Color customization via crafting
  - Keybind support for quick access

## 🗂️ Monorepo Structure

This repository contains all Minecraft versions of Mochila:

```
mochila2/
├── 1.21.11/          # Minecraft 1.21.11 version (old format)
│   ├── common/       # Shared code across loaders
│   ├── fabric/       # Fabric-specific implementation
│   ├── forge/        # Forge-specific implementation
│   └── neoforge/     # NeoForge-specific implementation
├── 26.1/             # Minecraft 26.1 version (new year-based format)
├── 27.0/             # Future versions...
└── README.md         # This file
```

## 🚀 Supported Versions

| Minecraft Version | Status | Directory | Format |
|-------------------|--------|-----------|--------|
| 1.21.10           | ✅ Active | `1.21.10/` | Legacy (1.x.x) |
| 1.21.11           | ✅ Active | `1.21.11/` | Legacy (1.x.x) |

*Note: Starting with Minecraft 26.x, versions use year-based format (e.g., 26.1, 27.0)*

## 🛠️ Building

Use `just` from the repo root as the command runner. It can target a single version or run across all versions.

```bash
# Build all loaders for a specific version
just build 1.21.11

# Build all loaders across all versions
just build

# Build a specific loader for a specific version
just run 1.21.11 :fabric:build
just run 1.21.11 :forge:build
just run 1.21.11 :neoforge:build

# Run tests
just test 1.21.11
just test

# Build only changed versions (vs. origin/main)
just build-changed
```

Built jars will be in `<version>/<loader>/build/libs/`

## 💻 Development

### Prerequisites

- Java 21 or higher
- Git
- just (install: `https://github.com/casey/just`)

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/iamkaf/mochila2.git
   cd mochila2
   ```

2. Open the specific version directory in your IDE:
   ```bash
   # Open 1.21.11 in IntelliJ IDEA, for example
   idea 1.21.11
   ```

3. The IDE should automatically detect the Gradle project and configure itself.

### Project Structure

Each version follows the same structure:

- **common/**: Shared code for all loaders
  - Core mod logic
  - Items, recipes, networking
  - Data generation
  
- **fabric/**: Fabric-specific code
  - Mod initialization
  - Platform services implementation
  
- **forge/**: Forge-specific code
  - Mod initialization  
  - Platform services implementation
  
- **neoforge/**: NeoForge-specific code
  - Mod initialization
  - Platform services implementation

### Adding a New Version

When a new Minecraft version is released:

1. Copy the closest existing version:
   ```bash
   # For year-based versions (26.x, 27.x, etc.)
   cp -r 1.21.11 26.1
   
   # Or if copying from another year-based version
   cp -r 26.1 27.0
   ```

2. Update version numbers and dependencies.

3. Test and make necessary adjustments for the new version.

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 🔗 Links

- **CurseForge**: https://www.curseforge.com/minecraft/mc-mods/mochila
- **Modrinth**: https://modrinth.com/mod/mochila
- **Issues**: [GitHub Issues](https://github.com/iamkaf/mochila2/issues)
- **Wiki**: [GitHub Wiki](https://github.com/iamkaf/mochila2/wiki)

## 👤 Author

**iamkaf**

- GitHub: [@iamkaf](https://github.com/iamkaf)

## 🙏 Acknowledgments

- Based on [jaredlll08's MultiLoader-Template](https://github.com/jaredlll08/MultiLoader-Template)
- My template at [iamkaf/template-mod](https://github.com/iamkaf/template-mod)
