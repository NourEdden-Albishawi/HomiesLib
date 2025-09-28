package lib.homies.framework.spigot.config;

import lib.homies.framework.PluginContext;
import lib.homies.framework.config.ConfigManager;
import lib.homies.framework.config.annotations.ConfigFile;
import lib.homies.framework.config.annotations.ConfigKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * Spigot-specific implementation of the {@link ConfigManager} interface.
 * This class handles loading and saving configuration files using Bukkit's {@link YamlConfiguration}.
 * It supports annotation-driven configuration classes with {@link ConfigFile} and {@link ConfigKey}.
 */
public class SpigotConfigManager implements ConfigManager {

    /**
     * Constructs a new SpigotConfigManager.
     * The plugin instance is no longer directly managed by the manager itself,
     * but passed with each load/save operation via {@link PluginContext}.
     */
    public SpigotConfigManager() {
        // No plugin parameter needed in constructor anymore
    }

    /**
     * Loads a configuration class, creating the file with default values if it doesn't exist.
     * The configuration class must be a POJO annotated with {@link ConfigFile}.
     * Fields within the class intended for configuration should be annotated with {@link ConfigKey}.
     *
     * @param callingPluginContext The context of the plugin calling this method, used to access its data folder and resources.
     * @param configClass The class representing the configuration.
     * @return An instance of the configuration class populated with values from the file.
     * @param <T> The type of the configuration class.
     * @throws IllegalArgumentException if the configClass is not annotated with @ConfigFile.
     * @throws RuntimeException if loading or saving fails due to I/O or reflection errors.
     */
    @Override
    public <T> T loadConfig(PluginContext callingPluginContext, Class<T> configClass) {
        try {
            T configInstance = configClass.getDeclaredConstructor().newInstance();

            ConfigFile configFile = configClass.getAnnotation(ConfigFile.class);
            if (configFile == null) {
                throw new IllegalArgumentException("Configuration class " + configClass.getSimpleName() + " must be annotated with @ConfigFile");
            }

            File dataFolder = callingPluginContext.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs(); // Ensure the data folder exists
            }

            File file = new File(dataFolder, configFile.fileName());
            if (!file.exists()) {
                // Check if the resource exists in the JAR
                try (InputStream is = callingPluginContext.getResource(configFile.fileName())) {
                    if (is != null) {
                        // Resource exists in JAR, save it to data folder
                        callingPluginContext.saveResource(configFile.fileName(), false);
                    } else {
                        // Resource does NOT exist in JAR. Create an empty file.
                        // The defaults will be written by the loop below and then saved.
                        file.createNewFile();
                    }
                }
            }

            FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);

            for (Field field : configClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigKey.class)) {
                    field.setAccessible(true);
                    String path = field.getName(); // Simple path based on field name

                    if (fileConfig.contains(path)) {
                        // Load value from file
                        field.set(configInstance, fileConfig.get(path));
                    } else {
                        // If value not in file, set default from instance and save to file
                        fileConfig.set(path, field.get(configInstance));
                    }
                }
            }
            fileConfig.save(file);

            return configInstance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration for " + configClass.getSimpleName(), e);
        }
    }

    /**
     * Saves the current state of a configuration object back to its file.
     * The configuration object must be an instance of a class annotated with {@link ConfigFile}.
     *
     * @param callingPluginContext The context of the plugin calling this method, used to access its data folder.
     * @param configInstance The configuration object to save.
     * @throws IllegalArgumentException if the configInstance's class is not annotated with @ConfigFile.
     * @throws RuntimeException if saving fails due to I/O or reflection errors.
     */
    @Override
    public void saveConfig(PluginContext callingPluginContext, Object configInstance) {
        try {
            Class<?> configClass = configInstance.getClass();
            ConfigFile configFile = configClass.getAnnotation(ConfigFile.class);
            if (configFile == null) {
                throw new IllegalArgumentException("Configuration class " + configClass.getSimpleName() + " must be annotated with @ConfigFile");
            }

            File dataFolder = callingPluginContext.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs(); // Ensure the data folder exists
            }

            File file = new File(dataFolder, configFile.fileName());
            FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);

            for (Field field : configClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigKey.class)) {
                    field.setAccessible(true);
                    String path = field.getName();
                    fileConfig.set(path, field.get(configInstance));
                }
            }

            fileConfig.save(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save configuration for " + configInstance.getClass().getSimpleName(), e);
        }
    }
}
