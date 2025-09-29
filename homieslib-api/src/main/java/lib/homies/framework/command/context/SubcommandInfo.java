package lib.homies.framework.command.context;

import java.util.List;

/**
 * Represents metadata about a subcommand, used for help messages and command introspection.
 */
public class SubcommandInfo {
    private final String name;
    private final String description;
    private final String usage;
    private final int minArgs;
    private final List<String> argTypes;
    private final String permission;

    /**
     * Constructs a new SubcommandInfo instance.
     * @param name The name or pattern of the subcommand.
     * @param description A brief description of the subcommand.
     * @param usage The usage message for the subcommand.
     * @param minArgs The minimum number of arguments required for the subcommand.
     * @param argTypes A list of string representations of the argument types.
     * @param permission The permission node required for this subcommand, or an empty string if none.
     */
    public SubcommandInfo(String name, String description, String usage, int minArgs, List<String> argTypes, String permission) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.minArgs = minArgs;
        this.argTypes = argTypes;
        this.permission = permission;
    }

    /**
     * Gets the name or pattern of the subcommand.
     * @return The subcommand name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the subcommand.
     * @return The subcommand description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the usage message for the subcommand.
     *
     * @return The usage message.
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Gets the minimum number of arguments required for the subcommand.
     * @return The minimum argument count.
     */
    public int getMinArgs() {
        return minArgs;
    }

    /**
     * Gets a list of string representations of the argument types for the subcommand.
     * @return A list of argument type strings.
     */
    public List<String> getArgTypes() {
        return argTypes;
    }

    /**
     * Gets the permission node required for this subcommand.
     * @return The permission node, or an empty string.
     */
    public String getPermission() {
        return permission;
    }
}
