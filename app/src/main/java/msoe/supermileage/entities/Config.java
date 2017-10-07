package msoe.supermileage.entities;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * The @Entity annotation identifies Server as a persistable entity.
 * This will trigger ObjectBox to generate persistence code tailored for this class.
 *
 * @link http://objectbox.io/documentation/entity-annotations/
 * @link http://objectbox.io/documentation/relations/
 *
 */
@Entity
public class Config {

    /**
     *  every object has an ID of type long to efficiently get or reference objects
     */
    @Id
    private long id;

    private String name;

    private String json;

    ToOne<Car> car;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public ToOne<Car> getCar() {
        return car;
    }

    public void setCar(ToOne<Car> car) {
        this.car = car;
    }
}
