package lib.homies.framework;

import java.io.File;
import java.io.InputStream;

/**
 * A platform-agnostic interface for accessing plugin-specific context and resources.
 * Implementations will wrap platform-specific plugin objects (e.g., Bukkit's JavaPlugin).
 */
public interface PluginContext {
    /**
     * Returns the name of the plugin.
     * @return The plugin's name.
     */
    String getName();

    /**
     * Returns the folder where this plugin's data is stored.
     * @return The plugin's data folder.
     */
    File getDataFolder();

    /**
     * Gets a resource from the plugin's JAR.
     * @param filename The path to the resource.
     * @return An InputStream for the resource, or null if not found.
     */
    InputStream getResource(String filename);

    /**
     * Saves a resource from the plugin's JAR to its data folder.
     * @param resourcePath The path to the resource within the JAR.
     * @param replace If true, replace the existing file; otherwise, do nothing.
     */
    void saveResource(String resourcePath, boolean replace);
}
