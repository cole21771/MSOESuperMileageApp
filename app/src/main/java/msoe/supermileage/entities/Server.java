package msoe.supermileage.entities;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

/**
 * The @Entity annotation identifies Server as a persistable entity.
 * This will trigger ObjectBox to generate persistence code tailored for this class.
 *
 * @link http://objectbox.io/documentation/entity-annotations/
 * @link http://objectbox.io/documentation/relations/
 *
 */
@Entity
public class Server {

    /**
     *  every object has an ID of type long to efficiently get or reference objects
     */
    @Id
    private long id;

    private String ipAddress;

    private String name;

    @Backlink
    ToMany<Car> cars;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ToMany<Car> getCars() {
        return cars;
    }

    public void setCars(ToMany<Car> cars) {
        this.cars = cars;
    }
}
