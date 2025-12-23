# LunarCore Fabric

A lightweight foundational SDK for Minecraft Fabric mods. Provides essential utilities for mod development without gameplay mechanics, UI systems, or forced mixins.

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11-green.svg)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-API-orange.svg)](https://fabricmc.net/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## Features

- **Mod Lifecycle Helpers** - Easy lifecycle management without multiple interfaces
- **Config Abstraction** - Simple JSON-based configuration with automatic defaults
- **Logger Wrapper** - Simplified SLF4J logging with per-mod instances
- **Event Helper Utilities** - Cleaner event registration with error handling
- **Safe Task Scheduling** - Thread-safe delayed and repeating tasks
- **Version/Update Checker** - Asynchronous update checking from GitHub or custom endpoints

## Design Principles

- **Minimal and Lightweight** - Only essential utilities, no bloat
- **Fabric-Native** - Built on Fabric's APIs, no unnecessary abstractions
- **No Forced Mechanics** - No gameplay changes, UI systems, or required mixins
- **Thread-Safe** - Safe task scheduling and async operations
- **Easy to Use** - Simple, intuitive API surface

## Installation

### Using GitHub Packages

Add LunarCore as a dependency to your `build.gradle`:

```gradle
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/LunarBit-dev/LunarCore-fabric")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_USER")
            password = project.findProperty("gpr.token") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    modImplementation "dev.lunarbit.lunarcore:lunarcore:1.0.0"
    include "dev.lunarbit.lunarcore:lunarcore:1.0.0"
}
```

## Quick Start

```java
public class YourMod implements ModInitializer {
    public static final String MOD_ID = "yourmod";
    private Config config;

    @Override
    public void onInitialize() {
        // Use LunarLogger for easy logging
        LunarLogger.info(MOD_ID, "Initializing Your Mod");

        // Register lifecycle callbacks
        LifecycleManager.onInitialize(() -> {
            config = new Config(MOD_ID);
            // Load config with defaults
            boolean enabled = config.getBoolean("enabled", true);
            config.save();
        });

        LifecycleManager.onShutdown(() -> {
            config.save();
        });

        // Register events with error handling
        EventHelper.forMod(MOD_ID)
            .on(ServerLifecycleEvents.SERVER_STARTED, server -> {
                LunarLogger.info(MOD_ID, "Server started!");
                
                // Schedule a delayed task
                TaskScheduler.runLater(MOD_ID, 100, () -> {
                    LunarLogger.info(MOD_ID, "5 seconds later...");
                });
            })
            .registerAll();

        // Check for updates asynchronously
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
    }
}
```

## API Documentation

### LifecycleManager

Manages mod lifecycle callbacks for initialization and shutdown phases.

- `onInitialize(Runnable)` - Register common initialization callback
- `onClientInitialize(Runnable)` - Register client initialization callback
- `onServerInitialize(Runnable)` - Register server initialization callback
- `onShutdown(Runnable)` - Register shutdown callback (reverse order execution)

[Full Documentation](docs/lifecycle-manager.md)

### Config

Simple JSON-based configuration with automatic defaults.

- `Config(String modId)` - Create config with default filename
- `Config(String modId, String filename)` - Create config with custom filename
- `load()` - Load configuration from disk
- `save()` - Save configuration to disk
- `getString(String key, String default)` - Get string value
- `getInt(String key, int default)` - Get integer value
- `getBoolean(String key, boolean default)` - Get boolean value
- `getDouble(String key, double default)` - Get double value
- `set(String key, Object value)` - Set a value
- `has(String key)` - Check if key exists
- `remove(String key)` - Remove a key
- `getData()` - Get raw JsonObject

[Full Documentation](docs/config.md)

### LunarLogger

Enhanced logger wrapper with simplified logging methods.

- `info(String modId, String message)` - Log info message
- `info(String modId, String format, Object... args)` - Log formatted info
- `warn(String modId, String message)` - Log warning
- `error(String modId, String message)` - Log error
- `error(String modId, String message, Throwable)` - Log error with exception
- `debug(String modId, String message)` - Log debug message
- `isDebugEnabled(String modId)` - Check if debug is enabled
- `getLoggerInstance(String modId)` - Get raw SLF4J logger

[Full Documentation](docs/logger.md)

### EventHelper

Simplified event registration with automatic error handling.

- `register(Event, listener, String modId)` - Register event with error handling
- `forMod(String modId)` - Create EventRegistrar for batch registration
  - `on(Event, listener)` - Add event to batch
  - `registerAll()` - Register all batched events

[Full Documentation](docs/event-helper.md)

### TaskScheduler

Safe task scheduling system running on the main server thread.

- `runLater(String modId, int ticks, Runnable)` - Schedule delayed task
- `runRepeating(String modId, int delay, int interval, Runnable)` - Schedule repeating task
- `runNextTick(String modId, Runnable)` - Schedule for next tick
- `runSync(String modId, Runnable)` - Execute on main thread
- `cancelAll(String modId)` - Cancel all tasks for a mod

[Full Documentation](docs/task-scheduler.md)

### UpdateChecker

Asynchronous version and update checker.

- `checkGitHubRelease(String modId, String owner, String repo, String version, Consumer<UpdateResult>)` - Check GitHub releases
- `checkCustomEndpoint(String modId, String url, String version, Consumer<UpdateResult>)` - Check custom endpoint

[Full Documentation](docs/update-checker.md)

## Example Mod

See `dev.lunarbit.lunarcore.example.ExampleMod` for a complete example demonstrating all features.

## Requirements

- Fabric Loader >= 0.18.3
- Fabric API
- Minecraft 1.21.11
- Java 21+

## Thread Safety

- All TaskScheduler tasks run on the main server thread
- Config operations are not thread-safe by default - use `TaskScheduler.runSync()` if needed
- Logger is thread-safe
- Event registration should be done during initialization

## Performance

LunarCore is designed to be lightweight with minimal overhead:

- Config uses lazy loading and caching
- TaskScheduler processes tasks efficiently during server tick
- Logger uses SLF4J's efficient logging
- Update checker runs asynchronously

## Best Practices

1. **Always use your mod ID** - Pass your mod ID to all API calls for proper logging and tracking
2. **Save configs on shutdown** - Register shutdown callbacks to save configuration
3. **Use batch event registration** - Use `EventHelper.forMod()` for cleaner code
4. **Schedule tasks properly** - Use TaskScheduler for any delayed or repeated operations
5. **Check for updates** - Inform users about available updates

## License

MIT License - See LICENSE file for details

## Contributing

Contributions are welcome! Please follow the [LunarCore Specification](https://github.com/LunarBit-dev/LunarCore-spec).

## Support

- GitHub: [LunarCore Spec](https://github.com/LunarBit-dev/LunarCore-spec)
- Issues: [GitHub Issues](https://github.com/LunarBit-dev/LunarCore-spec/issues)
- Documentation: [API Docs](docs/API.md)

