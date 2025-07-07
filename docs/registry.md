# Registry System

Amber provides a unified registry system that works across all mod loaders, allowing you to register items, blocks, entities, and other game objects with a consistent API.

## Overview

The registry system uses deferred registration, which means your objects are registered at the appropriate time during mod loading, regardless of the platform. This ensures compatibility across Fabric, Forge, and NeoForge.

## Key Components

- **DeferredRegister**: Manages registration for a specific registry type
- **RegistrySupplier**: Provides lazy access to registered objects
- **Resource Keys**: Modern approach to object identification

## Basic Usage

### Setting Up Deferred Registers

Create deferred registers for each type of object you want to register:

```java
import com.iamkaf.amber.api.registry.v1.DeferredRegister;
import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import net.minecraft.core.registries.Registries;

public class MyRegistries {
    public static final String MOD_ID = "mymod";
    
    // Deferred registers for different object types
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(MOD_ID, Registries.ITEM);
    
    public static final DeferredRegister<Block> BLOCKS = 
        DeferredRegister.create(MOD_ID, Registries.BLOCK);
    
    public static final DeferredRegister<EntityType<?>> ENTITIES = 
        DeferredRegister.create(MOD_ID, Registries.ENTITY_TYPE);
    
    public static final DeferredRegister<SoundEvent> SOUNDS = 
        DeferredRegister.create(MOD_ID, Registries.SOUND_EVENT);
    
    // Initialize all registers
    public static void init() {
        ITEMS.register();
        BLOCKS.register();
        ENTITIES.register();
        SOUNDS.register();
    }
}
```

### Registering Items

Register various types of items:

```java
public class MyItems {
    // Basic item
    public static final RegistrySupplier<Item> RUBY = MyRegistries.ITEMS.register("ruby",
        () -> new Item(new Item.Properties()));
    
    // Tool with custom properties
    public static final RegistrySupplier<SwordItem> RUBY_SWORD = MyRegistries.ITEMS.register("ruby_sword",
        () -> new SwordItem(
            MyTiers.RUBY,           // Custom tier
            3,                      // Attack damage
            -2.4F,                  // Attack speed
            new Item.Properties()
        ));
    
    // Food item
    public static final RegistrySupplier<Item> MAGIC_APPLE = MyRegistries.ITEMS.register("magic_apple",
        () -> new Item(new Item.Properties()
            .food(Foods.GOLDEN_APPLE)
            .stacksTo(16)));
    
    // Block item (automatically creates item for block)
    public static final RegistrySupplier<BlockItem> RUBY_BLOCK_ITEM = MyRegistries.ITEMS.register("ruby_block",
        () -> new BlockItem(MyBlocks.RUBY_BLOCK.get(), new Item.Properties()));
    
    // Custom item class
    public static final RegistrySupplier<MagicWandItem> MAGIC_WAND = MyRegistries.ITEMS.register("magic_wand",
        () -> new MagicWandItem(new Item.Properties()
            .stacksTo(1)
            .durability(500)));
}
```

### Registering Blocks

Register blocks with various properties:

```java
public class MyBlocks {
    // Basic block
    public static final RegistrySupplier<Block> RUBY_BLOCK = MyRegistries.BLOCKS.register("ruby_block",
        () -> new Block(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_RED)
            .strength(5.0F, 6.0F)
            .sound(SoundType.METAL)));
    
    // Ore block
    public static final RegistrySupplier<Block> RUBY_ORE = MyRegistries.BLOCKS.register("ruby_ore",
        () -> new DropExperienceBlock(
            UniformInt.of(3, 7),    // XP drop range
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(3.0F, 3.0F)
                .requiresCorrectToolForDrops()));
    
    // Custom block with special behavior
    public static final RegistrySupplier<MagicCraftingTableBlock> MAGIC_CRAFTING_TABLE = 
        MyRegistries.BLOCKS.register("magic_crafting_table",
            () -> new MagicCraftingTableBlock(BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .strength(2.5F)
                .sound(SoundType.WOOD)));
    
    // Stairs and slabs
    public static final RegistrySupplier<StairBlock> RUBY_STAIRS = MyRegistries.BLOCKS.register("ruby_stairs",
        () -> new StairBlock(
            RUBY_BLOCK.get().defaultBlockState(),
            BlockBehaviour.Properties.copy(RUBY_BLOCK.get())));
    
    public static final RegistrySupplier<SlabBlock> RUBY_SLAB = MyRegistries.BLOCKS.register("ruby_slab",
        () -> new SlabBlock(BlockBehaviour.Properties.copy(RUBY_BLOCK.get())));
}
```

### Registering Entities

Register custom entity types:

