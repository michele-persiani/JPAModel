package io.github.jpamodel;

/**
 * Master interface to access the model layer managing all entities.
 */
public interface Model
{
    /**
     * Retrieves the entity controller responsible for managing the specified entity type.
     *
     * @param entityClass the class of the entity type for which the controller is requested
     * @return the entity controller for the specified entity type
     */
    <T extends BaseEntity> EntityController<T> getEntityController(Class<T> entityClass);
}
