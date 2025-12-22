# TaskScheduler

Safe task scheduling system for delayed and repeated tasks. All tasks run on the main server thread to ensure thread safety.

## Features

- Thread-safe task submission
- Delayed task execution
- Repeating/periodic tasks
- Task cancellation
- Automatic error handling
- Main thread execution guarantee
- Per-mod task tracking

## Usage

### Delayed Task

```java
// Run after 5 seconds (100 ticks)
TaskScheduler.runLater(MOD_ID, 100, () -> {
    LunarLogger.info(MOD_ID, "This runs 5 seconds later");
});
```

### Repeating Task

```java
// Run every second (20 ticks), starting after 1 second
TaskScheduler.runRepeating(MOD_ID, 20, 20, () -> {
    LunarLogger.debug(MOD_ID, "This runs every second");
});
```

### Next Tick

```java
// Run on the next tick
TaskScheduler.runNextTick(MOD_ID, () -> {
    LunarLogger.debug(MOD_ID, "Running next tick");
});
```

### Sync Execution

```java
// If on main thread, runs immediately; otherwise, schedules for next tick
TaskScheduler.runSync(MOD_ID, () -> {
    config.save(); // Thread-safe config operation
});
```

### Cancellable Task

```java
ScheduledTask task = TaskScheduler.runRepeating(MOD_ID, 0, 20, () -> {
    checkStatus();
});

// Cancel later
if (shouldStop) {
    task.cancel();
}
```

## API Reference

### `runLater(String modId, int delayTicks, Runnable task)`

Schedule a task to run after a delay.

**Parameters:**
- `modId` - The mod ID (for logging)
- `delayTicks` - The delay in ticks (20 ticks = 1 second)
- `task` - The task to run

**Returns:** A ScheduledTask that can be cancelled

**Example:**
```java
TaskScheduler.runLater("mymod", 100, () -> {
    LunarLogger.info("mymod", "5 seconds have passed");
});
```

### `runRepeating(String modId, int delayTicks, int intervalTicks, Runnable task)`

Schedule a task to run repeatedly.

**Parameters:**
- `modId` - The mod ID (for logging)
- `delayTicks` - The initial delay in ticks
- `intervalTicks` - The interval between runs in ticks
- `task` - The task to run

**Returns:** A ScheduledTask that can be cancelled

**Example:**
```java
// Start after 1 second, repeat every 5 seconds
TaskScheduler.runRepeating("mymod", 20, 100, () -> {
    performPeriodicTask();
});
```

### `runNextTick(String modId, Runnable task)`

Schedule a task to run on the next tick.

**Parameters:**
- `modId` - The mod ID (for logging)
- `task` - The task to run

**Returns:** A ScheduledTask that can be cancelled

**Example:**
```java
TaskScheduler.runNextTick("mymod", () -> {
    updateGameState();
});
```

### `runSync(String modId, Runnable task)`

Execute a task synchronously on the main thread.

**Parameters:**
- `modId` - The mod ID (for logging)
- `task` - The task to run

**Behavior:**
- If already on main thread: executes immediately
- If on another thread: schedules for next tick

**Example:**
```java
// From async context
CompletableFuture.runAsync(() -> {
    Data data = fetchFromNetwork();
    
    TaskScheduler.runSync("mymod", () -> {
        // Now safe to interact with game state
        applyDataToWorld(data);
    });
});
```

### `cancelAll(String modId)`

Cancel all scheduled tasks for a specific mod.

**Parameters:**
- `modId` - The mod ID

**Example:**
```java
// When mod is disabled or unloaded
TaskScheduler.cancelAll("mymod");
```

## ScheduledTask

### `cancel()`

Cancel this task. For repeating tasks, prevents future executions.

**Example:**
```java
ScheduledTask task = TaskScheduler.runRepeating("mymod", 0, 20, () -> {
    if (shouldStop()) {
        task.cancel();
    }
});
```

### `isCancelled()`

Check if this task is cancelled.

**Returns:** `true` if cancelled

**Example:**
```java
if (task.isCancelled()) {
    LunarLogger.info("mymod", "Task was cancelled");
}
```

### `getModId()`

