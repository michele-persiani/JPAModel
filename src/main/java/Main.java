import io.github.jpamodel.Model;

import java.util.List;


public class Main
{
    public static void main(String[] args)
    {
        House house = new House();
        house.setPrice(1000000);

        Model.getInstance().getController(House.class).insertEntity(house);

        List<House> houses = Model.getInstance().getController(House.class).getAllEntities();

    }
}