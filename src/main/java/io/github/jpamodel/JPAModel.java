package io.github.jpamodel;


import java.util.HashMap;
import java.util.Map;


/**
 * Singleton class to manage all entities
 */
public class JPAModel implements Model
{
    public static String persistenceUnit = "DEFAULT_PU";

    private static JPAModel instance;

    private final Map<Class<?>, EntityController<?>> controllers = new HashMap<>();


    private JPAModel() {}

    /**
     * Gets the singleton instance of the JPAController
     * @return singleton instance of the JPAController
     */
    public static JPAModel getInstance()
    {
        if(instance == null)
            instance = new JPAModel();
        return instance;
    }

    public void setPersistenceUnit(String persistenceUnit)
    {
        JPAModel.persistenceUnit = persistenceUnit;
    }

    /**
     * Gets a IEntityController for the given entity class.
     * @param entityClass class of entities for which get the controller
     * @return the entity controller
     * @param <T>
     */
    public <T extends BaseEntity> EntityController<T> getEntityController(Class<T> entityClass)
    {
        if(!controllers.containsKey(entityClass))
            controllers.put(entityClass, createController(entityClass));
        return (EntityController<T>)controllers.get(entityClass);
    }


    private <T extends BaseEntity> EntityController<T> createController(Class<T> entityClass)
    {
        EntityController<T> controller = new JPAEntityController<>(entityClass, persistenceUnit);
        return controller;
    }

}
