# UpdateChecker

Asynchronous version and update checker for mods. Checks for updates without blocking the game.

## Features

- Asynchronous update checking (doesn't block game)
- GitHub Releases support
- Custom JSON endpoint support
- Version comparison
- Download URL retrieval
- Update descriptions/changelogs
- Error handling and logging

## Usage

### Check GitHub Release

```java
UpdateChecker.checkGitHubRelease(
    MOD_ID,
    "username",
    "repository",
    "1.0.0",
    result -> {
        if (result.isUpdateAvailable()) {
            LunarLogger.info(MOD_ID, "Update available: {} -> {}",
                result.getCurrentVersion(),
                result.getLatestVersion());
            LunarLogger.info(MOD_ID, "Download: {}", result.getDownloadUrl());
        }
    }
);
```

### Check Custom Endpoint

```java
UpdateChecker.checkCustomEndpoint(
    MOD_ID,
    "https://api.example.com/mod/version.json",
    "1.0.0",
    result -> {
        if (result.isUpdateAvailable()) {
            LunarLogger.info(MOD_ID, "New version: {}", 
                result.getLatestVersion());
        }
    }
);
```

## API Reference

### `checkGitHubRelease(String modId, String repoOwner, String repoName, String currentVersion, Consumer<UpdateResult> callback)`

Check for updates from a GitHub release.

**Parameters:**
- `modId` - The mod ID (for logging)
- `repoOwner` - The GitHub repository owner
- `repoName` - The GitHub repository name
- `currentVersion` - The current mod version
- `callback` - Callback with the update result

**Behavior:**
- Runs asynchronously (doesn't block)
- Queries GitHub API for latest release
- Compares versions (exact string match)
- Calls callback on completion
- Logs errors automatically

**Example:**
```java
UpdateChecker.checkGitHubRelease(
    "mymod",
    "username",
    "my-mod-repo",
    "1.0.0",
    result -> {
        if (result.isUpdateAvailable()) {
            notifyPlayer("Update available!");
        }
    }
);
```

### `checkCustomEndpoint(String modId, String updateUrl, String currentVersion, Consumer<UpdateResult> callback)`

Check for updates from a custom JSON endpoint.

**Parameters:**
- `modId` - The mod ID (for logging)
- `updateUrl` - The URL to check for updates
- `currentVersion` - The current mod version
- `callback` - Callback with the update result

**Expected JSON format:**
```json
{
  "version": "1.0.1",
  "downloadUrl": "https://example.com/download",
  "description": "What's new in this version"
}
```

**Example:**
```java
UpdateChecker.checkCustomEndpoint(
    "mymod",
    "https://api.mysite.com/mod-version.json",
    "1.0.0",
    result -> {
        handleUpdateResult(result);
    }
);
```

## UpdateResult

### `isUpdateAvailable()`

Check if an update is available.

**Returns:** `true` if update available

**Example:**
```java
if (result.isUpdateAvailable()) {
    // Update available
}
```

### `getCurrentVersion()`

Get the current version.

**Returns:** The current version string

**Example:**
```java
String current = result.getCurrentVersion(); // "1.0.0"
```

### `getLatestVersion()`

Get the latest version.

**Returns:** The latest version string, or `null` if check failed

**Example:**
```java
String latest = result.getLatestVersion(); // "1.0.1"
```

### `getDownloadUrl()`

Get the download URL for the update.

**Returns:** The download URL, or `null` if not available

**Example:**
```java
String url = result.getDownloadUrl();
if (url != null) {
    LunarLogger.info(MOD_ID, "Download at: {}", url);
}
```

### `getDescription()`

Get the update description/changelog.

**Returns:** The description, or `null` if not available

**Example:**
```java
String description = result.getDescription();
if (description != null) {
    LunarLogger.info(MOD_ID, "Changes:\n{}", description);
}
```

## Best Practices

### 1. Check on Server Start (with delay)

```java
EventHelper.register(ServerLifecycleEvents.SERVER_STARTED, server -> {
    // Wait a bit so it doesn't slow down startup
    TaskScheduler.runLater(MOD_ID, 100, () -> {
        checkForUpdates();
    });
}, MOD_ID);
```

### 2. Don't Check Too Frequently

```java
// GOOD - Check once on startup
LifecycleManager.onInitialize(this::checkForUpdates);

// BAD - Don't check repeatedly
// TaskScheduler.runRepeating(MOD_ID, 0, 20, this::checkForUpdates);
```

### 3. Handle All Result Cases

```java
UpdateChecker.checkGitHubRelease(MOD_ID, owner, repo, version, result -> {
    if (result.getLatestVersion() == null) {
        // Check failed (network error, API issue, etc.)
        LunarLogger.debug(MOD_ID, "Could not check for updates");
    } else if (result.isUpdateAvailable()) {
        // Update available
        LunarLogger.info(MOD_ID, "Update available: {}", 
            result.getLatestVersion());
    } else {
        // Up to date
        LunarLogger.debug(MOD_ID, "Mod is up to date");
    }
});
```

### 4. Notify Users Appropriately

```java
UpdateChecker.checkGitHubRelease(MOD_ID, owner, repo, version, result -> {
    if (result.isUpdateAvailable()) {
        // Log to console
        LunarLogger.info(MOD_ID, "Update available: {} -> {}",
            result.getCurrentVersion(), result.getLatestVersion());
        
        // Notify ops when they join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (server.getPlayerManager().isOperator(handler.getPlayer().getGameProfile())) {
                handler.getPlayer().sendMessage(
                    Text.literal("§e[" + MOD_ID + "] Update available: " + 
                        result.getLatestVersion())
                );
            }
        });
    }
});
```

### 5. Respect User Privacy

```java
Config config = new Config(MOD_ID);
boolean checkUpdates = config.getBoolean("checkUpdates", true);

if (checkUpdates) {
    checkForUpdates();
} else {
    LunarLogger.debug(MOD_ID, "Update checking disabled by config");
}
```

## Common Patterns

### Basic Update Check

```java
private void checkForUpdates() {
    UpdateChecker.checkGitHubRelease(
        MOD_ID,
        "LunarBit-dev",
        "my-mod",
        VERSION,
        result -> {
            if (result.isUpdateAvailable()) {
                LunarLogger.info(MOD_ID, "Update available!");
                LunarLogger.info(MOD_ID, "Current: {}, Latest: {}",
                    result.getCurrentVersion(),
                    result.getLatestVersion());
                LunarLogger.info(MOD_ID, "Download: {}",
                    result.getDownloadUrl());
            }
        }
    );
}
```

### With User Notification

```java
private UpdateResult cachedUpdateResult;

private void checkForUpdates() {
    UpdateChecker.checkGitHubRelease(MOD_ID, owner, repo, VERSION, result -> {
        cachedUpdateResult = result;
        
        if (result.isUpdateAvailable()) {
            LunarLogger.info(MOD_ID, "Update available: {}", 
                result.getLatestVersion());
        }
    });
}

// In player join event
private void notifyPlayer(ServerPlayerEntity player) {
    if (cachedUpdateResult != null && 
        cachedUpdateResult.isUpdateAvailable() &&
        player.hasPermissionLevel(2)) {
        
        player.sendMessage(Text.literal(
            "§e[MyMod] Update available: " + 
            cachedUpdateResult.getLatestVersion()
        ));
    }
}
```

### Custom Endpoint with Fallback

```java
private void checkForUpdates() {
    // Try custom endpoint first
    UpdateChecker.checkCustomEndpoint(
        MOD_ID,
        "https://mysite.com/mod-version.json",
        VERSION,
        result -> {
            if (result.getLatestVersion() != null) {
                handleUpdateResult(result);
            } else {
                // Fallback to GitHub
                LunarLogger.debug(MOD_ID, "Primary check failed, trying GitHub");
                UpdateChecker.checkGitHubRelease(
                    MOD_ID, owner, repo, VERSION,
                    this::handleUpdateResult
                );
            }
        }
    );
}
```

### Version Comparison with Semantic Versioning

```java
UpdateChecker.checkGitHubRelease(MOD_ID, owner, repo, VERSION, result -> {
    if (result.isUpdateAvailable()) {
        String current = result.getCurrentVersion();
        String latest = result.getLatestVersion();
        
        if (isBreakingUpdate(current, latest)) {
            LunarLogger.warn(MOD_ID, 
                "Breaking update available: {} (current: {})",
                latest, current);
        } else {
            LunarLogger.info(MOD_ID, 
                "Update available: {} (current: {})",
                latest, current);
        }
    }
});

private boolean isBreakingUpdate(String current, String latest) {
    // Simple semver major version check
    String[] currentParts = current.split("\\.");
    String[] latestParts = latest.split("\\.");
    
    if (currentParts.length > 0 && latestParts.length > 0) {
        int currentMajor = Integer.parseInt(currentParts[0]);
        int latestMajor = Integer.parseInt(latestParts[0]);
        return latestMajor > currentMajor;
    }
    
    return false;
}
```

### Changelog Display

```java
UpdateChecker.checkGitHubRelease(MOD_ID, owner, repo, VERSION, result -> {
    if (result.isUpdateAvailable()) {
        LunarLogger.info(MOD_ID, "=== Update Available ===");
        LunarLogger.info(MOD_ID, "Current: {}", result.getCurrentVersion());
        LunarLogger.info(MOD_ID, "Latest: {}", result.getLatestVersion());
        LunarLogger.info(MOD_ID, "Download: {}", result.getDownloadUrl());
        
        if (result.getDescription() != null) {
            LunarLogger.info(MOD_ID, "Changes:");
            LunarLogger.info(MOD_ID, result.getDescription());
        }
        
        LunarLogger.info(MOD_ID, "=======================");
    }
});
```

## Custom JSON Endpoint Format

Your custom endpoint should return JSON in this format:

```json
{
  "version": "1.0.1",
  "downloadUrl": "https://example.com/download/mod-1.0.1.jar",
  "description": "- Fixed bug #123\n- Added new feature\n- Performance improvements"
}
```

**Required:**
- `version` - The latest version string

**Optional:**
- `downloadUrl` - URL to download the update
- `description` - Changelog or description

## GitHub API

The UpdateChecker uses the GitHub Releases API:
```
https://api.github.com/repos/{owner}/{repo}/releases/latest
```

**Rate Limits:**
- 60 requests per hour (unauthenticated)
- 5000 requests per hour (authenticated)

**Response:**
- `tag_name` - Used as version
- `html_url` - Used as download URL
- `body` - Used as description

## Integration with Other Systems

### With LifecycleManager

```java
LifecycleManager.onInitialize(() -> {
    // Check for updates during initialization
    checkForUpdates();
});
```

### With TaskScheduler

```java
// Check for updates 10 seconds after server start
TaskScheduler.runLater(MOD_ID, 200, this::checkForUpdates);

// Periodic check (once per day = 1,728,000 ticks)
TaskScheduler.runRepeating(MOD_ID, 200, 1_728_000, this::checkForUpdates);
```

### With Config

```java
Config config = new Config(MOD_ID);
boolean autoCheck = config.getBoolean("updates.autoCheck", true);
String customUrl = config.getString("updates.customUrl", "");

if (autoCheck) {
    if (!customUrl.isEmpty()) {
        UpdateChecker.checkCustomEndpoint(MOD_ID, customUrl, VERSION, this::handleUpdate);
    } else {
        UpdateChecker.checkGitHubRelease(MOD_ID, owner, repo, VERSION, this::handleUpdate);
    }
}
```

### With EventHelper

```java
EventHelper.forMod(MOD_ID)
    .on(ServerLifecycleEvents.SERVER_STARTED, server -> {
        TaskScheduler.runLater(MOD_ID, 100, this::checkForUpdates);
    })
    .registerAll();
```

## Thread Safety

- All operations are asynchronous
- Callbacks may run on a different thread
- Use `TaskScheduler.runSync()` if you need to access game state in the callback

```java
UpdateChecker.checkGitHubRelease(MOD_ID, owner, repo, VERSION, result -> {
    // This might not be on the main thread!
    
    TaskScheduler.runSync(MOD_ID, () -> {
        // Now safe to access game state
        if (result.isUpdateAvailable()) {
            broadcastToPlayers("Update available!");
        }
    });
});
```

## Error Handling

- Network errors are caught and logged
- HTTP errors (404, 500, etc.) are logged
- JSON parse errors are logged
- Timeouts are handled (5 second connect/read timeout)
- Failed checks return `null` for `latestVersion`

## Performance

- Completely asynchronous (no impact on game performance)
- 5 second timeout for HTTP requests
- Minimal memory usage
- Single request per check
- No caching (each check makes a new request)

## Notes

- Version comparison is exact string match (not semantic versioning)
- GitHub API has rate limits (60 requests/hour unauthenticated)
- Custom endpoints should be HTTPS for security
- Update checks should be infrequent (startup only, or daily at most)
- Consider adding a config option to disable update checks
- Callbacks execute on a background thread (use `runSync` for game state access)

## See Also

- [LifecycleManager](lifecycle-manager.md) - For initialization hooks
- [TaskScheduler](task-scheduler.md) - For delayed checks and thread-safe callbacks
- [Config](config.md) - For update check preferences
- [LunarLogger](logger.md) - For logging update information

