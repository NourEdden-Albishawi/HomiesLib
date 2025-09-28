package lib.homies.framework.spigot.command;

import lib.homies.framework.command.annotations.SubCommand;
import lib.homies.framework.command.CommandManager;
import lib.homies.framework.command.annotations.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.logging.Level;

/**
 * Spigot-specific implementation of the {@link CommandManager} interface.
 * This class handles the registration of annotation-driven commands with the Bukkit/Spigot server.
 * It uses reflection to access the server's CommandMap and dynamically loads generated command dispatchers.
 */
public class SpigotCommandManager implements CommandManager {

    private final Plugin plugin;
    private final CommandMap commandMap;
    private final Constructor<PluginCommand> pluginCommandConstructor;

    /**
     * Constructs a new SpigotCommandManager.
     * Initializes access to the Bukkit CommandMap and the PluginCommand constructor via reflection.
     * @param plugin The {@link Plugin} instance of the framework.
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
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SpigotCommandManager: Could not access CommandMap.", e);
        }
    }

    /**
     * Registers an object as a command handler with the Spigot server.
     * The object's class must be annotated with {@link Command}.
     * This method dynamically loads the annotation processor-generated dispatcher class
     * and registers it as a {@link CommandExecutor} for the specified command.
     *
     * @param commandObject The instance of the command class to register.
     *                      This object should contain methods annotated with {@link Command} and {@link SubCommand}.
     */
    @Override
    public void registerCommand(Object commandObject) {
        Class<?> commandClass = commandObject.getClass();
        if (!commandClass.isAnnotationPresent(Command.class)) {
            plugin.getLogger().warning("Class " + commandClass.getSimpleName() + " is not annotated with @Command. Cannot register.");
            return;
        }

        // The annotation processor generates a dispatcher class with this naming convention
        String generatedClassName = commandClass.getName() + "_GeneratedDispatcher";
        try {
            // FIX: Use the classloader of the commandObject to load the generated class
            Class<?> generatedClass = commandClass.getClassLoader().loadClass(generatedClassName);
            // The generated dispatcher has a constructor that takes an instance of the command class
            Constructor<?> constructor = generatedClass.getConstructor(commandClass);
            CommandExecutor executor = (CommandExecutor) constructor.newInstance(commandObject);

            Command commandAnnotation = commandClass.getAnnotation(Command.class);

            // Create and configure a Bukkit PluginCommand instance
            PluginCommand command = pluginCommandConstructor.newInstance(commandAnnotation.name(), plugin);
            command.setExecutor(executor);
            command.setAliases(java.util.Arrays.asList(commandAnnotation.aliases()));
            command.setDescription(commandAnnotation.description());

            // Register the command with the server's CommandMap
            commandMap.register(plugin.getName(), command);

        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe("Could not find generated dispatcher for " + commandClass.getSimpleName() + ". Did the annotation processor run? " + e.getMessage());
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to register command " + commandClass.getSimpleName(), e);
        }
    }
}
