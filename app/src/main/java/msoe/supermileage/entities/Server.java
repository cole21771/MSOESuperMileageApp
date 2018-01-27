package msoe.supermileage.entities;

import android.webkit.URLUtil;

import io.objectbox.Box;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToOne;
import msoe.supermileage.WebUtility;

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

    private ToOne<Config> config;

    @Transient
    private boolean reachable;

    public Server() {
        // Entity is expected to have a no-arg constructor
    }

    public Server(String name, String ipAddress, String port) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
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

    public ToOne<Config> getConfig() {
        return config;
    }

    public void setConfig(ToOne<Config> config) {
        this.config = config;
    }

    public boolean isReachable() {
        return reachable;
    }

    public String url() {
        String result = null;

        if (this.ipAddress != null && this.port != null) {
            result = "http://" + this.ipAddress + ":" + this.port;
        } else if (this.ipAddress != null) {
            result = "http://" + this.ipAddress;
        }

        return URLUtil.isValidUrl(result) ? result : null;
    }

    public void checkReachable(final Box<Server> serverBox) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                boolean wasReachable = reachable;
                boolean isReachable = WebUtility.isReachable(ipAddress, Integer.parseInt(port));
                if (wasReachable != isReachable) {
                    reachable = isReachable;
                    serverBox.put(Server.this);
                }
            }
        };
        thread.setDaemon(false);
        thread.start();
    }
}
