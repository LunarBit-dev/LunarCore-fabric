# LunarCore API Documentation

Complete API reference for LunarCore Fabric SDK.

## Overview

LunarCore is a lightweight foundational SDK for Minecraft Fabric mods that provides:

- **LifecycleManager** - Mod lifecycle callbacks
- **Config** - JSON-based configuration
- **LunarLogger** - Enhanced logging wrapper
- **EventHelper** - Simplified event registration
- **TaskScheduler** - Safe task scheduling
- **UpdateChecker** - Asynchronous update checking

## Quick Reference

### LifecycleManager

```java
LifecycleManager.onInitialize(Runnable callback)
LifecycleManager.onClientInitialize(Runnable callback)
LifecycleManager.onServerInitialize(Runnable callback)
LifecycleManager.onShutdown(Runnable callback)
```

[Full Documentation](lifecycle-manager.md)

### Config

```java
Config config = new Config(modId);
config.load()
config.save()
config.getString(key, default)
config.getInt(key, default)
config.getBoolean(key, default)
config.getDouble(key, default)
config.set(key, value)
config.has(key)
config.remove(key)
```

[Full Documentation](config.md)

### LunarLogger

```java
LunarLogger.info(modId, message)
LunarLogger.info(modId, format, args...)
LunarLogger.warn(modId, message)
LunarLogger.error(modId, message)
LunarLogger.error(modId, message, throwable)
LunarLogger.debug(modId, message)
LunarLogger.isDebugEnabled(modId)
```

[Full Documentation](logger.md)

### EventHelper

```java
EventHelper.register(event, listener, modId)

EventHelper.forMod(modId)
    .on(event, listener)
    .on(event, listener)
    .registerAll()
```

[Full Documentation](event-helper.md)

### TaskScheduler

```java
TaskScheduler.runLater(modId, ticks, task)
TaskScheduler.runRepeating(modId, delay, interval, task)
TaskScheduler.runNextTick(modId, task)
TaskScheduler.runSync(modId, task)
TaskScheduler.cancelAll(modId)
```

[Full Documentation](task-scheduler.md)

### UpdateChecker

```java
UpdateChecker.checkGitHubRelease(modId, owner, repo, version, callback)
UpdateChecker.checkCustomEndpoint(modId, url, version, callback)
```

[Full Documentation](update-checker.md)

## Complete Example

```java
public class MyMod implements ModInitializer {
    public static final String MOD_ID = "mymod";
    public static final String VERSION = "1.0.0";
    
    private Config config;
    
    @Override
    public void onInitialize() {
        LunarLogger.info(MOD_ID, "Initializing {} v{}", MOD_ID, VERSION);
        
        // Lifecycle callbacks
        LifecycleManager.onInitialize(() -> {
            // Common initialization
            initializeConfig();
            LunarLogger.info(MOD_ID, "Core initialized");
        });
        
        LifecycleManager.onClientInitialize(() -> {
            // Client-only initialization
            LunarLogger.debug(MOD_ID, "Client initialized");
        });
        
        LifecycleManager.onServerInitialize(() -> {
            // Server-only initialization
            LunarLogger.debug(MOD_ID, "Server initialized");
        });
        
        LifecycleManager.onShutdown(() -> {
            // Cleanup
            config.save();
            LunarLogger.info(MOD_ID, "Shutting down");
        });
        
        // Event registration
        EventHelper.forMod(MOD_ID)
            .on(ServerLifecycleEvents.SERVER_STARTED, this::onServerStart)
            .on(ServerLifecycleEvents.SERVER_STOPPING, this::onServerStop)
            .on(ServerTickEvents.END_SERVER_TICK, this::onTick)
            .registerAll();
        
        // Update checking
        checkForUpdates();
        
        LunarLogger.info(MOD_ID, "Initialization complete");
    }
    
    private void initializeConfig() {
        config = new Config(MOD_ID);
        config.load();
        
        // Load settings with defaults
        boolean enabled = config.getBoolean("enabled", true);
        int maxValue = config.getInt("maxValue", 100);
        String serverName = config.getString("serverName", "My Server");
        
        config.save();
        
        LunarLogger.info(MOD_ID, "Config loaded - enabled: {}, max: {}", 
            enabled, maxValue);
    }
    
    private void onServerStart(MinecraftServer server) {
        LunarLogger.info(MOD_ID, "Server started");
        
        // Schedule a delayed task
        TaskScheduler.runLater(MOD_ID, 100, () -> {
            LunarLogger.info(MOD_ID, "Server fully initialized");
        });
        
        // Schedule a repeating task (every 5 minutes)
        TaskScheduler.runRepeating(MOD_ID, 6000, 6000, () -> {
            LunarLogger.debug(MOD_ID, "Periodic check");
            performPeriodicTask();
        });
    }
    
    private void onServerStop(MinecraftServer server) {
        LunarLogger.info(MOD_ID, "Server stopping");
        TaskScheduler.cancelAll(MOD_ID);
    }
    
    private void onTick(MinecraftServer server) {
        // Tick logic
    }
    
    private void performPeriodicTask() {
        // Periodic task logic
    }
    
    private void checkForUpdates() {
        TaskScheduler.runLater(MOD_ID, 100, () -> {
            UpdateChecker.checkGitHubRelease(
                MOD_ID,
                "your-username",
                "your-repo",
                VERSION,
                result -> {
                    if (result.isUpdateAvailable()) {
                        LunarLogger.info(MOD_ID, "Update available: {} -> {}",
                            result.getCurrentVersion(),
                            result.getLatestVersion());
                        LunarLogger.info(MOD_ID, "Download: {}", 
                            result.getDownloadUrl());
                    } else {
                        LunarLogger.debug(MOD_ID, "Mod is up to date");
                    }
                }
            );
        });
    }
}
```

