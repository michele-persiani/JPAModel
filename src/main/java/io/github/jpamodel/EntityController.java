package io.github.jpamodel;


import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Controller to manage a collection of entities
 * @param <T> type of entities
 */
public interface EntityController<T extends BaseEntity>
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
     * Puts an entity, namely it inserts it if it is new, or updates it if it is not new.
     *
     * @param entity entity to put
     * @return whether the operation was successful
     */
    default void putEntity(T entity)
    {
        if (entity.isNew())
            insertEntity(entity);
        else
            updateEntity(entity);
    }

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

    
    /**
     * Decorates this controller with a decorator.
     *
     * @param decorator decorator class to decorate the current controller with. This class must have a constructor with
     *                  first argument being an it.unibo.msrehab.model.EntityController<T>. The current instance will be passed as the first argument.
     * @param args      other arguments for the constructor
     * @param <C>       class of the decorator
     * @return
     */
    default <C extends EntityController<T>> C decorated(Class<C> decorator, Object... args)
    {
        try
        {
            Class<?>[] argsClasses = Stream.concat(
                            Stream.of(EntityController.class),
                            Stream.of(args).map(Object::getClass)
                    )
                    .toArray(Class<?>[]::new);
            Constructor<C> constructor = decorator.getConstructor(argsClasses);

            Object[] argsWithController = Stream.concat(Stream.of(this), Stream.of(args)).toArray();
            return constructor.newInstance(argsWithController);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
