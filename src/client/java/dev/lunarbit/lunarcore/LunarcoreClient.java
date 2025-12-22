package dev.lunarbit.lunarcore;

import dev.lunarbit.lunarcore.api.lifecycle.LifecycleManager;
import dev.lunarbit.lunarcore.api.log.LunarLogger;
import net.fabricmc.api.ClientModInitializer;

/**
 * LunarCore client-side initialization.
 */
public class LunarcoreClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		LunarLogger.debug(Lunarcore.MOD_ID, "Initializing LunarCore client");

		// Trigger client lifecycle callbacks
		LifecycleManager.triggerClientInitialize();

		LunarLogger.debug(Lunarcore.MOD_ID, "LunarCore client initialized");
	}
}