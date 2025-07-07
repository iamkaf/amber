# 🔧 Multiloader Event Implementation Guide

This document provides a comprehensive guide for implementing events in Amber's multiloader system across Fabric, Forge, and NeoForge.

## 📋 **Overview**

Amber's event system provides a unified API that works consistently across all three major Minecraft modding platforms. This guide outlines the process for implementing new events and handling platform-specific differences.

## 🎯 **Implementation Process**

### **Phase 1: Analysis & Planning**

#### **1.1 Platform Event Research**
- **Verify Event Availability**: Check if equivalent events exist in all 3 loaders
- **Document Event Signatures**: Record exact event names, parameters, and behaviors
- **Identify Implementation Gaps**: Note which platforms are missing events
- **Analyze Event Timing**: Understand when events fire and their execution order

#### **1.2 Event Classification**
- **✅ Universal Events**: All platforms have equivalent events
- **⚠️ Partial Events**: Some platforms missing, but can be implemented
- **❌ Platform-Specific**: Only available on specific platforms

### **Phase 2: Implementation Strategy**

#### **2.1 Universal Events (All Platforms Available)**
```java
// 1. Define common interface in EntityEvent.java
public static final Event<EntityDeath> ENTITY_DEATH = EventFactory.createArrayBacked(
    EntityDeath.class, callbacks -> (entity, source) -> {
        for (EntityDeath callback : callbacks) {
            callback.onEntityDeath(entity, source);
        }
    }
);

// 2. Register platform-specific handlers
// Fabric: ServerLivingEntityEvents.AFTER_DEATH
// Forge: LivingDeathEvent  
// NeoForge: LivingDeathEvent
```

#### **2.2 Partial Events (Missing Platform Implementation)**
When a platform lacks a direct event equivalent:

1. **Research Other Platforms**: Study how Forge/NeoForge implement the event
2. **Identify Hook Points**: Find the appropriate game methods to hook
3. **Use Mixins**: Create Mixin classes to inject event triggers
4. **Ensure Consistency**: Match behavior and timing of other platforms

```java
// Example: Fabric missing EntitySpawn event
@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "addFreshEntity", at = @At("HEAD"))
    private void onEntitySpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        EntityEvent.ENTITY_SPAWN.invoker().onEntitySpawn(entity, this);
    }
}
```

### **Phase 3: Code Structure**

#### **3.1 Common Event Interface**
Location: `common/src/main/java/com/iamkaf/amber/api/event/v1/events/common/`

```java
public class EntityEvent {
    // Event definition
    public static final Event<EntitySpawn> ENTITY_SPAWN = EventFactory.createArrayBacked(
        EntitySpawn.class, callbacks -> (entity, level) -> {
            for (EntitySpawn callback : callbacks) {
                callback.onEntitySpawn(entity, level);
            }
        }
    );

    // Callback interface
    @FunctionalInterface
    public interface EntitySpawn {
        void onEntitySpawn(Entity entity, Level level);
    }
}
```

#### **3.2 Platform-Specific Registration**

**Fabric Implementation:**
```java
// FabricAmberEventSetup.java
ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
    EntityEvent.ENTITY_DEATH.invoker().onEntityDeath(entity, source);
});
```

**Forge Implementation:**
```java
// ForgeAmberEventSetup.java
LivingDeathEvent.BUS.addListener(EventHandlerCommon::onLivingDeath);

@SubscribeEvent(priority = Priority.HIGH)
public static void onLivingDeath(LivingDeathEvent event) {
    EntityEvent.ENTITY_DEATH.invoker().onEntityDeath(event.getEntity(), event.getSource());
}
```

**NeoForge Implementation:**
```java
// NeoForgeAmberEventSetup.java
@SubscribeEvent(priority = EventPriority.HIGH)
public static void onLivingDeath(LivingDeathEvent event) {
    EntityEvent.ENTITY_DEATH.invoker().onEntityDeath(event.getEntity(), event.getSource());
}
```

### **Phase 4: Testing & Validation**

#### **4.1 Compilation Testing**
```bash
./gradlew compileJava
```
- Must compile successfully across all platforms
- Check for any missing imports or dependencies

#### **4.2 Behavior Validation**
- **Event Timing**: Verify events fire at consistent times across platforms
- **Parameter Consistency**: Ensure event data matches across platforms
- **Performance**: Minimal overhead when events aren't used

#### **4.3 Cross-Platform Testing**
- Test event registration and firing on each platform
- Verify cancellation behavior (if applicable)
- Check event priority handling

### **Phase 5: Documentation**

#### **5.1 Code Documentation**
- **Javadoc Comments**: Document event behavior and parameters
- **Usage Examples**: Provide clear implementation examples
- **Platform Notes**: Document any platform-specific behaviors

#### **5.2 Implementation Tracking**
- Update `EVENTS_TO_IMPLEMENT.md` with completed events
- Add implementation history with technical notes
- Document any platform-specific limitations

## 🔧 **Platform-Specific Considerations**

### **Fabric**
- **Event Style**: Callback-based events with `EVENT` static fields
- **Registration**: Direct callback registration
- **Limitations**: Some events not available, may require Mixins

### **Forge**
- **Event Style**: Annotation-based event handling with `@SubscribeEvent`
- **Registration**: Event bus registration with static methods
- **Bus Types**: Distinguish between MOD bus and FORGE bus events

### **NeoForge**
- **Event Style**: Similar to Forge but with updated event names
- **Registration**: Event bus registration with static methods
- **Migration**: Most Forge events have NeoForge equivalents

## 📂 **File Structure**

```
amber/
├── common/src/main/java/com/iamkaf/amber/api/event/v1/
│   ├── events/common/
│   │   ├── EntityEvent.java          # Entity lifecycle events
│   │   ├── PlayerEvents.java         # Player interaction events
│   │   └── ...
│   └── events/client/
│       ├── HudEvents.java            # Client-side HUD events
│       └── ...
├── fabric/src/main/java/com/iamkaf/amber/platform/
│   └── FabricAmberEventSetup.java    # Fabric event registration
├── forge/src/main/java/com/iamkaf/amber/platform/
│   └── ForgeAmberEventSetup.java     # Forge event registration
└── neoforge/src/main/java/com/iamkaf/amber/platform/
    └── NeoForgeAmberEventSetup.java  # NeoForge event registration
```

## 🎯 **Best Practices**

1. **Consistent Naming**: Use consistent event and callback names across platforms
2. **Parameter Matching**: Ensure event parameters match across platforms
3. **Error Handling**: Handle platform-specific exceptions gracefully
4. **Performance**: Minimize overhead when events aren't used
5. **Documentation**: Document platform differences and limitations
6. **Testing**: Verify behavior across all platforms before release

## 🚀 **Implementation Checklist**

- [ ] Research event availability across all platforms
- [ ] Design unified callback interface
- [ ] Implement platform-specific registrations
- [ ] Add Mixin implementations for missing events (if needed)
- [ ] Test compilation across all platforms
- [ ] Validate event behavior consistency
- [ ] Document implementation and usage
- [ ] Update tracking documentation

---

*This guide ensures consistent, reliable event implementation across Amber's multiloader system.*