package io.github.jpamodel;


import java.util.List;
import java.util.Optional;

public class EntityControllerDecorator<T extends BaseEntity> implements EntityController<T>
{
    private final EntityController<T> controller;

    public EntityControllerDecorator(EntityController<T> controller)
    {
        this.controller = controller;
    }

    @Override
    public List<T> getAllEntities()
    {
        return controller.getAllEntities();
    }

    @Override
    public Optional<T> findEntity(Object id)
    {
        return controller.findEntity(id);
    }

    @Override
    public void insertEntity(T entity)
    {
        controller.insertEntity(entity);
    }

    @Override
    public void updateEntity(T entity)
    {
        controller.updateEntity(entity);
    }

    @Override
    public void removeEntity(T entity)
    {
        controller.removeEntity(entity);
    }

    @Override
    public void putEntity(T entity)
    {
        controller.putEntity(entity);
    }

    @Override
    public T getEntity(Object id)
    {
        return controller.getEntity(id);
    }

    @Override
    public <C extends EntityController<T>> C decorated(Class<C> decorator, Object... args)
    {
        return controller.decorated(decorator, args);
    }
}
