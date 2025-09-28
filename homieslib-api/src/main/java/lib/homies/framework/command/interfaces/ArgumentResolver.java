package lib.homies.framework.command.interfaces;


import lib.homies.framework.command.CommandContext;
import lib.homies.framework.command.exceptions.ArgumentParseException;

/**
 * A functional interface for resolving command arguments into specific types.
 * Framework developers can implement this to provide custom argument parsing logic.
 * @param <T> The type of object this resolver can parse arguments into.
 */
@FunctionalInterface
public interface ArgumentResolver<T> {
    /**
     * Resolves a raw string argument into the target type T.
     * @param context The command context, providing access to sender, args, etc.
     * @param argument The raw string argument to resolve.
     * @return The parsed object of type T.
     * @throws ArgumentParseException if the argument cannot be parsed into the target type.
     */
    T resolve(CommandContext context, String argument) throws ArgumentParseException;
}