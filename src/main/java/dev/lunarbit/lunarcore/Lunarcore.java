package dev.lunarbit.lunarcore;

import dev.lunarbit.lunarcore.api.lifecycle.LifecycleManager;
import dev.lunarbit.lunarcore.api.log.LunarLogger;
import dev.lunarbit.lunarcore.api.scheduler.TaskScheduler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LunarCore - A lightweight foundational SDK for Minecraft Fabric mods.
 * Provides essential utilities for mod development without gameplay mechanics.
 *
 * Implements LunarCore Specification v1.0.0
 * @see <a href="https://github.com/LunarBit-dev/LunarCore-spec">LunarCore Spec</a>
 */
public class Lunarcore implements ModInitializer {
	public static final String MOD_ID = "lunarcore";

	/**
	 * SDK version (LC-VER-003: Version declaration).
	 * Follows Semantic Versioning 2.0.0 (LC-VER-001)
	 */
	public static final String VERSION = "1.0.0";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LunarLogger.info(MOD_ID, "Initializing LunarCore v{}", VERSION);

		// Trigger lifecycle callbacks
		LifecycleManager.triggerInitialize();

		// Register server lifecycle events for task scheduler
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			TaskScheduler.setServer(server);
			LunarLogger.debug(MOD_ID, "Task scheduler initialized");
		});

		// Register tick event for task scheduler
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			TaskScheduler.tick();
		});

		// Register shutdown event
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			LunarLogger.debug(MOD_ID, "Triggering shutdown callbacks");
			LifecycleManager.triggerShutdown();
		});

		LunarLogger.info(MOD_ID, "LunarCore initialized successfully");
	}
}