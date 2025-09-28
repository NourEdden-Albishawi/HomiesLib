package lib.homies.framework.database;

import java.util.Optional;

/**
 * A platform-agnostic interface for a generic data repository.
 * This interface defines basic CRUD (Create, Read, Update, Delete) operations
 * for entities managed by the {@link DatabaseService}.
 * @param <T> The type of the entity managed by this repository.
 */
public interface IRepository<T> {
    /**
     * Saves an entity to the database.
     * If the entity already exists (based on its ID), it should be updated; otherwise, it should be created.
     * @param entity The entity object to save.
     */
    void save(T entity);

    /**
     * Deletes an entity from the database.
     * @param entity The entity object to delete.
     */
    void delete(T entity);

    /**
     * Finds and retrieves an entity from the database by its unique identifier.
     * @param id The unique ID of the entity.
     * @return An {@link Optional} containing the found entity, or {@link Optional#empty()} if not found.
     */
    Optional<T> findById(String id);
}
