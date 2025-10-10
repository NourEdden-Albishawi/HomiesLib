package lib.homies.framework.spigot.config;

import lib.homies.framework.HomiesLib;
import lib.homies.framework.PluginContext;
import lib.homies.framework.config.ConfigManager;
import lib.homies.framework.config.annotations.ConfigFile;
import lib.homies.framework.config.annotations.ConfigKey;
import lib.homies.framework.events.MenuReloadEvent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Spigot-specific implementation of the {@link ConfigManager} interface.
 * This class handles loading and saving configuration files using Bukkit's {@link YamlConfiguration}.
 * It supports annotation-driven configuration classes with {@link ConfigFile} and {@link ConfigKey}.
 */
public class SpigotConfigManager implements ConfigManager {

    public SpigotConfigManager() {
    }

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
                dataFolder.mkdirs();
            }

            File file = new File(dataFolder, configFile.fileName());
            if (!file.exists()) {
                try (InputStream is = callingPluginContext.getResource(configFile.fileName())) {
                    if (is != null) {
                        callingPluginContext.saveResource(configFile.fileName(), false);
                    } else {
                        file.createNewFile();
                    }
                }
            }

            FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);

            for (Field field : configClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigKey.class)) {
                    field.setAccessible(true);
                    ConfigKey configKey = field.getAnnotation(ConfigKey.class);
                    String path = configKey.path();
                    if (path.isEmpty()) {
                        path = field.getName();
                    }

                    if (fileConfig.contains(path)) {
                        Object value = fileConfig.get(path);
                        Class<?> fieldType = field.getType();

                        // Handle specific type conversions
                        if (fieldType.isArray() && value instanceof List) {
                            if (fieldType.getComponentType() == int.class) {
                                List<?> list = (List<?>) value;
                                int[] intArray = new int[list.size()];
                                for (int i = 0; i < list.size(); i++) {
                                    if (list.get(i) instanceof Number) {
                                        intArray[i] = ((Number) list.get(i)).intValue();
                                    }
                                }
                                value = intArray;
                            }
                            // Add other array type conversions here if needed (e.g., String[], double[])
                        } else if (fieldType == String.class && value instanceof String) {
                            value = ChatColor.translateAlternateColorCodes('&', (String) value);
                        }

                        try {
                            field.set(configInstance, value);
                        } catch (IllegalArgumentException e) {
                            // Provide a more detailed error message for type mismatches
                            throw new IllegalArgumentException("Type mismatch for config key '" + path + "'. Expected " + fieldType.getName() + " but got " + value.getClass().getName() + ".", e);
                        }
                    } else {
                        fileConfig.set(path, field.get(configInstance));
                    }
                }
            }
            fileConfig.save(file);

            // Fire the MenuReloadEvent after config is loaded/reloaded
            HomiesLib.getEventBus().call(new MenuReloadEvent());

            return configInstance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration for " + configClass.getSimpleName(), e);
        }
    }

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
                dataFolder.mkdirs();
            }

            File file = new File(dataFolder, configFile.fileName());
            FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);

            for (Field field : configClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigKey.class)) {
                    field.setAccessible(true);
                    ConfigKey configKey = field.getAnnotation(ConfigKey.class);
                    String path = configKey.path();
                    if (path.isEmpty()) {
                        path = field.getName();
                    }
                    fileConfig.set(path, field.get(configInstance));
                }
            }

            fileConfig.save(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save configuration for " + configInstance.getClass().getSimpleName(), e);
        }
    }
}
