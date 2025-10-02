# Registry System

Amber's registry system simplifies the process of registering items, blocks, and other game objects. It uses a **deferred registration** pattern, which ensures that your objects are registered at the correct time, regardless of the mod loader.

---

## 1. Create Deferred Registers

First, create a `DeferredRegister` for each type of object you want to register (e.g., items, blocks). These should be `public static final` fields in a dedicated class.

```java
import com.iamkaf.amber.api.registry.v1.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class MyRegistries {
    public static final String MOD_ID = "mymod";

    // Create a deferred register for Items
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(MOD_ID, Registries.ITEM);

    // Create a deferred register for Blocks
    public static final DeferredRegister<Block> BLOCKS =
        DeferredRegister.create(MOD_ID, Registries.BLOCK);
}
```

---

## 2. Register Your Objects

Next, use your `DeferredRegister` to register your objects. The `.register()` method returns a `RegistrySupplier`, which is a lazy reference to your registered object.

### Registering an Item
```java
public class MyItems {
    // Register a basic item
    public static final RegistrySupplier<Item> MY_ITEM = MyRegistries.ITEMS.register("my_item",
        () -> new Item(new Item.Properties()));
}
```

### Registering a Block and its Item
When registering objects that depend on each other (like a block and its `BlockItem`), use the `.get()` method on the `RegistrySupplier` to access the registered object.

```java
public class MyBlocks {
    // Register a block
    public static final RegistrySupplier<Block> MY_BLOCK = MyRegistries.BLOCKS.register("my_block",
        () -> new Block(BlockBehaviour.Properties.of().strength(2.0f)));

    // Register the block's item, depending on the block itself
    public static final RegistrySupplier<Item> MY_BLOCK_ITEM = MyRegistries.ITEMS.register("my_block",
        () -> new BlockItem(MyBlocks.MY_BLOCK.get(), new Item.Properties()));
}
```

---

## 3. Initialize the Registers

Finally, you must call the `.register()` method on each of your `DeferredRegister` instances during your mod's initialization. This is what actually registers all of your queued objects with the game.

```java
public class MyMod {
    public static void init() {
        // ...
        MyRegistries.ITEMS.register();
        MyRegistries.BLOCKS.register();
        // ...
    }
}
```

This same pattern applies to all other registrable types, such as `EntityType`, `SoundEvent`, and more. For advanced use cases, such as custom tool tiers or data generation, please refer to the Amber source code for examples.