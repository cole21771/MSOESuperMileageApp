package msoe.supermileage.entities;

import java.io.IOException;
import java.net.InetAddress;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;

/**
 * The @Entity annotation identifies Server as a persistable entity.
 * This will trigger ObjectBox to generate persistence code tailored for this class.
 *
 * @link http://objectbox.io/documentation/entity-annotations/
 * @link http://objectbox.io/documentation/relations/
 */
@Entity
public class Server {

    /**
     * every object has an ID of type long to efficiently get or reference objects
     */
    @Id
    private long id;

    private String ipAddress;

    private String port;

    private String name;

    @Transient
    private boolean reachable;

    @Backlink
    private ToMany<Car> cars;

    public Server() {
        // Entity is expected to have a no-arg constructor
    }

    public Server(String name, String ipAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
    }

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

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
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

    public boolean isReachable() {
        return reachable;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }

    public void checkIsReachable() {
        boolean result = false;
        try {
            result = InetAddress.getByName(this.ipAddress).isReachable(3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setReachable(result);
    }
}