## Dependencies

### Required
- Fabric Loader >= 0.18.3
- Fabric API
- Minecraft 1.21.11
- Java 21+

### Gradle Setup

```gradle
repositories {
    maven { url = "https://your-maven-repo.com" }
}

dependencies {
    modImplementation "dev.lunarbit.lunarcore:lunarcore:1.0.0"
    include "dev.lunarbit.lunarcore:lunarcore:1.0.0"
}
```

## Design Principles

### Minimal and Lightweight
- Only essential utilities
- No unnecessary abstractions
- Minimal memory footprint
- Efficient implementations

### Fabric-Native
- Built on Fabric's APIs
- Follows Fabric conventions
- Compatible with Fabric ecosystem
- No custom event systems

### No Forced Mechanics
- No gameplay changes
- No UI systems
- No required mixins (optional example only)
- Pure utility library

### Thread-Safe
- Safe task scheduling
- Async operations support
- Main thread guarantees where needed
- Concurrent-safe APIs

### Easy to Use
- Simple, intuitive methods
- Clear documentation
- Comprehensive examples
- Type-safe APIs

## Best Practices

### 1. Always Use Your Mod ID
```java
public static final String MOD_ID = "mymod";

// Pass to all API calls
LunarLogger.info(MOD_ID, "Message");
Config config = new Config(MOD_ID);
TaskScheduler.runLater(MOD_ID, 20, task);
```

### 2. Save Configs on Shutdown
```java
LifecycleManager.onShutdown(() -> {
    config.save();
});
```

### 3. Use Batch Event Registration
```java
// GOOD
EventHelper.forMod(MOD_ID)
    .on(event1, listener1)
    .on(event2, listener2)
    .registerAll();

// Less good
EventHelper.register(event1, listener1, MOD_ID);
EventHelper.register(event2, listener2, MOD_ID);
```

### 4. Schedule Tasks Properly
```java
// Use TaskScheduler for delayed/repeated operations
TaskScheduler.runLater(MOD_ID, 100, task);

// Not directly in tick events unless necessary
```

### 5. Check for Updates
```java
// Inform users about available updates
UpdateChecker.checkGitHubRelease(MOD_ID, owner, repo, version, callback);
```

## Thread Safety

| Component | Thread Safety |
|-----------|---------------|
| LifecycleManager | Callbacks run on main thread |
| Config | Not thread-safe (use `runSync` if needed) |
| LunarLogger | Thread-safe |
| EventHelper | Register during init, handlers run on main thread |
| TaskScheduler | Thread-safe submission, executes on main thread |
| UpdateChecker | Async operations, use `runSync` for game state |

