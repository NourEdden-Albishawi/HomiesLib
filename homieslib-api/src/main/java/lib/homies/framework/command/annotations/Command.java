package lib.homies.framework.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a class or method as a main command handler.
 * When applied to a class, it signifies that the class contains command logic.
 * When applied to a method, it signifies the default execution for the main command.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Command {
    /**
     * The name of the command (e.g., "mycommand").
     * This is required when annotating a class, but ignored on a method.
     * @return The command name.
     */
    String name() default "";

    /**
     * A brief description of the command.
     * @return The command description.
     */
    String description() default "A HomiesLib command.";

    /**
     * An array of aliases for the command.
     * @return An array of command aliases.
     */
    String[] aliases() default {};

    /**
     * The usage message for the command.
     *
     * @return The usage message.
     */
    String usage() default "";

    /**
     * Whether this command can only be executed by a player.
     *
     * @return true if player-only, false otherwise.
     */
    boolean playerOnly() default false;

    /**
     * The message to send if a non-player tries to execute this command when playerOnly is true.
     *
     * @return The message to send.
     */
    String playerOnlyMessage() default "This command can only be run by a player.";
}
