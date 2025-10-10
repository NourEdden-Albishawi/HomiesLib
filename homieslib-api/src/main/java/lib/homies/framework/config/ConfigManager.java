package lib.homies.framework.config;

import lib.homies.framework.PluginContext;
import lib.homies.framework.config.annotations.ConfigFile;

/**
 * A platform-agnostic interface for managing configuration files.
 * Framework developers will use this service to load and save their plugin's configuration.
 */
public interface ConfigManager {

    /**
     * Loads a configuration class, creating the file with default values if it doesn't exist.
     * The configuration class must be a POJO annotated with {@link ConfigFile}.
     * Fields within the class intended for configuration should be annotated with {@link lib.homies.framework.config.annotations.ConfigKey}.
     *
     * @param pluginContext The context of the plugin calling this method, used to access its data folder and resources.
     * @param configClass The class representing the configuration.
     * @return An instance of the configuration class populated with values from the file.
     * @param <T> The type of the configuration class.
     * @throws IllegalArgumentException if the configClass is not annotated with @ConfigFile.
     * @throws RuntimeException if loading or saving fails due to I/O or reflection errors.
     */
    <T> T loadConfig(PluginContext pluginContext, Class<T> configClass);

    /**
     * Saves the current state of a configuration object back to its file.
     * The configuration object must be an instance of a class annotated with {@link ConfigFile}.
     *
     * @param pluginContext The context of the plugin calling this method, used to access its data folder.
     * @param configInstance The configuration object to save.
     * @throws IllegalArgumentException if the configInstance's class is not annotated with @ConfigFile.
     * @throws RuntimeException if saving fails due to I/O or reflection errors.
     */
    void saveConfig(PluginContext pluginContext, Object configInstance);
}
