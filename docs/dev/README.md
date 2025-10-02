# Amber Developer Documentation

Welcome to the official developer documentation for Amber, a comprehensive multiloader library for Minecraft mod development. This guide provides all the information you need to start building with Amber's powerful, cross-platform APIs.

Whether you're looking to simplify your registration process, implement a robust networking system, or manage configuration with ease, this documentation will guide you through Amber's features and best practices.

---

## Table of Contents

### Chapter 1: Getting Started

-   **[Installation & Setup](./getting-started.md)**
    A step-by-step guide to adding Amber to your development environment and initializing it in your mod.

### Chapter 2: Core Systems

-   **[Configuration](./configuration.md)**
    Learn how to use Amber's JSON-based configuration system to manage your mod's settings with automatic serialization.

-   **[Events](./events.md)**
    An overview of Amber's Fabric-inspired event system that works seamlessly across all supported mod loaders.

-   **[Networking](./networking/README.md)**
    A deep dive into Amber's type-safe networking system for client-server communication, including patterns and examples.

-   **[Registry](./registry.md)**
    Discover how to use Amber's deferred registration system to register items, blocks, and other game elements in a clean and efficient way.

---

## Architecture Overview

Amber follows a service-based architecture that abstracts platform-specific implementations behind a common API. This allows you to write your mod's logic once and have it run on Fabric, Forge, and NeoForge without any changes.

```
┌─────────────────┐
│     Your Mod    │
├─────────────────┤
│  Amber Common   │
├─────────────────┤
│  Amber Loader   │ (e.g., Fabric, Forge)
├─────────────────┤
│    Mod Loader   │ (e.g., Fabric, Forge)
└─────────────────┘
```

This design ensures that your mod interacts only with the `Amber Common` module, while the platform-specific `Amber Loader` module handles the communication with the underlying mod loader (Fabric, Forge, or NeoForge).