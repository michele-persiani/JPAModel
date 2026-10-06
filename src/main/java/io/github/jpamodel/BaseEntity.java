package io.github.jpamodel;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
public class BaseEntity implements Serializable, Comparable<BaseEntity>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "created_on", updatable = false)
    private String createdOn;

    @Column(name = "updated_on")
    private String lastUpdatedOn;


    @PrePersist
    public void prePersist()
    {
        createdOn = LocalDateTime.now().toString();
        lastUpdatedOn = createdOn;
    }

    @PreUpdate
    public void preUpdate()
    {
        lastUpdatedOn = LocalDateTime.now().toString();
    }


    public Long getId()
    {
        return id;
    }


    public void setId(Long id)
    {
        this.id = id;
    }


    public LocalDateTime getLastUpdatedOn()
    {
        if (Objects.isNull(lastUpdatedOn))
            return null;
        return LocalDateTime.parse(lastUpdatedOn);
    }


    public LocalDateTime getCreatedOn()
    {
        if (Objects.isNull(createdOn))
            return null;
        return LocalDateTime.parse(createdOn);
    }


    public boolean isNew()
    {
        return this.id == null || this.id < 0;
    }


    public boolean isValid()
    {
        return true;
    }


    @Override
    public boolean equals(Object obj)
    {
        if(!getClass().equals(obj.getClass()))
            return false;
        BaseEntity other = (BaseEntity) obj;
        if(id == null || other.id == null)
            return false;
        return Objects.equals(id, other.id);
    }


    public int hashCode()
    {
        return Objects.hash(getClass(), isNew()? -1 : id);
    }


    @Override
    public int compareTo(BaseEntity o)
    {
        if (createdOn == null && o.createdOn == null)
            return 0;
        else if (createdOn == null)
            return -1;
        else if (o.createdOn == null)
            return 1;
        else
            return getCreatedOn().compareTo(o.getCreatedOn());
    }
}
