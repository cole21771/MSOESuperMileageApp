package msoe.supermileage.entities;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

/**
 * Represents a configuration file that comes from a server.
 * <p>
 * The @Entity annotation identifies Server as a persistable entity.
 * This will trigger ObjectBox to generate persistence code tailored for this class.
 *
 * @author braithwaitec
 * @link http://objectbox.io/documentation/entity-annotations/
 * @link http://objectbox.io/documentation/relations/
 */
@Entity
public class Config {

    /**
     * every object has an ID of type long to efficiently get or reference objects
     */
    @Id
    private long id;

    private String name;

    private String json;

    private ToMany<Measurement> measurements;

    public Config() {
        // Entity is expected to have a no-arg constructor
    }

    public Config(String name, String json) {
        this.name = name;
        this.json = json;
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

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public ToMany<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(ToMany<Measurement> measurements) {
        this.measurements = measurements;
    }
}
