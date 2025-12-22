package dev.lunarbit.lunarcore.example;

import dev.lunarbit.lunarcore.api.config.Config;
import dev.lunarbit.lunarcore.api.event.EventHelper;
import dev.lunarbit.lunarcore.api.lifecycle.LifecycleManager;
import dev.lunarbit.lunarcore.api.log.LunarLogger;
import dev.lunarbit.lunarcore.api.scheduler.TaskScheduler;
import dev.lunarbit.lunarcore.api.update.UpdateChecker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * Example mod demonstrating LunarCore SDK usage.
 * This shows how to use all the core features provided by LunarCore.
 */
public class ExampleMod implements ModInitializer {
    public static final String MOD_ID = "lunarcore-example";
    public static final String VERSION = "1.0.0";

    private Config config;

    @Override
    public void onInitialize() {
        // Use LunarLogger for easy logging
        LunarLogger.info(MOD_ID, "Initializing Example Mod");

        // Register lifecycle callbacks
        LifecycleManager.onInitialize(() -> {
            LunarLogger.info(MOD_ID, "Common initialization callback");
            initializeConfig();
        });

        LifecycleManager.onClientInitialize(() -> {
            LunarLogger.info(MOD_ID, "Client-specific initialization");
        });

        LifecycleManager.onServerInitialize(() -> {
            LunarLogger.info(MOD_ID, "Server-specific initialization");
        });

        LifecycleManager.onShutdown(() -> {
            LunarLogger.info(MOD_ID, "Shutting down, saving config...");
            if (config != null) {
                config.save();
            }
        });

        // Register events using EventHelper
        EventHelper.forMod(MOD_ID)
            .on(ServerLifecycleEvents.SERVER_STARTED, server -> {
                LunarLogger.info(MOD_ID, "Server started!");

                // Schedule a delayed task
                TaskScheduler.runLater(MOD_ID, 100, () -> {
                    LunarLogger.info(MOD_ID, "This message appears 5 seconds after server start");
                });

                // Schedule a repeating task
                TaskScheduler.runRepeating(MOD_ID, 0, 1200, () -> {
                    LunarLogger.debug(MOD_ID, "This message repeats every minute");
                });
            })
            .on(ServerLifecycleEvents.SERVER_STOPPING, server -> {
                LunarLogger.info(MOD_ID, "Server stopping!");
            })
            .registerAll();

        // Check for updates asynchronously
        checkForUpdates();

        LunarLogger.info(MOD_ID, "Example Mod initialized successfully");
    }

    /**
     * Initialize and load configuration
     */
    private void initializeConfig() {
        config = new Config(MOD_ID);

        // Load config with defaults
        String serverName = config.getString("server.name", "My Server");
        int maxPlayers = config.getInt("server.maxPlayers", 20);
        boolean enableFeature = config.getBoolean("features.exampleFeature", true);
        double multiplier = config.getDouble("gameplay.multiplier", 1.5);

        LunarLogger.info(MOD_ID, "Config loaded - Server: {}, Max Players: {}",
                        serverName, maxPlayers);

        // Save config (will create file with defaults if it didn't exist)
        config.save();
    }

    /**
     * Check for mod updates
     */
    private void checkForUpdates() {
        UpdateChecker.checkGitHubRelease(
            MOD_ID,
            "LunarBit-dev",
            "LunarCore-spec",
            VERSION,
            result -> {
                if (result.isUpdateAvailable()) {
                    LunarLogger.info(MOD_ID, "Update available! Current: {}, Latest: {}",
                                    result.getCurrentVersion(), result.getLatestVersion());
                    LunarLogger.info(MOD_ID, "Download: {}", result.getDownloadUrl());
                } else {
                    LunarLogger.debug(MOD_ID, "Mod is up to date");
                }
            }
        );
    }
}

