# Config

Simple JSON-based configuration system with automatic defaults, type safety, and easy-to-use API.

## Features

- JSON-based storage for human readability
- Automatic default value handling
- Type-safe getters (String, int, boolean, double)
- Lazy loading and caching
- Automatic file creation
- Nested key support using dot notation
- Pretty-printed JSON output

## Usage

### Creating a Config

```java
// Default: saves to config/yourmod.json
Config config = new Config("yourmod");

// Custom filename: saves to config/custom-settings.json
Config config = new Config("yourmod", "custom-settings.json");
```

### Loading and Saving

```java
// Load from disk (returns true if file existed)
boolean loaded = config.load();

// Save to disk (returns true if successful)
boolean saved = config.save();
```

### Getting Values

```java
// String with default
String serverName = config.getString("server.name", "My Server");

// Integer with default
int maxPlayers = config.getInt("server.maxPlayers", 20);

// Boolean with default
boolean enabled = config.getBoolean("features.enabled", true);

// Double with default
double multiplier = config.getDouble("gameplay.multiplier", 1.5);
```

### Setting Values

```java
config.set("server.name", "New Name");
config.set("server.maxPlayers", 30);
config.set("features.enabled", false);
config.set("gameplay.multiplier", 2.0);

// Don't forget to save!
config.save();
```

### Checking and Removing

```java
// Check if key exists
if (config.has("server.name")) {
    // Key exists
}

// Remove a key
config.remove("server.name");
```

## API Reference

### Constructors

#### `Config(String modId)`

Create a new config with default filename.

**Parameters:**
- `modId` - The mod ID (used for logging and default filename)

**Example:**
```java
Config config = new Config("mymod");
// Saves to: config/mymod.json
```

#### `Config(String modId, String filename)`

Create a new config with custom filename.

**Parameters:**
- `modId` - The mod ID (used for logging)
- `filename` - The config filename

**Example:**
```java
Config config = new Config("mymod", "advanced-settings.json");
// Saves to: config/advanced-settings.json
```

### Methods

#### `boolean load()`

Load configuration from disk.

**Returns:** `true` if file existed and was loaded, `false` if file didn't exist

**Example:**
```java
if (config.load()) {
    LunarLogger.info(MOD_ID, "Config loaded successfully");
} else {
    LunarLogger.info(MOD_ID, "No config found, using defaults");
}
```

#### `boolean save()`

Save configuration to disk. Creates parent directories if needed.

**Returns:** `true` if saved successfully, `false` on error

**Example:**
```java
config.set("option", "value");
if (config.save()) {
    LunarLogger.info(MOD_ID, "Config saved successfully");
}
```

#### `String getString(String key, String defaultValue)`

Get a string value from config.

**Parameters:**
- `key` - The config key
- `defaultValue` - The default value if key doesn't exist

**Returns:** The value or default

**Behavior:** If key doesn't exist, adds it with the default value

**Example:**
```java
String name = config.getString("player.name", "Steve");
```

#### `int getInt(String key, int defaultValue)`

Get an integer value from config.

**Parameters:**
- `key` - The config key
- `defaultValue` - The default value if key doesn't exist

**Returns:** The value or default

**Example:**
```java
int lives = config.getInt("game.lives", 3);
```

#### `boolean getBoolean(String key, boolean defaultValue)`

Get a boolean value from config.

**Parameters:**
- `key` - The config key
- `defaultValue` - The default value if key doesn't exist

**Returns:** The value or default

**Example:**
```java
boolean pvpEnabled = config.getBoolean("server.pvp", true);
```

#### `double getDouble(String key, double defaultValue)`

Get a double value from config.

**Parameters:**
- `key` - The config key
- `defaultValue` - The default value if key doesn't exist

**Returns:** The value or default

**Example:**
```java
double spawnRate = config.getDouble("mobs.spawnRate", 1.0);
```

#### `void set(String key, Object value)`

Set a value in the config.

**Parameters:**
- `key` - The config key
- `value` - The value to set (String, Number, Boolean, or other)

**Example:**
```java
config.set("option", "value");
config.set("count", 42);
config.set("enabled", true);
```

#### `boolean has(String key)`

Check if a key exists in the config.

**Parameters:**
- `key` - The config key

**Returns:** `true` if the key exists

**Example:**
```java
if (config.has("experimental.feature")) {
    // Feature is configured
}
```

#### `void remove(String key)`

Remove a key from the config.

**Parameters:**
- `key` - The config key to remove

**Example:**
```java
config.remove("deprecated.option");
config.save();
```

#### `JsonObject getData()`

