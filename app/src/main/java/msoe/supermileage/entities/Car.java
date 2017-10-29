package msoe.supermileage.entities;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

/**
 * The @Entity annotation identifies Server as a persistable entity.
 * This will trigger ObjectBox to generate persistence code tailored for this class.
 *
 * @link http://objectbox.io/documentation/entity-annotations/
 * @link http://objectbox.io/documentation/relations/
 */
@Entity
public class Car {

    /**
     * every object has an ID of type long to efficiently get or reference objects
     */
    @Id
    private long id;

    private String name;

    private ToOne<Server> server;

    @Backlink
    private ToMany<Config> configs;

    public Car() {
        // Entity is expected to have a no-arg constructor
    }

    public Car(String name) {
        this.name = name;
    }

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

    public ToOne<Server> getServer() {
        return server;
    }

    public void setServer(ToOne<Server> server) {
        this.server = server;
    }

    public ToMany<Config> getConfigs() {
        return configs;
    }

    public void setConfigs(ToMany<Config> configs) {
        this.configs = configs;
    }

}
