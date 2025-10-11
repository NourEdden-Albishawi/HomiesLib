package lib.homies.framework.config;

import lib.homies.framework.PluginContext;
import lib.homies.framework.config.annotations.ConfigFile;

import java.util.concurrent.CompletableFuture;

/**
 * A platform-agnostic interface for managing configuration files.
 * All operations are performed asynchronously to prevent blocking the main server thread.
 */
public interface ConfigManager {

    /**
     * Asynchronously loads a configuration class, creating the file with default values if it doesn't exist.
     * The configuration class must be a POJO annotated with {@link ConfigFile}.
     * Fields within the class intended for configuration should be annotated with {@link lib.homies.framework.config.annotations.ConfigKey}.
     *
     * @param pluginContext The context of the plugin calling this method, used to access its data folder and resources.
     * @param configClass The class representing the configuration.
     * @param <T> The type of the configuration class.
     * @return A {@link CompletableFuture} that will complete with an instance of the configuration class.
     */
    <T> CompletableFuture<T> loadConfig(PluginContext pluginContext, Class<T> configClass);

    /**
     * Asynchronously saves the current state of a configuration object back to its file.
     * The configuration object must be an instance of a class annotated with {@link ConfigFile}.
     *
     * @param pluginContext The context of the plugin calling this method, used to access its data folder.
     * @param configInstance The configuration object to save.
     * @return A {@link CompletableFuture} that will complete when the save operation is finished.
     */
    CompletableFuture<Void> saveConfig(PluginContext pluginContext, Object configInstance);
}
