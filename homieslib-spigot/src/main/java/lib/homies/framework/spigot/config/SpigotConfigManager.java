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
import java.util.concurrent.CompletableFuture;

public class SpigotConfigManager implements ConfigManager {

    @Override
    public <T> CompletableFuture<T> loadConfig(PluginContext callingPluginContext, Class<T> configClass) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ConfigFile configFile = configClass.getAnnotation(ConfigFile.class);
                if (configFile == null) {
                    throw new IllegalArgumentException("Class " + configClass.getName() + " is not annotated with @ConfigFile.");
                }

                T configInstance = configClass.getDeclaredConstructor().newInstance();

                File dataFolder = callingPluginContext.getDataFolder();
                if (!dataFolder.exists()) {
                    dataFolder.mkdirs();
                }

                File file = new File(dataFolder, configFile.fileName());
                boolean isNewFile = !file.exists();

                if (isNewFile) {
                    try (InputStream is = callingPluginContext.getResource(configFile.fileName())) {
                        if (is != null) {
                            callingPluginContext.saveResource(configFile.fileName(), false);
                        } else {
                            file.createNewFile();
                        }
                    }
                }

                FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);

                if (isNewFile) {
                    // Build a header with all comments for the new file
                    StringBuilder header = new StringBuilder("Default configuration for " + configClass.getSimpleName() + "\n\n");
                    for (Field field : configClass.getDeclaredFields()) {
                        if (field.isAnnotationPresent(ConfigKey.class)) {
                            ConfigKey configKey = field.getAnnotation(ConfigKey.class);
                            if (!configKey.comment().isEmpty()) {
                                header.append(configKey.path()).append(":\n");
                                for (String line : configKey.comment().split("\\n")) {
                                    header.append("  # ").append(line).append("\n");
                                }
                                header.append("\n");
                            }
                        }
                    }
                    fileConfig.options().header(header.toString());
                    fileConfig.options().copyHeader(true);
                }

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
                            } else if (fieldType == String.class && value instanceof String) {
                                value = ChatColor.translateAlternateColorCodes('&', (String) value);
                            }

                            try {
                                field.set(configInstance, value);
                            } catch (IllegalArgumentException e) {
                                throw new IllegalArgumentException("Type mismatch for config key '" + path + "'. Expected '" + fieldType.getName() + "' but got '" + value.getClass().getName() + "'.", e);
                            }
                        } else {
                            fileConfig.set(path, field.get(configInstance));
                        }
                    }
                }

                fileConfig.save(file);

                // Dispatch the event back to the main thread
                HomiesLib.getSchedulerService().run(() -> HomiesLib.getEventBus().call(new MenuReloadEvent()));

                return configInstance;
            } catch (Exception e) {
                throw new RuntimeException("Failed to load configuration for " + configClass.getSimpleName(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> saveConfig(PluginContext callingPluginContext, Object configInstance) {
        return CompletableFuture.runAsync(() -> {
            try {
                Class<?> configClass = configInstance.getClass();
                ConfigFile configFile = configClass.getAnnotation(ConfigFile.class);
                if (configFile == null) {
                    throw new IllegalArgumentException("Class " + configClass.getName() + " is not annotated with @ConfigFile.");
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
        });
    }
}
