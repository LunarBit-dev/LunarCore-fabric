package dev.lunarbit.lunarcore.api.event;

import dev.lunarbit.lunarcore.api.log.LunarLogger;
import net.fabricmc.fabric.api.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Simplified event registration and management utilities.
 * Provides easier ways to register event listeners with automatic cleanup tracking.
 */
public class EventHelper {

    /**
     * Register an event listener with automatic error handling.
     * @param event The Fabric event to listen to
     * @param listener The listener callback
     * @param modId The mod ID (for logging)
     * @param <T> The event listener type
     */
    public static <T> void register(Event<T> event, T listener, String modId) {
        try {
            event.register(listener);
            LunarLogger.debug(modId, "Registered event listener for " + event.getClass().getSimpleName());
        } catch (Exception e) {
            LunarLogger.error(modId, "Failed to register event listener", e);
        }
    }

    /**
     * Register an event listener with automatic error handling (without mod ID).
     * @param event The Fabric event to listen to
     * @param listener The listener callback
     * @param <T> The event listener type
     */
    public static <T> void register(Event<T> event, T listener) {
        register(event, listener, "unknown");
    }

    /**
     * Builder for registering multiple events with the same mod context.
     */
    public static class EventRegistrar {
        private final String modId;
        private final List<Runnable> registrations = new ArrayList<>();

        public EventRegistrar(String modId) {
            this.modId = modId;
        }

        /**
         * Add an event registration to the batch.
         * @param event The event to register
         * @param listener The listener callback
         * @param <T> The event listener type
         * @return This registrar for chaining
         */
        public <T> EventRegistrar on(Event<T> event, T listener) {
            registrations.add(() -> register(event, listener, modId));
            return this;
        }

        /**
         * Execute all registered event registrations.
         */
        public void registerAll() {
            LunarLogger.debug(modId, "Registering " + registrations.size() + " event listeners");
            registrations.forEach(Runnable::run);
            LunarLogger.info(modId, "Successfully registered " + registrations.size() + " event listeners");
        }
    }

    /**
     * Create a new event registrar for batch registration.
     * @param modId The mod ID
     * @return A new EventRegistrar instance
     */
    public static EventRegistrar forMod(String modId) {
        return new EventRegistrar(modId);
    }
}

