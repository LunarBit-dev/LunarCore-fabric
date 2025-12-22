package dev.lunarbit.lunarcore.api.lifecycle;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages mod lifecycle callbacks for initialization and shutdown phases.
 * Provides a simple way to register lifecycle hooks without implementing multiple interfaces.
 */
public class LifecycleManager {
    private static final List<Runnable> initCallbacks = new ArrayList<>();
    private static final List<Runnable> clientInitCallbacks = new ArrayList<>();
    private static final List<Runnable> serverInitCallbacks = new ArrayList<>();
    private static final List<Runnable> shutdownCallbacks = new ArrayList<>();

    private static boolean initialized = false;
    private static boolean clientInitialized = false;
    private static boolean serverInitialized = false;

    /**
     * Register a callback to be executed during common initialization.
     * @param callback The callback to execute
     */
    public static void onInitialize(Runnable callback) {
        if (initialized) {
            callback.run();
        } else {
            initCallbacks.add(callback);
        }
    }

    /**
     * Register a callback to be executed during client initialization.
     * @param callback The callback to execute
     */
    public static void onClientInitialize(Runnable callback) {
        if (clientInitialized) {
            callback.run();
        } else {
            clientInitCallbacks.add(callback);
        }
    }

    /**
     * Register a callback to be executed during server initialization.
     * @param callback The callback to execute
     */
    public static void onServerInitialize(Runnable callback) {
        if (serverInitialized) {
            callback.run();
        } else {
            serverInitCallbacks.add(callback);
        }
    }

    /**
     * Register a callback to be executed during shutdown.
     * Shutdown callbacks are executed in reverse order of registration.
     * @param callback The callback to execute
     */
    public static void onShutdown(Runnable callback) {
        shutdownCallbacks.add(0, callback);
    }

    /**
     * Internal method to trigger initialization callbacks.
     */
    public static void triggerInitialize() {
        if (!initialized) {
            initialized = true;
            initCallbacks.forEach(Runnable::run);
            initCallbacks.clear();
        }
    }

    /**
     * Internal method to trigger client initialization callbacks.
     */
    public static void triggerClientInitialize() {
        if (!clientInitialized) {
            clientInitialized = true;
            clientInitCallbacks.forEach(Runnable::run);
            clientInitCallbacks.clear();
        }
    }

    /**
     * Internal method to trigger server initialization callbacks.
     */
    public static void triggerServerInitialize() {
        if (!serverInitialized) {
            serverInitialized = true;
            serverInitCallbacks.forEach(Runnable::run);
            serverInitCallbacks.clear();
        }
    }

    /**
     * Internal method to trigger shutdown callbacks.
     */
    public static void triggerShutdown() {
        shutdownCallbacks.forEach(Runnable::run);
    }
}


