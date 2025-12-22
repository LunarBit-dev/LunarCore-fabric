# Getting Started with LunarCore

This guide will help you get started using LunarCore in your Fabric mod.

## Installation

### Add Dependency

Add LunarCore to your `build.gradle`:

```gradle
repositories {
    maven { url = "https://your-maven-repo.com" }
}

dependencies {
    modImplementation "dev.lunarbit.lunarcore:lunarcore:1.0.0"
    include "dev.lunarbit.lunarcore:lunarcore:1.0.0"
}
```

### Refresh Gradle

Run `./gradlew --refresh-dependencies` to download LunarCore.

## Your First Mod

### 1. Create Your Mod Class

```java
package com.example.mymod;

import dev.lunarbit.lunarcore.api.lifecycle.LifecycleManager;
import dev.lunarbit.lunarcore.api.log.LunarLogger;
import net.fabricmc.api.ModInitializer;

public class MyMod implements ModInitializer {
    public static final String MOD_ID = "mymod";
    
    @Override
    public void onInitialize() {
        LunarLogger.info(MOD_ID, "Hello from MyMod!");
        
        LifecycleManager.onInitialize(() -> {
            LunarLogger.info(MOD_ID, "Initialization complete");
        });
    }
}
```

### 2. Update fabric.mod.json

```json
{
  "schemaVersion": 1,
  "id": "mymod",
  "version": "1.0.0",
  "name": "My Mod",
  "entrypoints": {
    "main": [
      "com.example.mymod.MyMod"
    ]
  },
  "depends": {
    "fabricloader": ">=0.18.3",
    "minecraft": "~1.21.11",
    "fabric-api": "*",
    "lunarcore": "*"
  }
}
```

### 3. Run Your Mod

```bash
./gradlew runClient
```

## Basic Features

### Logging

```java
LunarLogger.info(MOD_ID, "This is an info message");
LunarLogger.warn(MOD_ID, "This is a warning");
LunarLogger.error(MOD_ID, "This is an error");
LunarLogger.debug(MOD_ID, "This is debug info");
```

### Configuration

```java
Config config = new Config(MOD_ID);
config.load();

// Get values with defaults
String name = config.getString("player.name", "Steve");
int level = config.getInt("player.level", 1);
boolean enabled = config.getBoolean("features.enabled", true);

// Set values
config.set("player.name", "Alex");
config.save();
```

### Lifecycle Callbacks

```java
LifecycleManager.onInitialize(() -> {
    // Common initialization
});

LifecycleManager.onClientInitialize(() -> {
    // Client-only initialization
});

LifecycleManager.onServerInitialize(() -> {
    // Server-only initialization
});

LifecycleManager.onShutdown(() -> {
    // Cleanup code
});
```

### Event Registration

```java
EventHelper.forMod(MOD_ID)
    .on(ServerLifecycleEvents.SERVER_STARTED, server -> {
        LunarLogger.info(MOD_ID, "Server started!");
    })
    .on(ServerLifecycleEvents.SERVER_STOPPING, server -> {
        LunarLogger.info(MOD_ID, "Server stopping!");
    })
    .registerAll();
```

### Task Scheduling

```java
// Run after 5 seconds (100 ticks)
TaskScheduler.runLater(MOD_ID, 100, () -> {
    LunarLogger.info(MOD_ID, "Delayed task executed");
});

// Run every second
TaskScheduler.runRepeating(MOD_ID, 0, 20, () -> {
    LunarLogger.debug(MOD_ID, "Repeating task");
});
```

### Update Checking

```java
UpdateChecker.checkGitHubRelease(
    MOD_ID,
    "your-username",
    "your-repo",
    "1.0.0",
    result -> {
        if (result.isUpdateAvailable()) {
            LunarLogger.info(MOD_ID, "Update available: {}", 
                result.getLatestVersion());
        }
    }
);
```

## Complete Example

Here's a complete example that uses all features:

