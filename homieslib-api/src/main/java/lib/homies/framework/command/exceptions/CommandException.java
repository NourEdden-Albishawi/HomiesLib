package lib.homies.framework.command.exceptions;

/**
 * Base exception for all command-related errors in the framework.
 */
public class CommandException extends RuntimeException {
    /**
     * Constructs a new CommandException with the specified detail message.
     * @param message The detail message.
     */
    public CommandException(String message) {
        super(message);
    }

    /**
     * Constructs a new CommandException with the specified detail message and cause.
     * @param message The detail message.
     * @param cause The cause of the exception.
     */
    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
