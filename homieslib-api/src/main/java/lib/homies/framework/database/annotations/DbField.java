package lib.homies.framework.database.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a field within a {@link DbEntity} class to be persisted in the database.
 * This annotation is used by the {@link lib.homies.framework.database.DatabaseService}
 * to map object fields to database columns/fields.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DbField {
    /**
     * The name of the column or field in the database corresponding to this entity field.
     * If not specified, the Java field's name will be used.
     * @return The database column/field name.
     */
    String columnName() default "";

    /**
     * Indicates whether this field is the primary identifier (ID) for the entity.
     * An entity must have exactly one field marked as ID.
     * @return {@code true} if this field is the entity's ID, {@code false} otherwise.
     */
    boolean id() default false;
}
