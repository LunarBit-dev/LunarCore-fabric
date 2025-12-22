package dev.lunarbit.lunarcore.api.error;

/**
 * Configuration-related errors.
 * Implements LC-ERR-001: Error Hierarchy
 */
public class ConfigurationError extends LunarCoreError {
    public ConfigurationError(String message) {
        super(message, "LC_ERR_CFG_001");
    }

    public ConfigurationError(String message, Throwable cause) {
        super(message, "LC_ERR_CFG_001", cause);
    }
}

