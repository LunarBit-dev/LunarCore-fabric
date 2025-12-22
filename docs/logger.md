# LunarLogger

Enhanced logger wrapper providing simplified logging methods with automatic logger management per mod ID.

## Features

- Automatic logger instance management per mod ID
- Simplified logging methods
- Formatted message support
- Exception logging
- Thread-safe operations
- Built on SLF4J for compatibility
- Debug level checking

## Usage

### Basic Logging

```java
// Info messages
LunarLogger.info("mymod", "Server started successfully");
LunarLogger.info("mymod", "Loaded {} players", playerCount);

// Warning messages
LunarLogger.warn("mymod", "Config file not found, using defaults");

// Error messages
LunarLogger.error("mymod", "Failed to connect to database");
LunarLogger.error("mymod", "Error loading world", exception);

// Debug messages
LunarLogger.debug("mymod", "Processing tick {}", tickCount);
```

### Formatted Messages

```java
// Single argument
LunarLogger.info(MOD_ID, "Player {} joined", playerName);

// Multiple arguments
LunarLogger.info(MOD_ID, "Player {} joined from {}", playerName, ipAddress);

// Many arguments
LunarLogger.debug(MOD_ID, "Stats: {} players, {} chunks, {} entities",
    players, chunks, entities);
```

## API Reference

### `info(String modId, String message)`

Log an info message.

**Parameters:**
- `modId` - The mod ID
- `message` - The message to log

**Example:**
```java
LunarLogger.info("mymod", "Initialization complete");
```

### `info(String modId, String format, Object... args)`

Log a formatted info message.

**Parameters:**
- `modId` - The mod ID
- `format` - The message format (use `{}` for placeholders)
- `args` - The format arguments

**Example:**
```java
LunarLogger.info("mymod", "Loaded {} items from {}", itemCount, source);
```

### `warn(String modId, String message)`

Log a warning message.

**Parameters:**
- `modId` - The mod ID
- `message` - The message to log

**Example:**
```java
LunarLogger.warn("mymod", "Configuration option deprecated");
```

### `warn(String modId, String format, Object... args)`

Log a formatted warning message.

**Parameters:**
- `modId` - The mod ID
- `format` - The message format
- `args` - The format arguments

**Example:**
```java
LunarLogger.warn("mymod", "Player {} attempted invalid action", playerName);
```

### `error(String modId, String message)`

Log an error message.

**Parameters:**
- `modId` - The mod ID
- `message` - The message to log

**Example:**
```java
LunarLogger.error("mymod", "Critical system failure");
```

### `error(String modId, String message, Throwable throwable)`

Log an error message with exception details.

**Parameters:**
- `modId` - The mod ID
- `message` - The message to log
- `throwable` - The exception

**Example:**
```java
try {
    loadData();
} catch (IOException e) {
    LunarLogger.error("mymod", "Failed to load data", e);
}
```

### `error(String modId, String format, Object... args)`

Log a formatted error message.

**Parameters:**
- `modId` - The mod ID
- `format` - The message format
- `args` - The format arguments

**Example:**
```java
LunarLogger.error("mymod", "Failed to process {} after {} attempts", 
    operation, attempts);
```

### `debug(String modId, String message)`

Log a debug message.

**Parameters:**
- `modId` - The mod ID
- `message` - The message to log

**Note:** Only shown when debug logging is enabled

**Example:**
```java
LunarLogger.debug("mymod", "Entering method processData()");
```

### `debug(String modId, String format, Object... args)`

Log a formatted debug message.

**Parameters:**
- `modId` - The mod ID
- `format` - The message format
- `args` - The format arguments

**Example:**
```java
LunarLogger.debug("mymod", "Processing chunk at {}, {}", x, z);
```

### `trace(String modId, String message)`

Log a trace message (very detailed debugging).

**Parameters:**
- `modId` - The mod ID
- `message` - The message to log

**Note:** Only shown when trace logging is enabled

**Example:**
```java
LunarLogger.trace("mymod", "Variable state: x=5, y=10");
```

### `isDebugEnabled(String modId)`

Check if debug logging is enabled for a mod.

**Parameters:**
- `modId` - The mod ID

**Returns:** `true` if debug is enabled

**Example:**
```java
if (LunarLogger.isDebugEnabled("mymod")) {
    String expensiveDebugInfo = generateDetailedReport();
    LunarLogger.debug("mymod", expensiveDebugInfo);
}
```

### `getLoggerInstance(String modId)`

Get the raw SLF4J logger instance for advanced usage.

**Parameters:**
- `modId` - The mod ID

**Returns:** The SLF4J Logger instance

**Example:**
```java
Logger logger = LunarLogger.getLoggerInstance("mymod");
// Use logger directly for advanced features
```

## Best Practices

### 1. Use Your Mod ID Consistently

```java
public class MyMod implements ModInitializer {
    public static final String MOD_ID = "mymod";
    
    @Override
    public void onInitialize() {
        LunarLogger.info(MOD_ID, "Initializing");
        // Use MOD_ID everywhere
    }
}
```

### 2. Choose Appropriate Log Levels

```java
// INFO - Important events, user-facing information
LunarLogger.info(MOD_ID, "Server started");

// WARN - Potential issues, deprecated usage
LunarLogger.warn(MOD_ID, "Using deprecated API");

// ERROR - Actual errors, failures
LunarLogger.error(MOD_ID, "Failed to save data");

// DEBUG - Development information
LunarLogger.debug(MOD_ID, "Cache hit rate: 85%");
```

