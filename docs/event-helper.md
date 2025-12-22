# EventHelper

Simplified event registration and management utilities with automatic error handling and batch registration support.

## Features

- Automatic error handling for event registration
- Batch registration for cleaner code
- Logging integration
- Type-safe event registration
- Fluent API for multiple events

## Usage

### Simple Event Registration

```java
EventHelper.register(
    ServerLifecycleEvents.SERVER_STARTED,
    server -> {
        LunarLogger.info(MOD_ID, "Server started!");
    },
    MOD_ID
);
```

### Batch Registration (Recommended)

```java
EventHelper.forMod(MOD_ID)
    .on(ServerLifecycleEvents.SERVER_STARTED, server -> {
        LunarLogger.info(MOD_ID, "Server started");
    })
    .on(ServerLifecycleEvents.SERVER_STOPPING, server -> {
        LunarLogger.info(MOD_ID, "Server stopping");
    })
    .on(ServerTickEvents.END_SERVER_TICK, server -> {
        // Tick logic
    })
    .registerAll();
```

## API Reference

### `register(Event<T> event, T listener, String modId)`

Register a single event listener with error handling.

**Parameters:**
- `event` - The Fabric event to listen to
- `listener` - The listener callback
- `modId` - The mod ID (for logging)

**Example:**
```java
EventHelper.register(
    ServerLifecycleEvents.SERVER_STARTED,
    server -> {
        // Server started logic
    },
    "mymod"
);
```

### `register(Event<T> event, T listener)`

Register a single event listener without mod ID.

**Parameters:**
- `event` - The Fabric event to listen to
- `listener` - The listener callback

**Note:** Uses "unknown" as mod ID for logging

**Example:**
```java
EventHelper.register(
    ServerLifecycleEvents.SERVER_STARTED,
    server -> {
        // Server started logic
    }
);
```

### `forMod(String modId)`

Create an EventRegistrar for batch registration.

**Parameters:**
- `modId` - The mod ID

**Returns:** A new EventRegistrar instance

**Example:**
```java
EventHelper.EventRegistrar registrar = EventHelper.forMod("mymod");
```

## EventRegistrar

The EventRegistrar provides a fluent API for registering multiple events.

### `on(Event<T> event, T listener)`

Add an event registration to the batch.

**Parameters:**
- `event` - The event to register
- `listener` - The listener callback

**Returns:** This registrar for chaining

**Example:**
```java
registrar.on(ServerLifecycleEvents.SERVER_STARTED, server -> {
    // Logic here
});
```

### `registerAll()`

Execute all registered event registrations.

**Example:**
```java
registrar.registerAll();
```

## Best Practices

### 1. Use Batch Registration for Multiple Events

```java
// GOOD - Clean and organized
EventHelper.forMod(MOD_ID)
    .on(ServerLifecycleEvents.SERVER_STARTED, this::onServerStart)
    .on(ServerLifecycleEvents.SERVER_STOPPING, this::onServerStop)
    .on(ServerTickEvents.END_SERVER_TICK, this::onTick)
    .registerAll();

// LESS GOOD - Repetitive
EventHelper.register(ServerLifecycleEvents.SERVER_STARTED, 
    this::onServerStart, MOD_ID);
EventHelper.register(ServerLifecycleEvents.SERVER_STOPPING, 
    this::onServerStop, MOD_ID);
EventHelper.register(ServerTickEvents.END_SERVER_TICK, 
    this::onTick, MOD_ID);
```

### 2. Use Method References for Cleaner Code

```java
EventHelper.forMod(MOD_ID)
    .on(ServerLifecycleEvents.SERVER_STARTED, this::handleServerStart)
    .on(ServerLifecycleEvents.SERVER_STOPPING, this::handleServerStop)
    .registerAll();

private void handleServerStart(MinecraftServer server) {
    LunarLogger.info(MOD_ID, "Server started");
}

private void handleServerStop(MinecraftServer server) {
    LunarLogger.info(MOD_ID, "Server stopping");
}
```

### 3. Register Events During Initialization

```java
@Override
public void onInitialize() {
    LifecycleManager.onInitialize(() -> {
        registerEvents();
    });
}

private void registerEvents() {
    EventHelper.forMod(MOD_ID)
        .on(ServerLifecycleEvents.SERVER_STARTED, this::onServerStart)
        .on(PlayerBlockBreakEvents.BEFORE, this::onBlockBreak)
        .registerAll();
}
```

### 4. Keep Event Handlers Simple

```java
EventHelper.forMod(MOD_ID)
    .on(ServerLifecycleEvents.SERVER_STARTED, server -> {
        // Keep logic minimal
        TaskScheduler.runLater(MOD_ID, 20, () -> {
            // Do expensive work later
            initializeComplexSystems();
        });
    })
    .registerAll();
```

## Common Patterns

### Server Lifecycle Events

```java
EventHelper.forMod(MOD_ID)
    .on(ServerLifecycleEvents.SERVER_STARTING, server -> {
        LunarLogger.info(MOD_ID, "Server starting");
    })
    .on(ServerLifecycleEvents.SERVER_STARTED, server -> {
        LunarLogger.info(MOD_ID, "Server started");
        TaskScheduler.setServer(server);
    })
    .on(ServerLifecycleEvents.SERVER_STOPPING, server -> {
        LunarLogger.info(MOD_ID, "Server stopping");
        saveData();
    })
    .on(ServerLifecycleEvents.SERVER_STOPPED, server -> {
        LunarLogger.info(MOD_ID, "Server stopped");
    })
    .registerAll();
```

### Player Events

