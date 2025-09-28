package lib.homies.framework.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a class as a configuration file.
 * This class will be used by the {@link lib.homies.framework.config.ConfigManager}
 * to load and save configuration data to a YAML file.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigFile {
    /**
     * The name of the configuration file (e.g., "config.yml", "messages.yml").
     * @return The file name.
     */
    String fileName() default "config.yml";

    /**
     * The path within the plugin's data folder where the file should be located.
     * An empty string means the root of the data folder.
     * @return The file path.
     */
    String path() default "";
}