## Performance

| Component | Performance Notes |
|-----------|-------------------|
| LifecycleManager | Minimal overhead, one-time execution |
| Config | Lazy loading, efficient JSON parsing |
| LunarLogger | SLF4J efficiency, cached instances |
| EventHelper | No overhead vs direct Fabric registration |
| TaskScheduler | O(n) per tick, efficient for hundreds of tasks |
| UpdateChecker | Asynchronous, no game impact |

## Common Patterns

### Initialization Pattern
```java
@Override
public void onInitialize() {
    LifecycleManager.onInitialize(this::initCore);
    LifecycleManager.onClientInitialize(this::initClient);
    LifecycleManager.onServerInitialize(this::initServer);
    LifecycleManager.onShutdown(this::cleanup);
    
    registerEvents();
}
```

### Config Pattern
```java
private Config config;

private void initConfig() {
    config = new Config(MOD_ID);
    config.load();
    
    // Load with defaults
    boolean feature = config.getBoolean("feature.enabled", true);
    
    config.save();
}
```

### Event Pattern
```java
private void registerEvents() {
    EventHelper.forMod(MOD_ID)
        .on(ServerLifecycleEvents.SERVER_STARTED, this::onStart)
        .on(ServerLifecycleEvents.SERVER_STOPPING, this::onStop)
        .registerAll();
}
```

### Task Pattern
```java
private void scheduleTask() {
    TaskScheduler.runLater(MOD_ID, 100, () -> {
        LunarLogger.info(MOD_ID, "Delayed task executed");
    });
}
```

### Update Pattern
```java
private void checkUpdates() {
    UpdateChecker.checkGitHubRelease(
        MOD_ID, owner, repo, VERSION,
        result -> {
            if (result.isUpdateAvailable()) {
                notifyUpdate(result);
            }
        }
    );
}
```

## Error Handling

All LunarCore APIs handle errors gracefully:

- **LifecycleManager**: Exceptions in callbacks are caught and logged
- **Config**: I/O errors are caught, logged, and return false
- **LunarLogger**: Built on SLF4J's robust error handling
- **EventHelper**: Registration errors are caught and logged
- **TaskScheduler**: Task exceptions are caught and logged, task is cancelled
- **UpdateChecker**: Network/parse errors are caught and logged

## Migration Guide

### From Direct Fabric APIs

```java
// Before
ServerLifecycleEvents.SERVER_STARTED.register(server -> {
    // ...
});

// After
EventHelper.register(ServerLifecycleEvents.SERVER_STARTED, 
    server -> {
        // ...
    }, MOD_ID);
```

### From Direct SLF4J Logger

```java
// Before
private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
LOGGER.info("Message");

// After
LunarLogger.info(MOD_ID, "Message");
```

### Adding Config

```java
// Add to your mod
private Config config;

LifecycleManager.onInitialize(() -> {
    config = new Config(MOD_ID);
    config.load();
});

LifecycleManager.onShutdown(() -> {
    config.save();
});
```

## Troubleshooting

### Config Not Loading
- Check file permissions in `config/` directory
- Verify JSON syntax with a validator
- Check logs for parse errors
- Ensure `config.load()` is called

### Tasks Not Running
- Verify server is running (tasks need server tick)
- Check if task was cancelled
- Look for exceptions in task code
- Ensure `TaskScheduler.tick()` is registered

### Events Not Firing
- Register events during initialization
- Check event registration logs
- Verify event type matches listener signature
- Ensure `registerAll()` is called for batch registration

### Update Check Failing
- Check internet connectivity
- Verify repository owner/name
- Check GitHub API rate limits
- Look for timeout/network errors in logs

## Support

- GitHub: [LunarCore Spec](https://github.com/LunarBit-dev/LunarCore-spec)
- Issues: [GitHub Issues](https://github.com/LunarBit-dev/LunarCore-spec/issues)
- Documentation: [docs/](.)

## License

MIT License - See LICENSE file for details

