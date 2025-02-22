package dev.al3mid3x.lib.managers;

import dev.al3mid3x.lib.configurations.ConfigFile;
import dev.al3mid3x.lib.configurations.ConfigKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;

public class ConfigManager {
    private final JavaPlugin plugin;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public <T> T loadConfig(Class<T> configClass) {
        try {
            T configInstance = configClass.getDeclaredConstructor().newInstance();

            ConfigFile configFile = configClass.getAnnotation(ConfigFile.class);
            if (configFile == null) {
                throw new IllegalArgumentException("Configuration class must be annotated with @ConfigFile");
            }

            File file = new File(plugin.getDataFolder() + File.separator + configFile.path(), configFile.fileName());
            FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);

            for (Field field : configClass.getDeclaredFields()) {
                ConfigKey configKey = field.getAnnotation(ConfigKey.class);
                if (configKey != null) {
                    field.setAccessible(true);

                    String path = configKey.path().isEmpty() ? field.getName() : configKey.path();

                    if (fileConfig.contains(path)) {
                        field.set(configInstance, fileConfig.get(path));
                    } else {
                        fileConfig.set(path, field.get(configInstance));
                    }
                }
            }
            fileConfig.save(file);

            return configInstance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    public void saveConfig(Object configInstance) {
        try {
            Class<?> configClass = configInstance.getClass();
            ConfigFile configFile = configClass.getAnnotation(ConfigFile.class);
            if (configFile == null) {
                throw new IllegalArgumentException("Configuration class must be annotated with @ConfigFile");
            }

            File file = new File(plugin.getDataFolder() + File.separator + configFile.path(), configFile.fileName());
            FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);

            for (Field field : configClass.getDeclaredFields()) {
                ConfigKey configKey = field.getAnnotation(ConfigKey.class);
                if (configKey != null) {
                    field.setAccessible(true);

                    String path = configKey.path().isEmpty() ? field.getName() : configKey.path();

                    fileConfig.set(path, field.get(configInstance));
                }
            }

            fileConfig.save(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save configuration", e);
        }
    }
}