Get the mod ID that scheduled this task.

**Returns:** The mod ID

**Example:**
```java
String modId = task.getModId();
```

## Best Practices

### 1. Use Appropriate Delays

```java
// GOOD - 5 seconds for non-critical delayed tasks
TaskScheduler.runLater(MOD_ID, 100, this::updateStatus);

// GOOD - 1 minute for periodic checks
TaskScheduler.runRepeating(MOD_ID, 1200, 1200, this::checkUpdates);

// BAD - Don't use every tick for non-critical tasks
// TaskScheduler.runRepeating(MOD_ID, 0, 1, this::expensiveOperation);
```

### 2. Cancel Tasks When No Longer Needed

```java
private ScheduledTask updateTask;

public void startUpdates() {
    updateTask = TaskScheduler.runRepeating(MOD_ID, 0, 20, this::update);
}

public void stopUpdates() {
    if (updateTask != null) {
        updateTask.cancel();
        updateTask = null;
    }
}
```

### 3. Handle Errors in Tasks

```java
TaskScheduler.runLater(MOD_ID, 100, () -> {
    try {
        riskyOperation();
    } catch (Exception e) {
        LunarLogger.error(MOD_ID, "Error in scheduled task", e);
    }
});
```

### 4. Use runSync for Thread-Safe Operations

```java
// Async operation followed by sync update
CompletableFuture.supplyAsync(() -> {
    return downloadData();
}).thenAccept(data -> {
    TaskScheduler.runSync(MOD_ID, () -> {
        updateGameState(data);
    });
});
```

### 5. Store Task References for Cancellation

```java
public class MyMod {
    private final List<ScheduledTask> tasks = new ArrayList<>();
    
    public void scheduleTask() {
        ScheduledTask task = TaskScheduler.runRepeating(MOD_ID, 0, 100, () -> {
            doWork();
        });
        tasks.add(task);
    }
    
    public void shutdown() {
        tasks.forEach(ScheduledTask::cancel);
        tasks.clear();
    }
}
```

## Common Patterns

### Delayed Initialization

```java
EventHelper.register(ServerLifecycleEvents.SERVER_STARTED, server -> {
    // Wait for server to fully start before initializing
    TaskScheduler.runLater(MOD_ID, 100, () -> {
        initializeAfterServerStart();
    });
}, MOD_ID);
```

### Periodic Saves

```java
TaskScheduler.runRepeating(MOD_ID, 6000, 6000, () -> {
    LunarLogger.info(MOD_ID, "Auto-saving...");
    config.save();
    saveGameData();
});
```

### Delayed Broadcast

```java
public void broadcastDelayed(String message, int seconds) {
    int ticks = seconds * 20;
    TaskScheduler.runLater(MOD_ID, ticks, () -> {
        server.getPlayerManager().broadcast(
            Text.literal(message), false
        );
    });
}
```

### Countdown Timer

```java
public void startCountdown(int seconds) {
    for (int i = seconds; i > 0; i--) {
        int finalI = i;
        TaskScheduler.runLater(MOD_ID, (seconds - i) * 20, () -> {
            LunarLogger.info(MOD_ID, "Countdown: {}", finalI);
        });
    }
    
    TaskScheduler.runLater(MOD_ID, seconds * 20, () -> {
        LunarLogger.info(MOD_ID, "Go!");
    });
}
```

### Rate Limiting

```java
private long lastExecution = 0;
private static final long COOLDOWN = 5000; // 5 seconds

public void executeWithCooldown() {
    long now = System.currentTimeMillis();
    long remaining = COOLDOWN - (now - lastExecution);
    
    if (remaining > 0) {
        int ticks = (int) (remaining / 50);
        TaskScheduler.runLater(MOD_ID, ticks, this::doAction);
    } else {
        doAction();
    }
    
    lastExecution = now;
}
```

### Debouncing

```java
private ScheduledTask debounceTask;

public void onInput() {
    // Cancel previous debounce
    if (debounceTask != null) {
        debounceTask.cancel();
    }
    
    // Schedule new action
    debounceTask = TaskScheduler.runLater(MOD_ID, 10, () -> {
        processInput();
    });
}
```

### Retry Logic

