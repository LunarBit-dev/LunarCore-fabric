package dev.lunarbit.lunarcore.api.scheduler;

import dev.lunarbit.lunarcore.api.log.LunarLogger;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * Safe task scheduling system for delayed and repeated tasks.
 * All tasks run on the main server thread to ensure thread safety.
 */
public class TaskScheduler {
    private static final ConcurrentLinkedQueue<ScheduledTask> pendingTasks = new ConcurrentLinkedQueue<>();
    private static final List<ScheduledTask> activeTasks = new ArrayList<>();
    private static MinecraftServer server;

    /**
     * Internal method to set the server instance.
     * @param server The Minecraft server
     */
    public static void setServer(MinecraftServer server) {
        TaskScheduler.server = server;
    }

    /**
     * Schedule a task to run after a delay.
     * @param modId The mod ID (for logging)
     * @param delayTicks The delay in ticks (20 ticks = 1 second)
     * @param task The task to run
     * @return A ScheduledTask that can be cancelled
     */
    public static ScheduledTask runLater(String modId, int delayTicks, Runnable task) {
        ScheduledTask scheduledTask = new ScheduledTask(modId, task, delayTicks, 0, false);
        pendingTasks.add(scheduledTask);
        return scheduledTask;
    }

    /**
     * Schedule a task to run repeatedly.
     * @param modId The mod ID (for logging)
     * @param delayTicks The initial delay in ticks
     * @param intervalTicks The interval between runs in ticks
     * @param task The task to run
     * @return A ScheduledTask that can be cancelled
     */
    public static ScheduledTask runRepeating(String modId, int delayTicks, int intervalTicks, Runnable task) {
        ScheduledTask scheduledTask = new ScheduledTask(modId, task, delayTicks, intervalTicks, true);
        pendingTasks.add(scheduledTask);
        return scheduledTask;
    }

    /**
     * Schedule a task to run on the next tick.
     * @param modId The mod ID (for logging)
     * @param task The task to run
     * @return A ScheduledTask that can be cancelled
     */
    public static ScheduledTask runNextTick(String modId, Runnable task) {
        return runLater(modId, 1, task);
    }

    /**
     * Execute a task synchronously on the main thread.
     * If already on the main thread, executes immediately.
     * @param modId The mod ID (for logging)
     * @param task The task to run
     */
    public static void runSync(String modId, Runnable task) {
        if (server != null && server.isSameThread()) {
            task.run();
        } else {
            runNextTick(modId, task);
        }
    }

    /**
     * Internal method to tick all scheduled tasks.
     */
    public static void tick() {
        // Add any pending tasks
        while (!pendingTasks.isEmpty()) {
            activeTasks.add(pendingTasks.poll());
        }

        // Process active tasks
        Iterator<ScheduledTask> iterator = activeTasks.iterator();
        while (iterator.hasNext()) {
            ScheduledTask task = iterator.next();
            if (task.isCancelled()) {
                iterator.remove();
                continue;
            }

            if (task.tick()) {
                iterator.remove();
            }
        }
    }

    /**
     * Cancel all scheduled tasks for a specific mod.
     * @param modId The mod ID
     */
    public static void cancelAll(String modId) {
        activeTasks.removeIf(task -> {
            if (task.getModId().equals(modId)) {
                task.cancel();
                return true;
            }
            return false;
        });
    }

    /**
     * Represents a scheduled task.
     */
    public static class ScheduledTask {
        private final String modId;
        private final Runnable task;
        private int remainingTicks;
        private final int intervalTicks;
        private final boolean repeating;
        private boolean cancelled = false;

        private ScheduledTask(String modId, Runnable task, int delayTicks, int intervalTicks, boolean repeating) {
            this.modId = modId;
            this.task = task;
            this.remainingTicks = delayTicks;
            this.intervalTicks = intervalTicks;
            this.repeating = repeating;
        }

        /**
         * Cancel this task.
         */
        public void cancel() {
            this.cancelled = true;
        }

        /**
         * Check if this task is cancelled.
         * @return true if cancelled
         */
        public boolean isCancelled() {
            return cancelled;
        }

        /**
         * Get the mod ID that scheduled this task.
         * @return The mod ID
         */
        public String getModId() {
            return modId;
        }

        /**
         * Internal method to tick this task.
         * @return true if the task should be removed
         */
        private boolean tick() {
            if (cancelled) {
                return true;
            }

            remainingTicks--;
            if (remainingTicks <= 0) {
                try {
                    task.run();
                } catch (Exception e) {
                    LunarLogger.error(modId, "Error executing scheduled task", e);
                    return true;
                }

                if (repeating) {
                    remainingTicks = intervalTicks;
                    return false;
                } else {
                    return true;
                }
            }
            return false;
        }
    }
}

