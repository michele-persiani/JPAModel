import io.github.jpamodel.EntityController;
import io.github.jpamodel.EntityControllerDecorator;

import java.util.Comparator;

public class UserEntityController extends EntityControllerDecorator<User>
{
    public UserEntityController(EntityController<User> controller)
    {
        super(controller);
    }

    public User getOldest()
    {
        return getAllEntities()
                .stream()
                .max(Comparator.comparing(User::getAge))
                .orElse(null);
    }

    public User getYoungest()
    {
        return getAllEntities()
                .stream()
                .min(Comparator.comparing(User::getAge))
                .orElse(null);
    }
}
