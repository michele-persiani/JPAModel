import io.github.jpamodel.JPAModel;


public class Main
{
    public static void main(String[] args)
    {
        User youngest = JPAModel.getInstance()
                .getEntityController(User.class)
                .decorated(UserEntityController.class)
                .getYoungest();
    }
}