### 3. Use Formatting for Performance

```java
// GOOD - Only formats if logging is enabled
LunarLogger.debug(MOD_ID, "Player {} at position {}, {}, {}", 
    player, x, y, z);

// BAD - Always creates string even if debug is disabled
LunarLogger.debug(MOD_ID, "Player " + player + " at position " + 
    x + ", " + y + ", " + z);
```

### 4. Log Exceptions Properly

```java
try {
    riskyOperation();
} catch (Exception e) {
    // Include exception for stack trace
    LunarLogger.error(MOD_ID, "Failed to perform operation", e);
    
    // Not just the message
    // LunarLogger.error(MOD_ID, "Error: " + e.getMessage()); // BAD
}
```

### 5. Check Debug Enabled for Expensive Operations

```java
if (LunarLogger.isDebugEnabled(MOD_ID)) {
    // Only build expensive debug info if it will be logged
    StringBuilder report = new StringBuilder();
    for (Entity entity : world.getEntities()) {
        report.append(entity.toString()).append("\n");
    }
    LunarLogger.debug(MOD_ID, "Entity report:\n{}", report);
}
```

## Common Patterns

### Initialization Logging

```java
@Override
public void onInitialize() {
    LunarLogger.info(MOD_ID, "Initializing {} v{}", MOD_NAME, VERSION);
    
    try {
        loadConfig();
        registerEvents();
        LunarLogger.info(MOD_ID, "Initialization complete");
    } catch (Exception e) {
        LunarLogger.error(MOD_ID, "Initialization failed", e);
    }
}
```

### Event Logging

```java
ServerLifecycleEvents.SERVER_STARTED.register(server -> {
    LunarLogger.info(MOD_ID, "Server started with {} players", 
        server.getPlayerCount());
});

ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
    LunarLogger.info(MOD_ID, "Server shutting down");
});
```

### Performance Monitoring

```java
long startTime = System.currentTimeMillis();
processData();
long duration = System.currentTimeMillis() - startTime;

if (duration > 100) {
    LunarLogger.warn(MOD_ID, "Slow operation took {}ms", duration);
} else {
    LunarLogger.debug(MOD_ID, "Operation completed in {}ms", duration);
}
```

### Conditional Logging

```java
public void updateEntity(Entity entity) {
    if (LunarLogger.isDebugEnabled(MOD_ID)) {
        LunarLogger.debug(MOD_ID, "Updating entity: {}", entity.getId());
    }
    
    // Update logic
    entity.update();
    
    if (LunarLogger.isDebugEnabled(MOD_ID)) {
        LunarLogger.debug(MOD_ID, "Entity updated: {} -> {}", 
            entity.getOldState(), entity.getNewState());
    }
}
```

### Error Recovery Logging

```java
public boolean loadData() {
    try {
        data = readFromFile();
        LunarLogger.info(MOD_ID, "Data loaded successfully");
        return true;
    } catch (IOException e) {
        LunarLogger.error(MOD_ID, "Failed to load data, using defaults", e);
        data = getDefaults();
        return false;
    }
}
```

## Log Levels

### INFO
- Server/client started/stopped
- Mod initialization/shutdown
- Important state changes
- Configuration loaded/saved
- Updates available

### WARN
- Deprecated API usage
- Invalid configuration (using defaults)
- Potential issues
- Performance warnings
- Compatibility issues

### ERROR
- Exceptions and failures
- Data corruption
- Missing required files
- Network failures
- Critical errors

### DEBUG
- Method entry/exit
- State changes
- Performance metrics
- Cache hits/misses
- Development information

### TRACE
- Very detailed debugging
- Loop iterations
- Variable states
- Function call details

## Thread Safety

- All logging methods are thread-safe
- Logger instances are cached per mod ID
- Safe to call from any thread
- No synchronization needed in your code

## Performance

- Logger instances are cached (no repeated lookups)
- Formatted messages only evaluate if logging is enabled
- Use `isDebugEnabled()` for expensive operations
- SLF4J handles efficient logging
- Minimal overhead when disabled

## Integration with Other Systems

### With LifecycleManager

```java
LifecycleManager.onInitialize(() -> {
    LunarLogger.info(MOD_ID, "Running initialization callback");
});

LifecycleManager.onShutdown(() -> {
    LunarLogger.info(MOD_ID, "Running shutdown callback");
});
```

### With Config

```java
Config config = new Config(MOD_ID);
if (config.load()) {
    LunarLogger.info(MOD_ID, "Config loaded");
} else {
    LunarLogger.warn(MOD_ID, "Config not found, using defaults");
}
```

### With TaskScheduler

```java
TaskScheduler.runLater(MOD_ID, 100, () -> {
    LunarLogger.debug(MOD_ID, "Scheduled task executed");
});
```

### With EventHelper

```java
EventHelper.register(someEvent, listener, MOD_ID);
LunarLogger.debug(MOD_ID, "Event listener registered");
```

## Notes

- Logger instances are automatically created and cached per mod ID
- Built on SLF4J, so it works with Minecraft's logging system
- Log files are stored in `logs/` directory
- Latest log: `logs/latest.log`
- Archived logs: `logs/YYYY-MM-DD-X.log.gz`
- Use the mod ID as the logger name for easy filtering

## See Also

- [LifecycleManager](lifecycle-manager.md) - For lifecycle events
- [Config](config.md) - For configuration (logs load/save operations)
- [EventHelper](event-helper.md) - For event registration logging
- [TaskScheduler](task-scheduler.md) - For scheduled task logging