```java
public class MyEntities {
    // Custom mob
    public static final RegistrySupplier<EntityType<RubyGolemEntity>> RUBY_GOLEM = 
        MyRegistries.ENTITIES.register("ruby_golem",
            () -> EntityType.Builder.of(RubyGolemEntity::new, MobCategory.CREATURE)
                .sized(0.6F, 1.95F)  // Width, Height
                .clientTrackingRange(8)
                .updateInterval(3)
                .build("ruby_golem"));
    
    // Projectile
    public static final RegistrySupplier<EntityType<MagicBoltEntity>> MAGIC_BOLT = 
        MyRegistries.ENTITIES.register("magic_bolt",
            () -> EntityType.Builder.<MagicBoltEntity>of(MagicBoltEntity::new, MobCategory.MISC)
                .sized(0.25F, 0.25F)
                .clientTrackingRange(4)
                .updateInterval(10)
                .build("magic_bolt"));
}
```

### Registering Sounds

Register custom sound events:

```java
public class MySounds {
    public static final RegistrySupplier<SoundEvent> MAGIC_CAST = MyRegistries.SOUNDS.register("magic_cast",
        () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(MyRegistries.MOD_ID, "magic_cast")));
    
    public static final RegistrySupplier<SoundEvent> RUBY_BREAK = MyRegistries.SOUNDS.register("ruby_break",
        () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(MyRegistries.MOD_ID, "ruby_break")));
}
```

## Advanced Patterns

### Block and Item Pairs

Create blocks with their corresponding items automatically:

```java
public class BlockItemPairs {
    // Helper method for creating block-item pairs
    public static <T extends Block> RegistrySupplier<T> registerBlockWithItem(
            String name, 
            Supplier<T> blockSupplier) {
        
        // Register the block
        RegistrySupplier<T> block = MyRegistries.BLOCKS.register(name, blockSupplier);
        
        // Register the corresponding item
        MyRegistries.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        
        return block;
    }
    
    // Usage
    public static final RegistrySupplier<Block> DECORATED_RUBY_BLOCK = 
        registerBlockWithItem("decorated_ruby_block",
            () -> new Block(BlockBehaviour.Properties.copy(MyBlocks.RUBY_BLOCK.get())));
}
```

### Tool Tiers

Create custom tool tiers:

```java
public enum MyTiers implements Tier {
    RUBY(2, 500, 7.0F, 2.5F, 16, () -> Ingredient.of(MyItems.RUBY.get()));
    
    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final Supplier<Ingredient> repairIngredient;
    
    MyTiers(int level, int uses, float speed, float damage, int enchantmentValue, 
            Supplier<Ingredient> repairIngredient) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = repairIngredient;
    }
    
    @Override public int getUses() { return uses; }
    @Override public float getSpeed() { return speed; }
    @Override public float getAttackDamageBonus() { return damage; }
    @Override public int getLevel() { return level; }
    @Override public int getEnchantmentValue() { return enchantmentValue; }
    @Override public Ingredient getRepairIngredient() { return repairIngredient.get(); }
}
```

### Armor Materials

Create custom armor materials:

```java
public enum MyArmorMaterials implements ArmorMaterial {
    RUBY("ruby", 25, Map.of(
        ArmorItem.Type.BOOTS, 2,
        ArmorItem.Type.LEGGINGS, 5,
        ArmorItem.Type.CHESTPLATE, 7,
        ArmorItem.Type.HELMET, 2
    ), 16, SoundEvents.ARMOR_EQUIP_DIAMOND, 1.0F, 0.0F, 
    () -> Ingredient.of(MyItems.RUBY.get()));
    
    private final String name;
    private final int durabilityMultiplier;
    private final Map<ArmorItem.Type, Integer> protectionAmounts;
    private final int enchantmentValue;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;
    
    MyArmorMaterials(String name, int durabilityMultiplier, 
                     Map<ArmorItem.Type, Integer> protectionAmounts,
                     int enchantmentValue, SoundEvent equipSound,
                     float toughness, float knockbackResistance,
                     Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmounts = protectionAmounts;
        this.enchantmentValue = enchantmentValue;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }
    
    @Override public int getDurabilityForType(ArmorItem.Type type) {
        return BASE_DURABILITY.get(type) * durabilityMultiplier;
    }
    
    @Override public int getDefenseForType(ArmorItem.Type type) {
        return protectionAmounts.get(type);
    }
    
    @Override public int getEnchantmentValue() { return enchantmentValue; }
    @Override public SoundEvent getEquipSound() { return equipSound; }
    @Override public Ingredient getRepairIngredient() { return repairIngredient.get(); }
    @Override public String getName() { return MyMod.MOD_ID + ":" + name; }
    @Override public float getToughness() { return toughness; }
    @Override public float getKnockbackResistance() { return knockbackResistance; }
    
    private static final Map<ArmorItem.Type, Integer> BASE_DURABILITY = Map.of(
        ArmorItem.Type.BOOTS, 13,
        ArmorItem.Type.LEGGINGS, 15,
        ArmorItem.Type.CHESTPLATE, 16,
        ArmorItem.Type.HELMET, 11
    );
}
```

## Data Generation

Generate data files for your registered objects:

