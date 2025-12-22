package dev.lunarbit.lunarcore.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lunarbit.lunarcore.api.log.LunarLogger;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Simple configuration abstraction using JSON for storage.
 * Provides easy-to-use methods for reading and writing config values.
 * Implements LC-CFG-007: Sensitive value handling
 */
public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<String> SENSITIVE_KEYS = Arrays.asList(
        "password", "secret", "token", "key", "apikey", "api_key"
    );

    private final Path configPath;
    private JsonObject data;
    private final String modId;

    /**
     * Create a new config instance.
     * @param modId The mod ID (used for logging and default filename)
     */
    public Config(String modId) {
        this(modId, modId + ".json");
    }

    /**
     * Create a new config instance with a custom filename.
     * @param modId The mod ID (used for logging)
     * @param filename The config filename
     */
    public Config(String modId, String filename) {
        this.modId = modId;
        this.configPath = FabricLoader.getInstance().getConfigDir().resolve(filename);
        this.data = new JsonObject();
        load();
    }

    /**
     * Load the configuration from disk.
     * @return true if loaded successfully, false otherwise
     */
    public boolean load() {
        try {
            if (Files.exists(configPath)) {
                String content = Files.readString(configPath);
                data = JsonParser.parseString(content).getAsJsonObject();
                LunarLogger.info(modId, "Config loaded from " + configPath.getFileName());
                return true;
            } else {
                LunarLogger.info(modId, "Config file not found, using defaults");
                return false;
            }
        } catch (IOException e) {
            LunarLogger.error(modId, "Failed to load config", e);
            return false;
        }
    }

    /**
     * Save the configuration to disk.
     * @return true if saved successfully, false otherwise
     */
    public boolean save() {
        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, GSON.toJson(data));
            LunarLogger.info(modId, "Config saved to " + configPath.getFileName());
            return true;
        } catch (IOException e) {
            LunarLogger.error(modId, "Failed to save config", e);
            return false;
        }
    }

    /**
     * Get a string value from the config.
     * @param key The config key
     * @param defaultValue The default value if key doesn't exist
     * @return The value or default
     */
    public String getString(String key, String defaultValue) {
        if (data.has(key)) {
            return data.get(key).getAsString();
        }
        data.addProperty(key, defaultValue);
        return defaultValue;
    }

    /**
     * Get an integer value from the config.
     * @param key The config key
     * @param defaultValue The default value if key doesn't exist
     * @return The value or default
     */
    public int getInt(String key, int defaultValue) {
        if (data.has(key)) {
            return data.get(key).getAsInt();
        }
        data.addProperty(key, defaultValue);
        return defaultValue;
    }

    /**
     * Get a boolean value from the config.
     * @param key The config key
     * @param defaultValue The default value if key doesn't exist
     * @return The value or default
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        if (data.has(key)) {
            return data.get(key).getAsBoolean();
        }
        data.addProperty(key, defaultValue);
        return defaultValue;
    }

    /**
     * Get a double value from the config.
     * @param key The config key
     * @param defaultValue The default value if key doesn't exist
     * @return The value or default
     */
    public double getDouble(String key, double defaultValue) {
        if (data.has(key)) {
            return data.get(key).getAsDouble();
        }
        data.addProperty(key, defaultValue);
        return defaultValue;
    }

    /**
     * Set a value in the config.
     * @param key The config key
     * @param value The value to set
     */
    public void set(String key, Object value) {
        if (value instanceof String) {
            data.addProperty(key, (String) value);
        } else if (value instanceof Number) {
            data.addProperty(key, (Number) value);
        } else if (value instanceof Boolean) {
            data.addProperty(key, (Boolean) value);
        } else {
            data.addProperty(key, value.toString());
        }
    }

    /**
     * Check if a key exists in the config.
     * @param key The config key
     * @return true if the key exists
     */
    public boolean has(String key) {
        return data.has(key);
    }

    /**
     * Remove a key from the config.
     * @param key The config key
     */
    public void remove(String key) {
        data.remove(key);
    }

    /**
     * Get the underlying JsonObject for advanced usage.
     * @return The JsonObject containing all config data
     */
    public JsonObject getData() {
        return data;
    }

    /**
     * Check if a key contains sensitive data (LC-CFG-007).
     * @param key The config key
     * @return true if the key is sensitive
     */
    private boolean isSensitiveKey(String key) {
        String lowerKey = key.toLowerCase();
        return SENSITIVE_KEYS.stream().anyMatch(lowerKey::contains);
    }

    /**
     * Redact sensitive value for logging (LC-CFG-007).
     * @param key The config key
     * @param value The value
     * @return Redacted value if sensitive, otherwise original
     */
    private String redactIfSensitive(String key, Object value) {
        if (isSensitiveKey(key)) {
            return "[REDACTED]";
        }
        return String.valueOf(value);
    }
}

