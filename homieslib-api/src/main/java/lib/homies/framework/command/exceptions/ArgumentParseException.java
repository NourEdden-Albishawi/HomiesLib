package lib.homies.framework.command.exceptions;

/**
 * Exception thrown when a command argument cannot be parsed into the expected type.
 */
public class ArgumentParseException extends CommandException {
    /**
     * Constructs a new ArgumentParseException with the specified detail message.
     * @param message The detail message indicating why the argument could not be parsed.
     */
    public ArgumentParseException(String message) {
        super(message);
    }

    /**
     * Constructs a new ArgumentParseException with the specified detail message and cause.
     * @param message The detail message.
     * @param cause The cause of the exception.
     */
    public ArgumentParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
