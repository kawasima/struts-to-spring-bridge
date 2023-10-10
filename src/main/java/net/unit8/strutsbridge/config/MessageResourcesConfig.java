package net.unit8.strutsbridge.config;

import net.unit8.strutsbridge.Globals;

import java.io.Serializable;

public class MessageResourcesConfig implements Serializable {
    // ----------------------------------------------------- Instance Variables


    /**
     * Has this component been completely configured?
     */
    protected boolean configured = false;


    // ------------------------------------------------------------- Properties


    /**
     * Fully qualified Java class name of the MessageResourcesFactory class
     * we should use.
     */
    protected String factory =
            "org.apache.struts.util.PropertyMessageResourcesFactory";

    public String getFactory() {
        return (this.factory);
    }

    public void setFactory(String factory) {
        if (configured) {
            throw new IllegalStateException("Configuration is frozen");
        }
        this.factory = factory;
    }


    /**
     * The servlet context attributes key under which this MessageResources
     * instance is stored.
     */
    protected String key = Globals.MESSAGES_KEY;

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
     * Should we return <code>null</code> for unknown message keys?
     */
    protected boolean nullValue = true;

    public boolean getNull() {
        return (this.nullValue);
    }

    public void setNull(boolean nullValue) {
        if (configured) {
            throw new IllegalStateException("Configuration is frozen");
        }
        this.nullValue = nullValue;
    }

    /**
     * Indicates whether 'escape processing' should be performed on
     * the error message string.
     */
    private boolean escape = true;

    /**
     * Indicates whether 'escape processing' should be performed on
     * the error message string.
     *
     * @since Struts 1.2.8
     */
    public boolean isEscape() {
        return escape;
    }

    /**
     * Set whether 'escape processing' should be performed on
     * the error message string.
     *
     * @since Struts 1.2.8
     */
    public void setEscape(boolean escape) {
        this.escape = escape;
    }

    /**
     * Parameter that is passed to the <code>createResources()</code> method
     * of our MessageResourcesFactory implementation.
     */
    protected String parameter = null;

    public String getParameter() {
        return (this.parameter);
    }

    public void setParameter(String parameter) {
        if (configured) {
            throw new IllegalStateException("Configuration is frozen");
        }
        this.parameter = parameter;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Freeze the configuration of this component.
     */
    public void freeze() {

        configured = true;

    }


    /**
     * Return a String representation of this object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("MessageResourcesConfig[");
        sb.append("factory=");
        sb.append(this.factory);
        sb.append(",null=");
        sb.append(this.nullValue);
        sb.append(",escape=");
        sb.append(this.escape);
        sb.append(",parameter=");
        sb.append(this.parameter);
        sb.append("]");
        return (sb.toString());

    }
}
