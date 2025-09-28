package lib.homies.framework.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify a permission required to execute a command or subcommand.
 * Can be applied to methods annotated with {@link Command} or {@link SubCommand}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Permission {
    /**
     * The permission string required (e.g., "myplugin.command.use").
     * @return The permission node.
     */
    String value();

    /**
     * The message sent to the player if they do not have the required permission.
     * @return The permission denied message.
     */
    String message() default "&cYou do not have permission to use this command.";
}