```java
EventHelper.forMod(MOD_ID)
    .on(ServerPlayConnectionEvents.JOIN, (handler, sender, server) -> {
        String playerName = handler.getPlayer().getName().getString();
        LunarLogger.info(MOD_ID, "Player joined: {}", playerName);
    })
    .on(ServerPlayConnectionEvents.DISCONNECT, (handler, server) -> {
        String playerName = handler.getPlayer().getName().getString();
        LunarLogger.info(MOD_ID, "Player left: {}", playerName);
    })
    .registerAll();
```

### Server Tick Events

```java
EventHelper.forMod(MOD_ID)
    .on(ServerTickEvents.START_SERVER_TICK, server -> {
        // Beginning of tick
    })
    .on(ServerTickEvents.END_SERVER_TICK, server -> {
        // End of tick
        TaskScheduler.tick(); // If using TaskScheduler
    })
    .registerAll();
```

### World Events

```java
EventHelper.forMod(MOD_ID)
    .on(ServerWorldEvents.LOAD, (server, world) -> {
        LunarLogger.info(MOD_ID, "World loaded: {}", 
            world.getRegistryKey().getValue());
    })
    .on(ServerWorldEvents.UNLOAD, (server, world) -> {
        LunarLogger.info(MOD_ID, "World unloaded: {}", 
            world.getRegistryKey().getValue());
    })
    .registerAll();
```

### Command Registration

```java
EventHelper.forMod(MOD_ID)
    .on(CommandRegistrationCallback.EVENT, (dispatcher, registryAccess, environment) -> {
        // Register your commands
        MyCommands.register(dispatcher);
    })
    .registerAll();
```

### Resource Reload

```java
EventHelper.forMod(MOD_ID)
    .on(ResourceManagerHelper.get(ResourceType.SERVER_DATA).getRegisterReloadListenerEvent(),
        (resourceManager, listener) -> {
            // Register reload listener
        })
    .registerAll();
```

## Error Handling

EventHelper automatically catches and logs exceptions during registration:

```java
EventHelper.forMod(MOD_ID)
    .on(SomeEvent.EVENT, listener -> {
        // If this throws an exception, it's caught and logged
        // Other event registrations will still proceed
        riskyOperation();
    })
    .registerAll();
```

## Integration with Other Systems

### With LifecycleManager

```java
LifecycleManager.onInitialize(() -> {
    EventHelper.forMod(MOD_ID)
        .on(ServerLifecycleEvents.SERVER_STARTED, server -> {
            LunarLogger.info(MOD_ID, "Server started");
        })
        .registerAll();
});
```

### With TaskScheduler

```java
EventHelper.forMod(MOD_ID)
    .on(ServerLifecycleEvents.SERVER_STARTED, server -> {
        TaskScheduler.runLater(MOD_ID, 100, () -> {
            LunarLogger.info(MOD_ID, "Delayed startup task");
        });
    })
    .registerAll();
```

### With Config

```java
private Config config;

EventHelper.forMod(MOD_ID)
    .on(ServerLifecycleEvents.SERVER_STARTED, server -> {
        config = new Config(MOD_ID);
        config.load();
    })
    .on(ServerLifecycleEvents.SERVER_STOPPING, server -> {
        config.save();
    })
    .registerAll();
```

## Common Fabric Events

Here are some commonly used Fabric events you can register with EventHelper:

### Lifecycle
- `ServerLifecycleEvents.SERVER_STARTING`
- `ServerLifecycleEvents.SERVER_STARTED`
- `ServerLifecycleEvents.SERVER_STOPPING`
- `ServerLifecycleEvents.SERVER_STOPPED`

### Tick
- `ServerTickEvents.START_SERVER_TICK`
- `ServerTickEvents.END_SERVER_TICK`
- `ServerTickEvents.START_WORLD_TICK`
- `ServerTickEvents.END_WORLD_TICK`

### Player
- `ServerPlayConnectionEvents.JOIN`
- `ServerPlayConnectionEvents.DISCONNECT`
- `ServerPlayerEvents.AFTER_RESPAWN`
- `ServerPlayerEvents.COPY_FROM`

### World
- `ServerWorldEvents.LOAD`
- `ServerWorldEvents.UNLOAD`

### Entity
- `ServerEntityEvents.ENTITY_LOAD`
- `ServerEntityEvents.ENTITY_UNLOAD`
- `ServerLivingEntityEvents.AFTER_DEATH`
- `ServerLivingEntityEvents.ALLOW_DAMAGE`

### Block
- `PlayerBlockBreakEvents.BEFORE`
- `PlayerBlockBreakEvents.AFTER`
- `AttackBlockCallback.EVENT`
- `UseBlockCallback.EVENT`

### Commands
- `CommandRegistrationCallback.EVENT`

## Thread Safety

- Event registration is thread-safe
- However, register events during initialization (main thread)
- Event handlers run on the game thread
- Don't access shared state without proper synchronization

## Performance

- Minimal overhead for event registration
- Error handling has negligible impact
- Logging is efficient (only if error occurs)
- Batch registration has same performance as individual registration

## Notes

- Events are registered immediately, not deferred
- Failed registrations are logged but don't prevent other registrations
- The mod ID is used for logging purposes only
- EventHelper doesn't create wrapper objects - it directly calls Fabric's event registration
- You can mix EventHelper with direct Fabric event registration

## See Also

- [LifecycleManager](lifecycle-manager.md) - For initialization callbacks
- [TaskScheduler](task-scheduler.md) - For delayed/scheduled tasks
- [LunarLogger](logger.md) - For logging
- [Fabric API Events](https://fabricmc.net/wiki/tutorial:events) - Full event list