Get the underlying JsonObject for advanced usage.

**Returns:** The JsonObject containing all config data

**Example:**
```java
JsonObject data = config.getData();
// Direct manipulation for advanced use cases
```

## Best Practices

### 1. Load in Initialization, Save on Shutdown

```java
private Config config;

@Override
public void onInitialize() {
    LifecycleManager.onInitialize(() -> {
        config = new Config(MOD_ID);
        config.load();
        applyConfig();
    });
    
    LifecycleManager.onShutdown(() -> {
        config.save();
    });
}
```

### 2. Use Nested Keys for Organization

```java
// Instead of flat keys
config.getString("serverName", "My Server");
config.getInt("serverPort", 25565);

// Use nested keys
config.getString("server.name", "My Server");
config.getInt("server.port", 25565);
config.getBoolean("server.pvp", true);
```

### 3. Provide Sensible Defaults

```java
// Always provide reasonable defaults
int maxPlayers = config.getInt("server.maxPlayers", 20);
boolean whitelist = config.getBoolean("server.whitelist", false);
double difficulty = config.getDouble("game.difficulty", 1.0);
```

### 4. Validate Values After Loading

```java
config.load();

// Validate and clamp values
int maxPlayers = config.getInt("server.maxPlayers", 20);
if (maxPlayers < 1 || maxPlayers > 100) {
    maxPlayers = 20;
    config.set("server.maxPlayers", maxPlayers);
    LunarLogger.warn(MOD_ID, "Invalid maxPlayers, reset to default");
}
```

### 5. Save After Important Changes

```java
public void updateSetting(String key, Object value) {
    config.set(key, value);
    config.save();
    LunarLogger.info(MOD_ID, "Setting updated: {} = {}", key, value);
}
```

## Common Patterns

### Configuration with Categories

```java
// Server settings
config.getString("server.name", "My Server");
config.getInt("server.maxPlayers", 20);
config.getBoolean("server.pvp", true);

// Gameplay settings
config.getDouble("gameplay.difficulty", 1.0);
config.getBoolean("gameplay.keepInventory", false);

// Feature flags
config.getBoolean("features.advancedMode", false);
config.getBoolean("features.experimentalFeatures", false);
```

### Config Reloading

```java
public void reloadConfig() {
    LunarLogger.info(MOD_ID, "Reloading configuration");
    config.load();
    applyConfig();
}

private void applyConfig() {
    // Apply config values to your mod
    boolean pvpEnabled = config.getBoolean("server.pvp", true);
    if (pvpEnabled) {
        enablePvP();
    } else {
        disablePvP();
    }
}
```

### Migration from Old Config

```java
config.load();

// Migrate old keys to new format
if (config.has("oldKey")) {
    Object value = config.getData().get("oldKey");
    config.set("newKey", value);
    config.remove("oldKey");
    config.save();
    LunarLogger.info(MOD_ID, "Migrated config from old format");
}
```

### Environment-Specific Configs

```java
// Different configs for client and server
Config clientConfig = new Config(MOD_ID, "client-config.json");
Config serverConfig = new Config(MOD_ID, "server-config.json");

LifecycleManager.onClientInitialize(() -> {
    clientConfig.load();
});

LifecycleManager.onServerInitialize(() -> {
    serverConfig.load();
});
```

## File Format

The config is stored as pretty-printed JSON:

```json
{
  "server": {
    "name": "My Server",
    "maxPlayers": 20,
    "pvp": true
  },
  "gameplay": {
    "difficulty": 1.5,
    "keepInventory": false
  },
  "features": {
    "advancedMode": false
  }
}
```

Note: The actual JSON will be flat with dot notation keys unless you manually structure it using the JsonObject.

## Thread Safety

- Config operations are **not thread-safe** by default
- If you need to modify config from another thread, use:

```java
TaskScheduler.runSync(MOD_ID, () -> {
    config.set("key", "value");
    config.save();
});
```

## Error Handling

- Load failures are logged but don't throw exceptions
- Save failures are logged but don't throw exceptions
- Invalid JSON is logged with parse errors
- Missing parent directories are created automatically
- Method return values indicate success/failure

## Notes

- Config files are stored in the `config/` directory
- Values are automatically added if they don't exist (when using getters with defaults)
- Changes are only persisted when you call `save()`
- The config is not automatically reloaded when the file changes
- Use `getData()` for advanced manipulation, but be careful

## See Also

- [LifecycleManager](lifecycle-manager.md) - For initialization and shutdown hooks
- [LunarLogger](logger.md) - For logging
- [TaskScheduler](task-scheduler.md) - For thread-safe config operations

