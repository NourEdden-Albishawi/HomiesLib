package lib.homies.framework.database;

import lib.homies.framework.database.annotations.DbEntity;
import lib.homies.framework.database.annotations.DbField;

import java.util.Optional;

/**
 * A platform-agnostic service for managing database operations and repositories.
 * This service acts as a Unit of Work for database interactions, providing methods
 * to save and retrieve entities based on annotations.
 */
public interface DatabaseService {

    /**
     * Retrieves a registered repository by its class.
     * Repositories are typically registered during framework initialization.
     * @param repoClass The class of the repository to retrieve.
     * @return An {@link Optional} containing the repository if found, otherwise {@link Optional#empty()}.
     * @param <T> The type of the repository, which must extend {@link IRepository}.
     */
    <T extends IRepository<?>> Optional<T> getRepository(Class<T> repoClass);

    /**
     * Saves an entity to the database using reflection and annotations.
     * The entity's class must be annotated with {@link DbEntity} and have an ID field
     * annotated with {@link DbField}(id=true).
     *
     * @param entity The entity object to save.
     * @param <T> The entity type.
     * @throws IllegalArgumentException if the entity class is not annotated with @DbEntity.
     * @throws IllegalStateException if the entity does not have an ID field.
     * @throws RuntimeException if saving fails due to reflection errors or database issues.
     */
    <T> void saveEntity(T entity);

    /**
     * Finds and retrieves an entity from the database by its ID.
     * The entity's class must be annotated with {@link DbEntity}.
     *
     * @param entityType The class of the entity to find.
     * @param id         The ID of the entity.
     * @param <T>        The entity type.
     * @return An {@link Optional} containing the found entity, or {@link Optional#empty()} if not found.
     * @throws IllegalArgumentException if the entityType is not annotated with @DbEntity.
     * @throws RuntimeException if retrieval or mapping fails due to reflection errors or database issues.
     */
    <T> Optional<T> findById(Class<T> entityType, String id);
}
