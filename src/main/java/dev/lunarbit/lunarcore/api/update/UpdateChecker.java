package dev.lunarbit.lunarcore.api.update;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lunarbit.lunarcore.api.log.LunarLogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Simple version and update checker for mods.
 * Checks for updates asynchronously without blocking the game.
 */
public class UpdateChecker {

    /**
     * Check for updates from a GitHub release.
     * @param modId The mod ID (for logging)
     * @param repoOwner The GitHub repository owner
     * @param repoName The GitHub repository name
     * @param currentVersion The current mod version
     * @param callback Callback with the update result
     */
    public static void checkGitHubRelease(String modId, String repoOwner, String repoName,
                                         String currentVersion, Consumer<UpdateResult> callback) {
        CompletableFuture.runAsync(() -> {
            try {
                String url = String.format("https://api.github.com/repos/%s/%s/releases/latest",
                                         repoOwner, repoName);

                HttpURLConnection connection = (HttpURLConnection) new URI(url).toURL().openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "LunarCore-UpdateChecker");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
                    String latestVersion = json.get("tag_name").getAsString();
                    String downloadUrl = json.get("html_url").getAsString();
                    String description = json.has("body") ? json.get("body").getAsString() : "";

                    boolean updateAvailable = !currentVersion.equals(latestVersion) &&
                                            !latestVersion.isEmpty();

                    UpdateResult result = new UpdateResult(
                        currentVersion,
                        latestVersion,
                        updateAvailable,
                        downloadUrl,
                        description
                    );

                    callback.accept(result);

                    if (updateAvailable) {
                        LunarLogger.info(modId, "Update available: {} -> {}", currentVersion, latestVersion);
                    } else {
                        LunarLogger.debug(modId, "No updates available (current: {})", currentVersion);
                    }
                } else {
                    LunarLogger.warn(modId, "Failed to check for updates: HTTP {}", responseCode);
                    callback.accept(new UpdateResult(currentVersion, null, false, null, null));
                }

                connection.disconnect();
            } catch (Exception e) {
                LunarLogger.error(modId, "Error checking for updates", e);
                callback.accept(new UpdateResult(currentVersion, null, false, null, null));
            }
        });
    }

    /**
     * Check for updates from a custom JSON endpoint.
     * Expected JSON format: {"version": "1.0.0", "downloadUrl": "...", "description": "..."}
     * @param modId The mod ID (for logging)
     * @param updateUrl The URL to check for updates
     * @param currentVersion The current mod version
     * @param callback Callback with the update result
     */
    public static void checkCustomEndpoint(String modId, String updateUrl,
                                          String currentVersion, Consumer<UpdateResult> callback) {
        CompletableFuture.runAsync(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URI(updateUrl).toURL().openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "LunarCore-UpdateChecker");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
                    String latestVersion = json.get("version").getAsString();
                    String downloadUrl = json.has("downloadUrl") ? json.get("downloadUrl").getAsString() : null;
                    String description = json.has("description") ? json.get("description").getAsString() : "";

                    boolean updateAvailable = !currentVersion.equals(latestVersion);

                    UpdateResult result = new UpdateResult(
                        currentVersion,
                        latestVersion,
                        updateAvailable,
                        downloadUrl,
                        description
                    );

                    callback.accept(result);

                    if (updateAvailable) {
                        LunarLogger.info(modId, "Update available: {} -> {}", currentVersion, latestVersion);
                    }
                } else {
                    LunarLogger.warn(modId, "Failed to check for updates: HTTP {}", responseCode);
                    callback.accept(new UpdateResult(currentVersion, null, false, null, null));
                }

                connection.disconnect();
            } catch (Exception e) {
                LunarLogger.error(modId, "Error checking for updates", e);
                callback.accept(new UpdateResult(currentVersion, null, false, null, null));
            }
        });
    }

    /**
     * Represents the result of an update check.
     */
    public static class UpdateResult {
        private final String currentVersion;
        private final String latestVersion;
        private final boolean updateAvailable;
        private final String downloadUrl;
        private final String description;

        public UpdateResult(String currentVersion, String latestVersion, boolean updateAvailable,
                          String downloadUrl, String description) {
            this.currentVersion = currentVersion;
            this.latestVersion = latestVersion;
            this.updateAvailable = updateAvailable;
            this.downloadUrl = downloadUrl;
            this.description = description;
        }

        public String getCurrentVersion() {
            return currentVersion;
        }

        public String getLatestVersion() {
            return latestVersion;
        }

        public boolean isUpdateAvailable() {
            return updateAvailable;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public String getDescription() {
            return description;
        }
    }
}

