package net.unit8.strutsbridge.config;

import net.unit8.strutsbridge.Globals;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DataSourceConfig implements Serializable {
    // ----------------------------------------------------- Instance Variables


    /**
     * Has this component been completely configured?
     */
    protected boolean configured = false;


    // ------------------------------------------------------------- Properties


    /**
     * The servlet context attribute key under which this data source
     * is stored and made available.
     */
    protected String key = Globals.DATA_SOURCE_KEY;

    public String getKey() {
        return (this.key);
    }

    public void setKey(String key) {
        if (configured) {
            throw new IllegalStateException("Configuration is frozen");
        }
        this.key = key;
    }


    /**
     * The custom configuration properties for this data source implementation.
     */
    protected HashMap properties = new HashMap();

    public Map getProperties() {
        return (this.properties);
    }


    /**
     * The fully qualified class name of the <code>javax.sql.DataSource</code>
     * implementation class.
     */
    protected String type;

    public String getType() {
        return (this.type);
    }

    public void setType(String type) {
        if (configured) {
            throw new IllegalStateException("Configuration is frozen");
        }
        this.type = type;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Add a new custom configuration property.
     *
     * @param name Custom property name
     * @param value Custom property value
     */
    public void addProperty(String name, String value) {

        if (configured) {
            throw new IllegalStateException("Configuration is frozen");
        }
        properties.put(name, value);

    }


    /**
     * Freeze the configuration of this data source.
     */
    public void freeze() {

        configured = true;

    }


    /**
     * Return a String representation of this object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("DataSourceConfig[");
        sb.append("key=");
        sb.append(key);
        sb.append(",type=");
        sb.append(type);
        Iterator names = properties.keySet().iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            String value = (String) properties.get(name);
            sb.append(',');
            sb.append(name);
            sb.append('=');
            sb.append(value);
        }
        sb.append("]");
        return (sb.toString());

    }
}
