package lib.homies.framework.command;

import lib.homies.framework.command.interfaces.CommandSender;

import java.util.List;

/**
 * Represents the context of a command execution.
 * This object provides access to the sender, arguments, and label of the command.
 */
public class CommandContext {

    private final CommandSender sender;
    private final List<String> args;
    private final String label;

    /**
     * Constructs a new CommandContext.
     * @param sender The sender of the command.
     * @param args The arguments provided with the command.
     * @param label The label (alias) used to execute the command.
     */
    public CommandContext(CommandSender sender, List<String> args, String label) {
        this.sender = sender;
        this.args = args;
        this.label = label;
    }

    /**
     * Gets the sender of the command.
     * @return The CommandSender instance.
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * Gets the arguments provided with the command.
     * @return A list of command arguments.
     */
    public List<String> getArgs() {
        return args;
    }

    /**
     * Gets the label (alias) used to execute the command.
     * @return The command label.
     */
    public String getLabel() {
        return label;
    }
}
