package io.github.jpamodel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JPAEntityController<T> implements EntityController<T>
{
    private final Class<T> entityClass;
    private final EntityManager entityManager;

    private final Logger logger = Logger.getLogger(getClass().getName());

    public JPAEntityController(Class<T> entityClass, EntityManager entityManager)
    {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
    }

    protected String getTableName()
    {
        if(entityClass.isAnnotationPresent(Table.class))
        {
            Table table = entityClass.getAnnotation(Table.class);
            if(!table.name().isEmpty())
                return table.name();
        }
        return entityClass.getSimpleName();
    }

    public void insertEntity(T entity)
    {
        logger.info(String.format("insert of %s", entityClass.getSimpleName()));

        try
        {
            entityManager.getTransaction().begin();
            entityManager.persist(entity);
            entityManager.getTransaction().commit();
            logger.info("success");
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Exception: %s", e.getMessage());
            entityManager.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }


    /**
     * Finds an entity with the given id
     *
     * @param id id of the entity
     * @return the found entity, or null
     */
    public Optional<T> findEntity(Object id)
    {
        logger.info(String.format("id %s", id));
        if (id == null)
            return Optional.empty();

        T entity = null;
        try
        {
            entity = entityManager.find(entityClass, id);
            logger.info("entity fetched");
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "exception %s", e.getMessage());
        }

        return Optional.ofNullable(entity);
    }


    public List<T> getAllEntities()
    {
        String tableName = getTableName();

        try
        {
            List<T> entities = new ArrayList<>(
                    entityManager.createQuery("SELECT e FROM " + tableName + " e", entityClass).getResultList()
            );
            logger.info(String.format("num entities %s", entities.size()));
            return entities;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void updateEntity(T entity)
    {
        try
        {
            entityManager.getTransaction().begin();
            entityManager.merge(entity);
            entityManager.getTransaction().commit();
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "exception while updating an entity: %s", e.getMessage());
            entityManager.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }


    public void removeEntity(T entity)
    {

        try
        {
            entityManager.getTransaction().begin();
            if (!entityManager.contains(entity))
                entity = entityManager.merge(entity); // For detached entities
            entityManager.remove(entity);

            entityManager.getTransaction().commit();
        }
        catch (Exception e)
        {
            entityManager.getTransaction().rollback();
            logger.log(Level.SEVERE, "exception %s", e.getMessage());
            throw new RuntimeException(e);
        }
    }



    /**
     * Use the entity manager to perform a transaction, specified by {@code function}.
     *
     * @param function
     * @param <S>
     * @return an optional with the transaction result, or empty if an exception occurred
     */
    private <S> S performTransaction(Function<EntityManager, S> function)
    {
        logger.info("Transaction begin");

        try
        {
            entityManager.getTransaction().begin();
            S result = function.apply(entityManager);
            entityManager.getTransaction().commit();
            logger.info("transaction success");
            return result;
        } catch (Exception e)
        {
            logger.log(Level.SEVERE, "Exception: %s", e.getMessage());
            entityManager.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }


    /**
     * Executes a select named query (need to be defined inside a @NamedQuery annotation of the entity) returning a result list.
     * @param namedQueryId
     * @param queryConfig consumer to configure the query. e.g. to invoke .setParameter() for all query parameters
     * @return the list of entities resulting from applying the query, or null if an error occurred
     */
    public final <S> List<S> executeResultListNamedQuery(String namedQueryId, Class<S> entityClass, Consumer<TypedQuery<S>> queryConfig)
    {
        logger.info("Executing named query: " + namedQueryId);

        return performTransaction(em -> {
            TypedQuery<S> query = em.createNamedQuery(namedQueryId, entityClass);
            queryConfig.accept(query);
            return query.getResultList();
        });
    }



    /**
     * Executes a select named query (need to be defined inside a @NamedQuery annotation of the entity) returning a result list.
     * @param namedQueryId
     * @param queryConfig consumer to configure the query. e.g. to invoke .setParameter() for all query parameters
     * @return the list of entities resulting from applying the query, or null if an error occurred
     */
    public final <S> List<S> executeResultListNamedQuery(String namedQueryId, Consumer<Query> queryConfig)
    {
        logger.info("Executing named query: " + namedQueryId);

        return performTransaction(em -> {
            Query query = em.createNamedQuery(namedQueryId);
            queryConfig.accept(query);
            return (List<S>) query.getResultList();
        });
    }


    /**
     * Executes a named query returning a single result (for example, SELECT count(x) from X). The result
     * is casted to the requested type
     * @param namedQueryId name of the named query
     * @param queryConfig consumer to configure the query. e.g. to invoke .setParameter() for all query parameters
     * @return the query's single result, casted to the requested type. Cast is unchecked
     */
    public final Object executeSingleResultNamedQuery(String namedQueryId, Consumer<Query> queryConfig)
    {
        logger.info("Executing named query: " + namedQueryId);

        return performTransaction(em -> {

            Query query = em.createNamedQuery(namedQueryId);
            queryConfig.accept(query);
            return query.getSingleResult();
        });
    }


    /**
     * Executes a named query returning a single result (for example, SELECT count(x) from X). The result
     * is casted to the requested type
     * @param namedQueryId name of the named query
     * @param resultType type of the result
     * @param queryConfig consumer to configure the query. e.g. to invoke .setParameter() for all query parameters
     * @return the query's single result, casted to the requested type. Cast is unchecked
     * @param <S>
     */
    public final <S> S executeSingleResultNamedQuery(String namedQueryId, Class<S> resultType, Consumer<Query> queryConfig)
    {
        logger.info("Executing named query: " + namedQueryId);

        return performTransaction(em -> {
            TypedQuery<S> query = em.createNamedQuery(namedQueryId, resultType);
            queryConfig.accept(query);
            return query.getSingleResult();
        });
    }
}
