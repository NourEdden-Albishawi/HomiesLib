package lib.homies.framework.command.annotations;

import java.lang.annotation.*;

/**
 * Marks a method as a tab completer for a specific command or subcommand path.
 * The method should return a {@code java.util.List<String>} of suggestions.
 * <p>
 * The method signature should typically include parameters like:
 * <ul>
 *     <li>{@code lib.homies.framework.command.interfaces.CommandSender} or {@code org.bukkit.command.CommandSender}</li>
 *     <li>{@code String[]} args</li>
 * </ul>
 * and optionally {@code String} for the current partial argument.
 */
@Repeatable(TabCompletes.class)
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface TabComplete {

    /**
     * The command or subcommand path this completer applies to.
     * For example, "" for the main command, "subcommand" for a specific subcommand,
     * or {@code "subcommand <arg1>"} for completion after the first argument of a subcommand.
     * Placeholders like {@code <player>} or {@code <amount>} can be used to indicate argument positions.
     * @return The path this completer applies to.
     */
    String value() default "";

    /**
     * An array of aliases for the subcommand path this completer applies to.
     * These aliases will also trigger this tab completer.
     * @return An array of aliases.
     */
    String[] aliases() default {};
}
