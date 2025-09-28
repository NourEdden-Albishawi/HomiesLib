package lib.homies.framework.database.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a class as a database entity.
 * Classes annotated with {@code @DbEntity} can be persisted and retrieved
 * using the {@link lib.homies.framework.database.DatabaseService}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DbEntity {
    /**
     * The name of the table or collection where this entity's data will be stored.
     * This is used as a key prefix in key-value stores like Redis.
     * If not specified, the class name will be used.
     * @return The table or collection name.
     */
    String tableName() default "";
}
