import io.github.jpamodel.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class House extends BaseEntity
{
    @Column(name = "price")
    private double price;


    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }
}
