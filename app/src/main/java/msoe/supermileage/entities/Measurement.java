package msoe.supermileage.entities;


import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * An individual measurement that makes up a config file.
 * <p>
 * The @Entity annotation identifies Server as a persistable entity.
 * This will trigger ObjectBox to generate persistence code tailored for this class.
 *
 * @author braithwaitec
 * @link http://objectbox.io/documentation/entity-annotations/
 * @link http://objectbox.io/documentation/relations/
 */
@Entity
public class Measurement {
    /**
     * every object has an ID of type long to efficiently get or reference objects
     */
    @Id
    private long id;

    private String label;

    private String color;

    private String units;

    private int min;

    private int max;

    private boolean displayAlways;

    private ToOne<Config> config;

    public Measurement() {
        // Entity is expected to have a no-arg constructor
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public boolean isDisplayAlways() {
        return displayAlways;
    }

    public void setDisplayAlways(boolean displayAlways) {
        this.displayAlways = displayAlways;
    }

    public ToOne<Config> getConfig() {
        return config;
    }

    public void setConfig(ToOne<Config> config) {
        this.config = config;
    }
}
