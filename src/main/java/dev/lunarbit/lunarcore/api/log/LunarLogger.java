package dev.lunarbit.lunarcore.api.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Enhanced logger wrapper providing simplified logging methods.
 * Automatically manages logger instances per mod ID.
 * Implements LC-LOG-005: Component naming with LunarCore. prefix
 * Implements LC-LOG-007: Sensitive data redaction
 */
public class LunarLogger {
    private static final Map<String, Logger> loggers = new HashMap<>();
    private static final Pattern SENSITIVE_PATTERN = Pattern.compile(
        "(password|token|secret|key|apikey|api_key)\\s*[:=]\\s*\\S+",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Get or create a logger for the specified mod ID.
     * Follows LC-LOG-005: Component naming convention (LunarCore.ModId)
     * @param modId The mod ID
     * @return The logger instance
     */
    private static Logger getLogger(String modId) {
        return loggers.computeIfAbsent(modId, id -> {
            // LC-LOG-005: Component naming - LunarCore.ModId
            String componentName = "LunarCore." + capitalize(id);
            return LoggerFactory.getLogger(componentName);
        });
    }

    /**
     * Capitalize first letter of mod ID for component naming.
     */
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * Redact sensitive data from log messages (LC-LOG-007).
     * @param message The message to redact
     * @return Redacted message
     */
    private static String redactSensitiveData(String message) {
        if (message == null) {
            return null;
        }
        return SENSITIVE_PATTERN.matcher(message).replaceAll("$1: [REDACTED]");
    }

    /**
     * Log an info message.
     * @param modId The mod ID
     * @param message The message to log
     */
    public static void info(String modId, String message) {
        getLogger(modId).info(redactSensitiveData(message));
    }

    /**
     * Log an info message with formatting.
     * @param modId The mod ID
     * @param format The message format
     * @param args The format arguments
     */
    public static void info(String modId, String format, Object... args) {
        getLogger(modId).info(redactSensitiveData(format), args);
    }

    /**
     * Log a warning message.
     * @param modId The mod ID
     * @param message The message to log
     */
    public static void warn(String modId, String message) {
        getLogger(modId).warn(redactSensitiveData(message));
    }

    /**
     * Log a warning message with formatting.
     * @param modId The mod ID
     * @param format The message format
     * @param args The format arguments
     */
    public static void warn(String modId, String format, Object... args) {
        getLogger(modId).warn(redactSensitiveData(format), args);
    }

    /**
     * Log an error message.
     * @param modId The mod ID
     * @param message The message to log
     */
    public static void error(String modId, String message) {
        getLogger(modId).error(redactSensitiveData(message));
    }

    /**
     * Log an error message with an exception.
     * @param modId The mod ID
     * @param message The message to log
     * @param throwable The exception
     */
    public static void error(String modId, String message, Throwable throwable) {
        getLogger(modId).error(redactSensitiveData(message), throwable);
    }

    /**
     * Log an error message with formatting.
     * @param modId The mod ID
     * @param format The message format
     * @param args The format arguments
     */
    public static void error(String modId, String format, Object... args) {
        getLogger(modId).error(redactSensitiveData(format), args);
    }

    /**
     * Log a debug message.
     * @param modId The mod ID
     * @param message The message to log
     */
    public static void debug(String modId, String message) {
        getLogger(modId).debug(redactSensitiveData(message));
    }

    /**
     * Log a debug message with formatting.
     * @param modId The mod ID
     * @param format The message format
     * @param args The format arguments
     */
    public static void debug(String modId, String format, Object... args) {
        getLogger(modId).debug(redactSensitiveData(format), args);
    }

    /**
     * Log a trace message.
     * @param modId The mod ID
     * @param message The message to log
     */
    public static void trace(String modId, String message) {
        getLogger(modId).trace(redactSensitiveData(message));
    }

    /**
     * Check if debug logging is enabled for a mod.
     * @param modId The mod ID
     * @return true if debug is enabled
     */
    public static boolean isDebugEnabled(String modId) {
        return getLogger(modId).isDebugEnabled();
    }

    /**
     * Get the raw SLF4J logger instance.
     * @param modId The mod ID
     * @return The logger instance
     */
    public static Logger getLoggerInstance(String modId) {
        return getLogger(modId);
    }
}

