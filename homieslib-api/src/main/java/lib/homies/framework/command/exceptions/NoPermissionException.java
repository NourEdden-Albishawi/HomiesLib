package lib.homies.framework.command.exceptions;

/**
 * Exception thrown when a command sender does not have the required permission
 * to execute a command or subcommand.
 */
public class NoPermissionException extends CommandException {
    /**
     * Constructs a new NoPermissionException with the specified detail message.
     * @param message The detail message.
     */
    public NoPermissionException(String message) {
        super(message);
    }
}
