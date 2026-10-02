Set of classes to access JPA.

```java
@Entity
public class User extends BaseEntity
{
    @Column(name="name")
    private int name;
    @Column(name="surname")
    private int surname;
    @Column(name="age")
    private int age;
    ...
}


User youngest = JPAModel.getInstance()
        .getEntityController(User.class)
        .decorated(UserEntityController.class)
        .getYoungest();
```
