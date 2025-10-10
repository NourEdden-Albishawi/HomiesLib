package lib.homies.framework.database;

import lib.homies.framework.database.annotations.DbEntity;
import lib.homies.framework.database.annotations.DbField;

import java.util.List;
import java.util.Optional;

/**
 * A platform-agnostic service for managing database operations and repositories.
 * This service acts as a Unit of Work for database interactions, providing methods
 * to save and retrieve entities based on annotations.
 */
public interface DatabaseService {

    <T extends IRepository<?>> Optional<T> getRepository(Class<T> repoClass);

    <T> void saveEntity(T entity);

    <T> Optional<T> findById(Class<T> entityType, String id);

    <T> List<T> findAll(Class<T> entityType);

    /**
     * Finds and retrieves all entities that match a specific field-value pair.
     *
     * @param entityType The class of the entities to find.
     * @param fieldName  The name of the database column to query.
     * @param value      The value to match against.
     * @param <T>        The entity type.
     * @return A {@link List} of all found entities, or an empty list if none are found.
     */
    <T> List<T> findByField(Class<T> entityType, String fieldName, Object value);

    /**
     * Deletes an entity from the database.
     * The entity's class must be annotated with {@link DbEntity} and have an ID field
     * annotated with {@link DbField}(id=true).
     *
     * @param entity The entity object to delete.
     * @param <T> The entity type.
     * @throws IllegalArgumentException if the entity class is not annotated with @DbEntity.
     * @throws IllegalStateException if the entity does not have an ID field.
     * @throws RuntimeException if deletion fails due to reflection errors or database issues.
     */
    <T> void deleteEntity(T entity);

    /**
     * Deletes entities from the database that match a specific field-value pair.
     *
     * @param entityType The class of the entities to delete.
     * @param fieldName  The name of the database column to match.
     * @param value      The value to match against.
     * @param <T>        The entity type.
     */
    <T> void deleteByField(Class<T> entityType, String fieldName, Object value);
}
