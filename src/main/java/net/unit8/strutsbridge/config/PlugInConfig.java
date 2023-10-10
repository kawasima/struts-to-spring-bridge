package net.unit8.strutsbridge.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PlugInConfig implements Serializable {
    /**
     * Has this component been completely configured?
     */
    protected boolean configured = false;


    /**
     * A <code>Map</code> of the name-value pairs that will be used to
     * configure the property values of a <code>PlugIn</code> instance.
     */
    protected Map properties = new HashMap();


    // ------------------------------------------------------------- Properties


    /**
     * The fully qualified Java class name of the <code>PlugIn</code>
     * implementation class being configured.
     */
    protected String className = null;

    public String getClassName() {
        return (this.className);
    }

    public void setClassName(String className) {
        this.className = className;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Add a new property name and value to the set that will be used to
     * configure the <code>PlugIn</code> instance.
     *
     * @param name  Property name
     * @param value Property value
     */
    public void addProperty(String name, String value) {

        if (configured) {
            throw new IllegalStateException("Configuration is frozen");
        }
        properties.put(name, value);

    }


    /**
     * Freeze the configuration of this component.
     */
    public void freeze() {

        configured = true;

    }


    /**
     * Return the properties that will be used to configure a
     * <code>PlugIn</code> instance.
     */
    public Map getProperties() {

        return (properties);

    }
}
