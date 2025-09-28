package lib.homies.framework.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a field within a configuration class as a configurable key.
 * This field's value will be loaded from and saved to the configuration file.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigKey {
    /**
     * The path to the configuration key within the YAML file.
     * If empty, the field's name will be used as the key.
     * @return The configuration path.
     */
    String path() default "";

    /**
     * A comment to be added above the configuration key in the generated file.
     * @return The comment for the key.
     */
    String comment() default "";
}
