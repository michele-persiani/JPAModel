package io.github.jpamodel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JPAEntityController<T extends BaseEntity> implements EntityController<T>
{
    private final String persistenceUnit;
    private final Class<T> entityClass;
    private final EntityManagerFactory entitymanagerFactory;

    private final Logger logger = Logger.getLogger(getClass().getName());

    public JPAEntityController(Class<T> entityClass, String persistenceUnit)
    {
        this.entityClass = entityClass;
        this.persistenceUnit = persistenceUnit;
        this.entitymanagerFactory = Persistence.createEntityManagerFactory(this.persistenceUnit);
    }

    protected final EntityManager getEntityManager()
    {
        return entitymanagerFactory.createEntityManager();
    }


    protected String getSchemaName()
    {
        return entityClass.getSimpleName();
    }


    public void insertEntity(T entity)
    {
        logger.info(String.format("insert of %s", entityClass.getSimpleName()));
        if (!entity.isValid())
        {
            logger.log(Level.SEVERE, "Attempting to insert an invalid entity");
            throw new IllegalArgumentException("Entity is not valid");
        }
        if (!entity.isNew())
        {
            logger.log(Level.SEVERE, "Entity is not new");
            throw new IllegalArgumentException("Entity is not new");
        }
        EntityManager em = getEntityManager();

        try
        {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            logger.info("success");
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Exception: %s", e.getMessage());
            em.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally
        {
            em.close();
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
        EntityManager em = getEntityManager();
        T entity = null;
        try
        {
            entity = em.find(entityClass, id);
            logger.info("entity fetched");
        } catch (Exception e)
        {
            logger.log(Level.SEVERE, "exception %s", e.getMessage());
        } finally
        {
            em.close();
        }

        return Optional.ofNullable(entity);
    }


    public List<T> getAllEntities()
    {
        String tableName = getSchemaName();
        EntityManager em = getEntityManager();

        try
        {
            List<T> entities = new ArrayList<>(
                    em.createQuery("SELECT e FROM " + tableName + " e", entityClass).getResultList()
            );
            logger.info(String.format("num entities %s", entities.size()));
            return entities;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            em.close();
        }
    }


    @Override
    public void updateEntity(T entity)
    {
        if (entity.isNew())
        {
            logger.log(Level.SEVERE, "Attempting to update an entity but is new");
            throw new IllegalArgumentException("Entity is new");
        }
        if (!entity.isValid())
        {
            logger.log(Level.SEVERE, "Attempting to update an invalid entity");
            throw new IllegalArgumentException("Entity is not valid");
        }

        EntityManager em = getEntityManager();

        try
        {
            em.getTransaction().begin();

            em.merge(entity);

            em.getTransaction().commit();
        } catch (Exception e)
        {
            logger.log(Level.SEVERE, "exception while updating an entity: %s", e.getMessage());
            em.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally
        {
            em.close();

        }
    }


    public void removeEntity(T entity)
    {
        if (entity.isNew())
        {
            logger.log(Level.SEVERE, "Attempting to remove an invalid entity");
            throw new IllegalArgumentException("Entity is new");
        }
        EntityManager em = getEntityManager();
        try
        {
            em.getTransaction().begin();
            if (!em.contains(entity))
                entity = em.merge(entity); // For detached entities
            em.remove(entity);

            em.getTransaction().commit();

        } catch (Exception e)
        {
            em.getTransaction().rollback();
            logger.log(Level.SEVERE, "exception %s", e.getMessage());
            throw new RuntimeException(e);
        } finally
        {
            em.close();
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

        EntityManager em = getEntityManager();

        try
        {
            em.getTransaction().begin();
            S result = function.apply(em);
            em.getTransaction().commit();
            logger.info("transaction success");
            return result;
        } catch (Exception e)
        {
            logger.log(Level.SEVERE, "Exception: %s", e.getMessage());
            em.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally
        {
            em.close();
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
