package io.github.jpamodel;


import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Controller to manage a collection of entities
 * @param <T> type of entities
 */
public interface EntityController<T>
{
    /**
     * Gets all entities
     *
     * @return all entities
     */
    List<T> getAllEntities();

    /**
     * Finds an entity with the given id
     * @param id entity id
     * @return an optional with the found entity
     */
    Optional<T> findEntity(Object id);

    /**
     * Inserts an entity
     *
     * @param entity entity to insert
     */
    void insertEntity(T entity);

    /**
     * Updates an entity
     *
     * @param entity entity to update
     */
    void updateEntity(T entity);

    /**
     * Removes an entity
     *
     * @param entity entity to remove
     * @return true if the entity was removed, false if the entity was not found or an error occurred
     */
    void removeEntity(T entity);

    /**
     * Gets the entity with the given id or throws an exception if not found.
     *
     * @param id entity id
     * @return the entity with id {@code id}
     * @throws IllegalArgumentException if the entity with id {@code id} is not found
     */
    default T getEntity(Object id)
    {
        return findEntity(id).orElseThrow(() -> new IllegalArgumentException(String.format("Entity with id %s not found", id.toString())));
    }
}
