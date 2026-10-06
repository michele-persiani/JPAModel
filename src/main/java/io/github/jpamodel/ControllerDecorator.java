package io.github.jpamodel;


import java.util.List;
import java.util.Optional;

public class ControllerDecorator<T extends BaseEntity> implements EntityController<T>
{
    private final EntityController<T> controller;

    public ControllerDecorator(EntityController<T> controller)
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
    public T getEntity(Object id)
    {
        return controller.getEntity(id);
    }
}