```java
public void retryOperation(int maxAttempts) {
    retryOperation(maxAttempts, 0);
}

private void retryOperation(int maxAttempts, int attempt) {
    try {
        riskyOperation();
        LunarLogger.info(MOD_ID, "Operation succeeded");
    } catch (Exception e) {
        if (attempt < maxAttempts - 1) {
            LunarLogger.warn(MOD_ID, "Operation failed, retrying in 5s...");
            TaskScheduler.runLater(MOD_ID, 100, () -> {
                retryOperation(maxAttempts, attempt + 1);
            });
        } else {
            LunarLogger.error(MOD_ID, "Operation failed after {} attempts", 
                maxAttempts, e);
        }
    }
}
```

### Async -> Sync Pattern

```java
public void loadDataAsync() {
    CompletableFuture.runAsync(() -> {
        // Heavy I/O operation
        Data data = loadFromDisk();
        
        // Switch back to main thread
        TaskScheduler.runSync(MOD_ID, () -> {
            applyData(data);
            LunarLogger.info(MOD_ID, "Data loaded and applied");
        });
    });
}
```

## Time Conversions

```java
// Common time conversions (20 ticks = 1 second)
int oneSecond = 20;
int fiveSeconds = 100;
int tenSeconds = 200;
int thirtySeconds = 600;
int oneMinute = 1200;
int fiveMinutes = 6000;
int tenMinutes = 12000;
int oneHour = 72000;
```

## Integration with Other Systems

### With LifecycleManager

```java
LifecycleManager.onInitialize(() -> {
    TaskScheduler.runLater(MOD_ID, 100, () -> {
        LunarLogger.info(MOD_ID, "Delayed initialization");
    });
});

LifecycleManager.onShutdown(() -> {
    TaskScheduler.cancelAll(MOD_ID);
});
```

### With EventHelper

```java
EventHelper.forMod(MOD_ID)
    .on(ServerLifecycleEvents.SERVER_STARTED, server -> {
        TaskScheduler.runRepeating(MOD_ID, 0, 1200, () -> {
            performPeriodicTask();
        });
    })
    .registerAll();
```

### With Config

```java
Config config = new Config(MOD_ID);

// Thread-safe config save
TaskScheduler.runSync(MOD_ID, () -> {
    config.save();
});

// Auto-save every 5 minutes
TaskScheduler.runRepeating(MOD_ID, 6000, 6000, () -> {
    config.save();
    LunarLogger.info(MOD_ID, "Config auto-saved");
});
```

### With UpdateChecker

```java
// Check for updates 10 seconds after server start
TaskScheduler.runLater(MOD_ID, 200, () -> {
    UpdateChecker.checkGitHubRelease(
        MOD_ID, "owner", "repo", VERSION,
        result -> {
            if (result.isUpdateAvailable()) {
                LunarLogger.info(MOD_ID, "Update available!");
            }
        }
    );
});
```

## Thread Safety

- **Task submission**: Thread-safe, can be called from any thread
- **Task execution**: Always runs on main server thread
- **Cancellation**: Thread-safe
- **Queries (isCancelled, getModId)**: Thread-safe

## Performance

- Minimal overhead per tick
- Tasks are processed in O(n) time where n is active tasks
- Cancelled tasks are immediately removed
- No memory leaks from completed tasks
- Efficient for hundreds of scheduled tasks

## Error Handling

- Exceptions in tasks are caught and logged
- Failed tasks don't affect other tasks
- Repeating tasks are cancelled if they throw an exception
- Error messages include the mod ID for easy debugging

## Notes

- 20 ticks = 1 second (at normal server speed)
- Tasks execute at the end of the server tick
- Minimum delay is 1 tick (use `runNextTick()`)
- Zero delay means "next tick" for runRepeating
- Tasks are executed in order of completion time
- Repeating tasks reschedule themselves automatically
- Server lag affects timing (tasks may run late, never early)

## See Also

- [LifecycleManager](lifecycle-manager.md) - For initialization and shutdown
- [EventHelper](event-helper.md) - For event registration
- [LunarLogger](logger.md) - For logging in tasks
- [Config](config.md) - For thread-safe config operations

