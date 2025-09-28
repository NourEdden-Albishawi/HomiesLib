package lib.homies.framework.command;

import lib.homies.framework.command.annotations.Command;
import lib.homies.framework.command.annotations.SubCommand;

/**
 * A platform-agnostic interface for registering command classes.
 * Framework developers will use this service to register their command handlers.
 */
public interface CommandManager {

    /**
     * Registers an object as a command handler.
     * The object's class must be annotated with {@link Command} to be recognized.
     * The framework will automatically handle the registration of the command
     * and its subcommands with the underlying platform (e.g., Spigot).
     *
     * @param commandObject The instance of the command class to register.
     *                      This object should contain methods annotated with {@link Command} and {@link SubCommand}.
     */
    void registerCommand(Object commandObject);

}
