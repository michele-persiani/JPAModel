package io.github.jpamodel;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;


/**
 * Singleton class to manage all entities
 */
public class Model
{
    public static String persistenceUnit = "defaultPersistenceUnit";

    private static Model instance;

    private final Map<Class<?>, EntityController<?>> controllers = new HashMap<>();

    private EntityManager entityManager;

    private Model()
    {
    }

    private EntityManager getEntityManager()
    {
        if(entityManager == null)
        {
            EntityManagerFactory entitymanagerFactory = Persistence.createEntityManagerFactory(persistenceUnit);
            entityManager = entitymanagerFactory.createEntityManager();
        }
        return entityManager;
    }

    /**
     * Gets the singleton instance of the Model
     * @return singleton instance of the Model
     */
    public static Model getInstance()
    {
        if(instance == null)
            instance = new Model();
        return instance;
    }

    /**
     * Sets the JPA persistence unit
     *
     * @param persistenceUnit name of the persistence unit
     * @return singleton instance of the Model
     */
    public Model setPersistenceUnit(String persistenceUnit)
    {
        Model.persistenceUnit = persistenceUnit;
        entityManager = null;
        controllers.clear();
        return this;
    }

    /**
     * Gets a EntityController for the given entity class.
     * @param entityClass class of entities for which get the controller
     * @return the entity controller
     * @param <T>
     */
    public <T> EntityController<T> getController(Class<T> entityClass)
    {
        if(!controllers.containsKey(entityClass))
            controllers.put(entityClass, createController(entityClass));
        return (EntityController<T>)controllers.get(entityClass);
    }


    private <T> EntityController<T> createController(Class<T> entityClass)
    {
        return new JPAEntityController<>(entityClass, getEntityManager());
    }

}
