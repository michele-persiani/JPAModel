package io.github.jpamodel;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;


@MappedSuperclass
public class BaseEntity implements Serializable, Comparable<BaseEntity>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "created_at", nullable = false)
    long createdAtEpochMillis;

    @Column(name = "updated_at", nullable = false)
    long updatedAtEpochMillis;

    @PrePersist
    private void onCreate()
    {
        createdAtEpochMillis = System.currentTimeMillis();
        updatedAtEpochMillis = createdAtEpochMillis;
    }

    @PreUpdate
    private void onUpdate()
    {
        updatedAtEpochMillis = System.currentTimeMillis();
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public boolean isNew()
    {
        return this.id == null;
    }

    public boolean isValid()
    {
        return true;
    }

    @Override
    public String toString()
    {
        return String.format("Entity[class=%s, id=%s]", getClass().getSimpleName(), id);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!getClass().equals(obj.getClass()))
            return false;
        BaseEntity other = (BaseEntity) obj;
        if (id == null || other.id == null)
            return false;
        return Objects.equals(id, other.id);
    }


    public int hashCode()
    {
        return Objects.hash(getClass(), isNew() ? -1 : id);
    }


    private static final Comparator<BaseEntity> comparator = Comparator.nullsLast(Comparator.comparingLong(BaseEntity::getId));

    @Override
    public int compareTo(BaseEntity o)
    {
        return Objects.compare(this, o, comparator);
    }
}
