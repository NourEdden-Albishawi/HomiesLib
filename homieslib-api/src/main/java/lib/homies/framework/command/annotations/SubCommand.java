package lib.homies.framework.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a method as a subcommand handler.
 * Methods annotated with this must reside within a class annotated with {@link Command}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubCommand {
    /**
     * The name or pattern of the subcommand (e.g., "help", "set <value>").
     * @return The subcommand name or pattern.
     */
    String value();

    /**
     * A brief description of the subcommand.
     * @return The subcommand description.
     */
    String description() default "";

    /**
     * The usage message for the subcommand.
     *
     * @return The usage message.
     */
    String usage() default "";

    /**
     * Whether this subcommand can only be executed by a player.
     *
     * @return true if player-only, false otherwise.
     */
    boolean playerOnly() default false;

    /**
     * The message to send if a non-player tries to execute this subcommand when playerOnly is true.
     *
     * @return The message to send.
     */
    String playerOnlyMessage() default "This command can only be run by a player.";
}
