import io.github.jpamodel.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;



@Entity
public class User extends BaseEntity
{
    @Column(name="name")
    private int name;
    @Column(name="surname")
    private int surname;
    @Column(name="age")
    private int age;

    public int getName()
    {
        return name;
    }

    public void setName(int name)
    {
        this.name = name;
    }

    public int getSurname()
    {
        return surname;
    }

    public void setSurname(int surname)
    {
        this.surname = surname;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }
}
