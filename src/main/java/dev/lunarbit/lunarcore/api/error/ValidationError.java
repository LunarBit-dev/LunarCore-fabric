package dev.lunarbit.lunarcore.api.error;

/**
 * Validation-related errors.
 * Implements LC-ERR-001: Error Hierarchy
 */
public class ValidationError extends LunarCoreError {
    public ValidationError(String message) {
        super(message, "LC_ERR_VAL_001");
    }

    public ValidationError(String message, Throwable cause) {
        super(message, "LC_ERR_VAL_001", cause);
    }
}

