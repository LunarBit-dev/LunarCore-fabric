package dev.lunarbit.lunarcore;

import dev.lunarbit.lunarcore.api.lifecycle.LifecycleManager;
import dev.lunarbit.lunarcore.api.log.LunarLogger;
import net.fabricmc.api.DedicatedServerModInitializer;

/**
 * LunarCore dedicated server initialization.
 */
public class LunarcoreServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        LunarLogger.debug(Lunarcore.MOD_ID, "Initializing LunarCore server");

        // Trigger server lifecycle callbacks
        LifecycleManager.triggerServerInitialize();

        LunarLogger.debug(Lunarcore.MOD_ID, "LunarCore server initialized");
    }
}