```java
package com.example.mymod;

import dev.lunarbit.lunarcore.api.config.Config;
import dev.lunarbit.lunarcore.api.event.EventHelper;
import dev.lunarbit.lunarcore.api.lifecycle.LifecycleManager;
import dev.lunarbit.lunarcore.api.log.LunarLogger;
import dev.lunarbit.lunarcore.api.scheduler.TaskScheduler;
import dev.lunarbit.lunarcore.api.update.UpdateChecker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class MyMod implements ModInitializer {
    public static final String MOD_ID = "mymod";
    public static final String VERSION = "1.0.0";
    
    private Config config;
    
    @Override
    public void onInitialize() {
        LunarLogger.info(MOD_ID, "Initializing MyMod v{}", VERSION);
        
        // Setup lifecycle callbacks
        setupLifecycle();
        
        // Register events
        registerEvents();
        
        // Check for updates (delayed)
        TaskScheduler.runLater(MOD_ID, 100, this::checkForUpdates);
        
        LunarLogger.info(MOD_ID, "MyMod initialized successfully");
    }
    
    private void setupLifecycle() {
        LifecycleManager.onInitialize(() -> {
            LunarLogger.info(MOD_ID, "Loading configuration");
            loadConfig();
        });
        
        LifecycleManager.onShutdown(() -> {
            LunarLogger.info(MOD_ID, "Saving configuration");
            if (config != null) {
                config.save();
            }
        });
    }
    
    private void registerEvents() {
        EventHelper.forMod(MOD_ID)
            .on(ServerLifecycleEvents.SERVER_STARTED, server -> {
                LunarLogger.info(MOD_ID, "Server has started");
                
                // Start periodic task
                TaskScheduler.runRepeating(MOD_ID, 6000, 6000, () -> {
                    LunarLogger.debug(MOD_ID, "Auto-save triggered");
                    if (config != null) {
                        config.save();
                    }
                });
            })
            .on(ServerLifecycleEvents.SERVER_STOPPING, server -> {
                LunarLogger.info(MOD_ID, "Server is stopping");
                TaskScheduler.cancelAll(MOD_ID);
            })
            .registerAll();
    }
    
    private void loadConfig() {
        config = new Config(MOD_ID);
        config.load();
        
        // Load settings
        String serverName = config.getString("server.name", "My Server");
        int maxPlayers = config.getInt("server.maxPlayers", 20);
        boolean debugMode = config.getBoolean("debug.enabled", false);
        
        LunarLogger.info(MOD_ID, "Config loaded: server={}, maxPlayers={}", 
            serverName, maxPlayers);
        
        // Save defaults
        config.save();
    }
    
    private void checkForUpdates() {
        UpdateChecker.checkGitHubRelease(
            MOD_ID,
            "your-username",
            "your-repo",
            VERSION,
            result -> {
                if (result.isUpdateAvailable()) {
                    LunarLogger.info(MOD_ID, 
                        "Update available! Current: {}, Latest: {}",
                        result.getCurrentVersion(),
                        result.getLatestVersion());
                } else {
                    LunarLogger.debug(MOD_ID, "Mod is up to date");
                }
            }
        );
    }
}
```

## Next Steps

### Learn More

- [API Documentation](API.md) - Complete API reference
- [LifecycleManager](lifecycle-manager.md) - Mod lifecycle management
- [Config](config.md) - Configuration system
- [LunarLogger](logger.md) - Logging utilities
- [EventHelper](event-helper.md) - Event registration
- [TaskScheduler](task-scheduler.md) - Task scheduling
- [UpdateChecker](update-checker.md) - Update checking

### Best Practices

1. **Always use your MOD_ID** - Pass it to all API calls for proper tracking
2. **Save configs on shutdown** - Register a shutdown callback to save configuration
3. **Use batch event registration** - Use `EventHelper.forMod()` for cleaner code
4. **Schedule tasks properly** - Use TaskScheduler instead of manual tick handling
5. **Check for updates** - Keep your users informed about new versions

### Common Patterns

#### Config with Lifecycle

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

#### Server Event Handling

```java
EventHelper.forMod(MOD_ID)
    .on(ServerLifecycleEvents.SERVER_STARTED, server -> {
        // Server started
    })
    .on(ServerLifecycleEvents.SERVER_STOPPING, server -> {
        // Server stopping - cleanup
    })
    .registerAll();
```

#### Periodic Task

```java
TaskScheduler.runRepeating(MOD_ID, 6000, 6000, () -> {
    // This runs every 5 minutes
    performPeriodicTask();
});
```

## Troubleshooting

### Build Errors

If you get build errors, make sure:
- LunarCore dependency is in your `build.gradle`
- You've run `./gradlew --refresh-dependencies`
- Your Gradle is up to date
- You're using Java 21+

### Runtime Errors

If you get runtime errors:
- Check that `lunarcore` is in your `fabric.mod.json` dependencies
- Verify LunarCore is actually bundled (check the jar file)
- Look at the logs for more detailed error messages

### Config Not Saving

If your config isn't saving:
- Make sure you call `config.save()`
- Register a shutdown callback to save on exit
- Check file permissions in the `config/` directory

## Support

- GitHub: [LunarCore Spec](https://github.com/LunarBit-dev/LunarCore-spec)
- Issues: [Report Issues](https://github.com/LunarBit-dev/LunarCore-spec/issues)
- Example: See `ExampleMod.java` in the LunarCore source

## License

LunarCore is available under the MIT License.