```java
public class MyDataGenerator {
    public static void generateRecipes(RecipeOutput output) {
        // Shaped recipe
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, MyItems.RUBY_SWORD.get())
            .pattern(" R ")
            .pattern(" R ")
            .pattern(" S ")
            .define('R', MyItems.RUBY.get())
            .define('S', Items.STICK)
            .unlockedBy("has_ruby", has(MyItems.RUBY.get()))
            .save(output);
        
        // Smelting recipe
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(MyBlocks.RUBY_ORE.get()),
                RecipeCategory.MISC,
                MyItems.RUBY.get(),
                0.7F,
                200)
            .unlockedBy("has_ruby_ore", has(MyBlocks.RUBY_ORE.get()))
            .save(output);
    }
    
    public static void generateLootTables(LootTableProvider.SubProviderEntry... entries) {
        // Block loot tables
        LootTable.Builder rubyOreTable = LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(MyItems.RUBY.get())
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                    .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
    }
}
```

## Best Practices

### 1. Organization

Organize your registries logically:

```java
// Separate classes for different object types
public class MyItems { /* item registrations */ }
public class MyBlocks { /* block registrations */ }
public class MyEntities { /* entity registrations */ }

// Or group by feature
public class MagicRegistries { /* magic-related objects */ }
public class MiningRegistries { /* mining-related objects */ }
```

### 2. Naming Conventions

Use consistent naming:

```java
// Good - consistent naming
public static final RegistrySupplier<Item> RUBY_INGOT = ITEMS.register("ruby_ingot", ...);
public static final RegistrySupplier<Block> RUBY_BLOCK = BLOCKS.register("ruby_block", ...);
public static final RegistrySupplier<Item> RUBY_SWORD = ITEMS.register("ruby_sword", ...);

// Bad - inconsistent naming
public static final RegistrySupplier<Item> rubyIngot = ITEMS.register("RubyIngot", ...);
public static final RegistrySupplier<Block> Ruby_Block = BLOCKS.register("ruby-block", ...);
```

### 3. Lazy Initialization

Always use suppliers for object creation:

```java
// Good - lazy initialization
public static final RegistrySupplier<Item> MY_ITEM = ITEMS.register("my_item",
    () -> new MyItem(new Item.Properties()));

// Bad - immediate initialization (can cause issues)
private static final MyItem ITEM_INSTANCE = new MyItem(new Item.Properties());
public static final RegistrySupplier<Item> MY_ITEM = ITEMS.register("my_item",
    () -> ITEM_INSTANCE);
```

### 4. Resource Location Handling

Let the registry system handle resource locations:

```java
// Good - let the system handle namespacing
ITEMS.register("my_item", () -> new Item(...));

// Unnecessary - the system already uses your MOD_ID
ITEMS.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "my_item").toString(), ...);
```

## Integration with Other Systems

### Creative Tabs

Add your items to creative tabs:

```java
public class MyCreativeTabs {
    public static final RegistrySupplier<CreativeModeTab> MY_TAB = 
        DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB)
            .register("my_tab", () -> CreativeModeTab.builder()
                .icon(() -> new ItemStack(MyItems.RUBY.get()))
                .title(Component.translatable("itemGroup.mymod.my_tab"))
                .displayItems((parameters, output) -> {
                    output.accept(MyItems.RUBY.get());
                    output.accept(MyItems.RUBY_SWORD.get());
                    output.accept(MyBlocks.RUBY_BLOCK.get());
                })
                .build());
}
```

### Tags

Create and use tags for your objects:

```java
public class MyTags {
    public static final TagKey<Item> RUBY_ITEMS = 
        TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "ruby_items"));
    
    public static final TagKey<Block> RUBY_BLOCKS = 
        TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MOD_ID, "ruby_blocks"));
}
```

## Platform-Specific Notes

### Fabric
- Uses Fabric's registry system internally
- Automatic registration timing
- Compatible with Fabric's data generation

### Forge/NeoForge
- Uses deferred registers internally
- Proper registration timing during mod loading
- Compatible with Forge's data generation system

The registry system abstracts these differences, providing a unified API across all platforms.

## Migration from Platform-Specific Registration

### From Fabric Registration

```java
// Old Fabric way
public static final Item MY_ITEM = Registry.register(
    Registries.ITEM, 
    new Identifier("mymod", "my_item"), 
    new Item(new Item.Properties())
);

// New Amber way
public static final RegistrySupplier<Item> MY_ITEM = ITEMS.register("my_item",
    () -> new Item(new Item.Properties()));
```

### From Forge DeferredRegister

```java
// Old Forge way
public static final DeferredRegister<Item> ITEMS = 
    DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
public static final RegistryObject<Item> MY_ITEM = ITEMS.register("my_item",
    () -> new Item(new Item.Properties()));

// New Amber way (very similar!)
public static final DeferredRegister<Item> ITEMS = 
    DeferredRegister.create(MOD_ID, Registries.ITEM);
public static final RegistrySupplier<Item> MY_ITEM = ITEMS.register("my_item",
    () -> new Item(new Item.Properties()));
```

The migration from Forge is minimal - just change the registry reference and return type!

For more information on specific object types, see the individual feature documentation.