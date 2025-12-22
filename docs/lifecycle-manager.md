# LifecycleManager

The LifecycleManager provides a simple way to register lifecycle hooks without implementing multiple interfaces. It manages callbacks for different initialization and shutdown phases.

## Features

- Common initialization callbacks
- Client-specific initialization callbacks
- Server-specific initialization callbacks
- Shutdown callbacks (executed in reverse order)
- Automatic callback execution if phase already passed
- One-time execution guarantee

## Usage

### Basic Initialization

```java
public class YourMod implements ModInitializer {
    @Override
    public void onInitialize() {
        LifecycleManager.onInitialize(() -> {
            // This runs during common initialization
            System.out.println("Mod initializing...");
        });
    }
}
```

### Client-Specific Initialization

```java
LifecycleManager.onClientInitialize(() -> {
    // This only runs on the client
    System.out.println("Client initializing...");
});
```

### Server-Specific Initialization

```java
LifecycleManager.onServerInitialize(() -> {
    // This only runs on the dedicated server
    System.out.println("Server initializing...");
});
```

### Shutdown Callbacks

```java
LifecycleManager.onShutdown(() -> {
    // This runs when the game/server is shutting down
    // Save important data here
    config.save();
    System.out.println("Shutting down...");
});
```

## API Reference

### `onInitialize(Runnable callback)`

Register a callback to be executed during common initialization.

**Parameters:**
- `callback` - The callback to execute

**Behavior:**
- If already initialized, runs immediately
- Otherwise, queues for next initialization phase
- Callbacks run in registration order
- Each callback runs exactly once

**Example:**
```java
LifecycleManager.onInitialize(() -> {
    LunarLogger.info(MOD_ID, "Initializing core systems");
    initializeConfig();
    registerCommands();
});
```

### `onClientInitialize(Runnable callback)`

Register a callback to be executed during client initialization.

**Parameters:**
- `callback` - The callback to execute

**Behavior:**
- Only runs in client environment
- If already initialized, runs immediately
- Otherwise, queues for next client initialization
- Callbacks run in registration order

**Example:**
```java
LifecycleManager.onClientInitialize(() -> {
    LunarLogger.info(MOD_ID, "Setting up client features");
    registerKeyBindings();
    initializeClientConfig();
});
```

### `onServerInitialize(Runnable callback)`

Register a callback to be executed during server initialization.

**Parameters:**
- `callback` - The callback to execute

**Behavior:**
- Only runs on dedicated server
- If already initialized, runs immediately
- Otherwise, queues for next server initialization
- Callbacks run in registration order

**Example:**
```java
LifecycleManager.onServerInitialize(() -> {
    LunarLogger.info(MOD_ID, "Setting up server features");
    registerServerEvents();
    loadServerData();
});
```

### `onShutdown(Runnable callback)`

Register a callback to be executed during shutdown.

**Parameters:**
- `callback` - The callback to execute

**Behavior:**
- Callbacks execute in **reverse** order of registration (LIFO)
- This allows proper cleanup ordering
- Always queued, never runs immediately
- Guaranteed to run before JVM shutdown

**Example:**
```java
LifecycleManager.onShutdown(() -> {
    LunarLogger.info(MOD_ID, "Saving data before shutdown");
    config.save();
    database.close();
});
```

## Best Practices

### 1. Use for Cross-Mod Compatibility

```java
// Instead of directly in onInitialize()
LifecycleManager.onInitialize(() -> {
    // This can be called from anywhere, even libraries
    registerFeature();
});
```

### 2. Clean Resource Management

```java
private Connection connection;

LifecycleManager.onInitialize(() -> {
    connection = Database.connect();
});

LifecycleManager.onShutdown(() -> {
    if (connection != null) {
        connection.close();
    }
});
```

### 3. Organize Complex Initialization

```java
@Override
public void onInitialize() {
    // Register all lifecycle hooks first
    LifecycleManager.onInitialize(this::initializeCore);
    LifecycleManager.onInitialize(this::registerEvents);
    LifecycleManager.onInitialize(this::loadConfig);
    
    LifecycleManager.onClientInitialize(this::setupClient);
    LifecycleManager.onServerInitialize(this::setupServer);
    
    LifecycleManager.onShutdown(this::cleanup);
}

private void initializeCore() { /* ... */ }
private void registerEvents() { /* ... */ }
private void loadConfig() { /* ... */ }
private void setupClient() { /* ... */ }
private void setupServer() { /* ... */ }
private void cleanup() { /* ... */ }
```

### 4. Handle Dependencies Between Mods

```java
LifecycleManager.onInitialize(() -> {
    if (FabricLoader.getInstance().isModLoaded("somemod")) {
        // Initialize compatibility with other mod
        SomeModIntegration.initialize();
    }
});
```

## Common Patterns

### Configuration Loading

```java
private Config config;

LifecycleManager.onInitialize(() -> {
    config = new Config(MOD_ID);
    config.load();
});

LifecycleManager.onShutdown(() -> {
    config.save();
});
```

### Event Registration

```java
LifecycleManager.onInitialize(() -> {
    ServerLifecycleEvents.SERVER_STARTED.register(server -> {
        // Server started
    });
    
    ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
        // Server stopping
    });
});
```

### Delayed Initialization

```java
LifecycleManager.onServerInitialize(() -> {
    // Wait a bit for the server to fully start
    TaskScheduler.runLater(MOD_ID, 20, () -> {
        LunarLogger.info(MOD_ID, "Server fully initialized");
    });
});
```

## Thread Safety

- All callbacks run on the main thread
- Safe to call registration methods from any thread
- Callbacks are queued and executed at the appropriate time
- No synchronization needed in callback code (unless accessing external resources)

## Notes

- Shutdown callbacks run in **reverse order** (LIFO) to allow proper cleanup
- If you register a callback after the phase has already occurred, it runs immediately
- Each callback is guaranteed to run exactly once
- Exceptions in callbacks are caught and logged but don't prevent other callbacks
- The system is designed to be called from mod initializers, but can be used from anywhere

## See Also

- [Config](config.md) - For configuration management
- [EventHelper](event-helper.md) - For event registration
- [TaskScheduler](task-scheduler.md) - For scheduled tasks

