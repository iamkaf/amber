# Getting Started

This guide will walk you through the process of setting up Amber in your modding project. In just a few steps, you'll have a multiloader environment ready to go.

---

## 1. Add Amber to Your Project

First, you need to add the Amber repository and dependencies to your `build.gradle` file.

### Add the Maven Repository

Add the following to your `repositories` block:
```gradle
repositories {
    // ... other repositories
    maven {
        name = 'Amber Maven'
        url = 'https://raw.githubusercontent.com/iamkaf/modresources/main/maven/'
    }
}
```

### Add the Dependencies

For a multiloader project, add the following dependencies to the appropriate modules:

```gradle
// In your common module's build.gradle
dependencies {
    implementation "com.iamkaf:amber-common:6.0.10+1.21.7"
}

// In your fabric module's build.gradle
dependencies {
    implementation "com.iamkaf:amber-fabric:6.0.10+1.21.7"
}

// In your forge module's build.gradle
dependencies {
    implementation "com.iamkaf:amber-forge:6.0.10+1.21.7"
}

// In your neoforge module's build.gradle
dependencies {
    implementation "com.iamkaf:amber-neoforge:6.0.10+1.21.7"
}
```

---

## 2. Initialize Amber

In your common module, create a main class for your mod and initialize Amber.

```java
// In your common module (e.g., src/main/java/com/example/mymod/MyMod.java)
package com.example.mymod;

import com.iamkaf.amber.api.core.v2.AmberInitializer;

public class MyMod {
    public static final String MOD_ID = "mymod";

    public static void init() {
        // Initialize Amber
        AmberInitializer.initialize(MOD_ID);

        System.out.println("MyMod has been initialized with Amber!");

        // Your other initialization code (registries, events, etc.) goes here
    }
}
```

### Call `init()` from Each Platform

Finally, call your common `MyMod.init()` method from each of your platform-specific entry points.

**Fabric (`onInitialize`):**
```java
public class MyModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MyMod.init();
    }
}
```

**Forge/NeoForge (Constructor):**
```java
@Mod(MyMod.MOD_ID)
public class MyModForge {
    public MyModForge() {
        MyMod.init();
    }
}
```

---

## What's Next?

Your project is now set up with Amber! You can start building your mod using Amber's cross-platform APIs.

Here are the next topics you should explore:

-   **[Configuration System](./configuration.md)**: Learn how to manage your mod's configuration with ease.
-   **[Event System](./events.md)**: Discover how to listen and react to game events.
-   **[Registry System](./registry.md)**: See how to register your items, blocks, and more.
-   **[Networking](./networking/README.md)**: Dive into Amber's type-safe networking system.