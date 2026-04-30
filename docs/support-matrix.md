# Support Matrix

Amber's supported loader matrix is defined by `versions/*/gradle.properties` and exposed by `just list-nodes`.

## Support Bands

| Minecraft line | Support band | Enabled loaders |
| --- | --- | --- |
| `1.14.4` | EOL | Fabric |
| `1.15` | EOL | Fabric |
| `1.15.1` | EOL | Fabric |
| `1.15.2` | EOL | Fabric |
| `1.16` | EOL | Fabric |
| `1.16.1` | EOL | Fabric |
| `1.16.2` | EOL | Fabric |
| `1.16.3` | EOL | Fabric |
| `1.16.4` | EOL | Fabric |
| `1.16.5` | EOL | Fabric, Forge |
| `1.17` | Legacy Supported | Fabric |
| `1.17.1` | Legacy Supported | Fabric, Forge |
| `1.18` | Legacy Supported | Fabric, Forge |
| `1.18.1` | Legacy Supported | Fabric, Forge |
| `1.18.2` | Legacy Supported | Fabric, Forge |
| `1.19` | Legacy Supported | Fabric, Forge |
| `1.19.1` | Legacy Supported | Fabric, Forge |
| `1.19.2` | Legacy Supported | Fabric, Forge |
| `1.19.3` | Legacy Supported | Fabric, Forge |
| `1.19.4` | Legacy Supported | Fabric, Forge |
| `1.20` | Modern Supported | Fabric, Forge |
| `1.20.1` | Modern Supported | Fabric, Forge |
| `1.20.2` | Modern Supported | Fabric, Forge |
| `1.20.3` | Modern Supported | Fabric, Forge |
| `1.20.4` | Modern Supported | Fabric, Forge |
| `1.20.5` | Modern Supported | Fabric |
| `1.20.6` | Modern Supported | Fabric, Forge |
| `1.21` | Modern Supported | Fabric, Forge, NeoForge |
| `1.21.1` | Modern Supported | Fabric, Forge, NeoForge |
| `1.21.2` | Modern Supported | Fabric, NeoForge |
| `1.21.3` | Modern Supported | Fabric, Forge, NeoForge |
| `1.21.4` | Modern Supported | Fabric, Forge, NeoForge |
| `1.21.5` | Modern Supported | Fabric, Forge, NeoForge |
| `1.21.6` | Modern Supported | Fabric, Forge, NeoForge |
| `1.21.7` | Modern Supported | Fabric, Forge, NeoForge |
| `1.21.8` | Modern Supported | Fabric, Forge, NeoForge |
| `1.21.9` | Modern Supported | Fabric, Forge, NeoForge |
| `1.21.10` | Modern Supported | Fabric, Forge, NeoForge |
| `1.21.11` | Modern Supported | Fabric, Forge, NeoForge |
| `26.1` | Modern Supported | Fabric, Forge, NeoForge |
| `26.1.1` | Modern Supported | Fabric, Forge, NeoForge |
| `26.1.2` | Modern Supported | Fabric, Forge, NeoForge |

## Coordinates

Amber artifacts use the Minecraft line as the version suffix:

```gradle
implementation "com.iamkaf.amber:amber-common:<amber-version>+<mc-version>"
implementation "com.iamkaf.amber:amber-fabric:<amber-version>+<mc-version>"
implementation "com.iamkaf.amber:amber-forge:<amber-version>+<mc-version>"
implementation "com.iamkaf.amber:amber-neoforge:<amber-version>+<mc-version>"
```

Use only the loader artifacts enabled for the target Minecraft line.

## Verification Status

Phase 1 establishes the support contract and compile-only test mod. Runtime feature parity probes are planned but not yet implemented.
