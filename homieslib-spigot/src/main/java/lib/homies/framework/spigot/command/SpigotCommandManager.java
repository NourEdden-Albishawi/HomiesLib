package lib.homies.framework.spigot.command;

import lib.homies.framework.command.annotations.Command;
import lib.homies.framework.command.annotations.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;

/**
 * Handles the automatic registration of all annotation-driven commands at runtime.
 * This manager discovers commands via the annotation processor-generated registry,
 * removing the need for manual registration in plugin.yml.
 */
public class SpigotCommandManager {

    private final Plugin plugin;
    private final CommandMap commandMap;
    private final Constructor<PluginCommand> pluginCommandConstructor;

    /**
     * Constructs a new SpigotCommandManager.
     * Initializes access to the Bukkit CommandMap and the PluginCommand constructor via reflection.
     * @param plugin The {@link Plugin} instance that owns these commands.
     * @throws RuntimeException if access to Bukkit's internal command system fails.
     */
    public SpigotCommandManager(Plugin plugin) {
        this.plugin = plugin;
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            this.commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            this.pluginCommandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            this.pluginCommandConstructor.setAccessible(true);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to initialize SpigotCommandManager: Could not access Bukkit internals.", e);
        }
    }

    /**
     * Discovers and registers all commands annotated with @Command.
     * This method should be called once during the plugin's onEnable phase.
     */
    public void registerAllCommands() {
        plugin.getLogger().info("Starting automatic command registration...");
        try {
            // Use the plugin's classloader to find the generated registry
            Class<?> registryClass = Class.forName("lib.homies.framework.spigot.command.HomiesCommandRegistry", true, plugin.getClass().getClassLoader());
            java.net.URL location = registryClass.getProtectionDomain().getCodeSource().getLocation();
            plugin.getLogger().info("Loaded HomiesCommandRegistry from: " + location.getFile());
            Field commandClassesField = registryClass.getField("COMMAND_CLASS_NAMES");
            @SuppressWarnings("unchecked")
            List<String> commandClassNames = (List<String>) commandClassesField.get(null);

            plugin.getLogger().info("Discovered command classes: " + commandClassNames);

            if (commandClassNames.isEmpty()) {
                plugin.getLogger().info("No commands found to register.");
                return;
            }

            for (String className : commandClassNames) {
                registerCommand(className);
            }
            plugin.getLogger().info("Successfully registered " + commandClassNames.size() + " command(s).");

        } catch (ClassNotFoundException e) {
            plugin.getLogger().info("No HomiesCommandRegistry found. Skipping automatic command registration. This is normal if no commands are defined.");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred during automatic command registration.", e);
        }
    }

    private void registerCommand(String commandClassName) {
        try {
            Class<?> commandClass = Class.forName(commandClassName, true, plugin.getClass().getClassLoader());
            Command commandAnnotation = commandClass.getAnnotation(Command.class);

            if (commandAnnotation == null) {
                plugin.getLogger().warning("Class " + commandClassName + " is listed in the registry but is not annotated with @Command. Skipping.");
                return;
            }

            String commandName = commandAnnotation.name();
            if (commandName == null || commandName.isEmpty()) {
                plugin.getLogger().severe("Failed to register command from class " + commandClassName + ": @Command annotation on the class is missing a 'name'.");
                return;
            }

            // Instantiate the command class
            Object commandInstance;
            try {
                commandInstance = commandClass.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                plugin.getLogger().severe("Failed to instantiate command class: " + commandClassName + ". Does it have a public no-arg constructor?");
                return;
            }

            // Load the generated dispatcher
            String dispatcherClassName = commandClass.getName() + "_GeneratedDispatcher";
            Class<?> dispatcherClass = Class.forName(dispatcherClassName, true, plugin.getClass().getClassLoader());
            Constructor<?> dispatcherConstructor = dispatcherClass.getConstructor(commandClass);
            CommandExecutor executor = (CommandExecutor) dispatcherConstructor.newInstance(commandInstance);

            // Create and register the Bukkit command
            PluginCommand command = pluginCommandConstructor.newInstance(commandName, plugin);
            command.setExecutor(executor);
            command.setAliases(java.util.Arrays.asList(commandAnnotation.aliases()));
            command.setDescription(commandAnnotation.description());
            command.setUsage(ChatColor.translateAlternateColorCodes('&', commandAnnotation.usage().replace("<command>", commandName)));

            // Set permission and permission message if present on the class
            Permission permission = commandClass.getAnnotation(Permission.class);
            if (permission != null) {
                command.setPermission(permission.value());
                command.setPermissionMessage(ChatColor.translateAlternateColorCodes('&', permission.message()));
            }

            // Load and set the generated tab completer, if it exists
            String tabCompleterClassName = commandClass.getName() + "_GeneratedTabCompleter";
            try {
                Class<?> tabCompleterClass = Class.forName(tabCompleterClassName, true, plugin.getClass().getClassLoader());
                Constructor<?> tabCompleterConstructor = tabCompleterClass.getConstructor(commandClass);
                TabCompleter tabCompleter = (TabCompleter) tabCompleterConstructor.newInstance(commandInstance);
                command.setTabCompleter(tabCompleter);
            } catch (ClassNotFoundException e) {
                // No tab completer generated for this command, which is fine.
                plugin.getLogger().fine("No tab completer found for " + commandClassName + ".");
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to load or set tab completer for " + commandClassName, e);
            }

            commandMap.register(plugin.getName().toLowerCase(), command);

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to register command from class: " + commandClassName, e);
        }
    }
}
