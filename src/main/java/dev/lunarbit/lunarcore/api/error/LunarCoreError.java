package dev.lunarbit.lunarcore.api.error;

/**
 * Base class for all LunarCore errors.
 * Implements LC-ERR-001: Error Hierarchy
 */
public class LunarCoreError extends RuntimeException {
    private final String code;
    private final String timestamp;

    public LunarCoreError(String message, String code) {
        super(message);
        this.code = code;
        this.timestamp = java.time.Instant.now().toString();
    }

    public LunarCoreError(String message, String code, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.timestamp = java.time.Instant.now().toString();
    }

    public String getCode() {
        return code;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getErrorName() {
        return this.getClass().getSimpleName();
    }
}
