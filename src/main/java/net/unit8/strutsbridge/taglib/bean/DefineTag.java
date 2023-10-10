package net.unit8.strutsbridge.taglib.bean;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import net.unit8.strutsbridge.TagUtils;
import org.slf4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

public class DefineTag extends BodyTagSupport {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(DefineTag.class);

    /**
     * The message resources for this package.
     */
    private static final MessageSource messages;

    static {
        messages = new ResourceBundleMessageSource();
        ((ResourceBundleMessageSource)messages).setBasename("org.apache.struts.taglib.bean.LocalStrings");
    }

    /**
     * The body content of this tag (if any).
     */
    protected String body = null;


    // ------------------------------------------------------------- Properties


    /**
     * The name of the scripting variable that will be exposed as a page
     * scope attribute.
     */
    protected String id = null;

    public String getId() {
        return (this.id);
    }

    public void setId(String id) {
        this.id = id;
    }


    /**
     * The name of the bean owning the property to be exposed.
     */
    protected String name = null;

    public String getName() {
        return (this.name);
    }

    public void setName(String name) {
        this.name = name;
    }


    /**
     * The name of the property to be retrieved.
     */
    protected String property = null;

    public String getProperty() {
        return (this.property);
    }

    public void setProperty(String property) {
        this.property = property;
    }


    /**
     * The scope within which to search for the specified bean.
     */
    protected String scope = null;

    public String getScope() {
        return (this.scope);
    }

    public void setScope(String scope) {
        this.scope = scope;
    }


    /**
     * The scope within which the newly defined bean will be creatd.
     */
    protected String toScope = null;

    public String getToScope() {
        return (this.toScope);
    }

    public void setToScope(String toScope) {
        this.toScope = toScope;
    }


    /**
     * The fully qualified Java class name of the value to be exposed.
     */
    protected String type = null;

    public String getType() {
        return (this.type);
    }

    public void setType(String type) {
        this.type = type;
    }


    /**
     * The (String) value to which the defined bean will be set.
     */
    protected String value = null;

    public String getValue() {
        return (this.value);
    }

    public void setValue(String value) {
        this.value = value;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Check if we need to evaluate the body of the tag
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {

        return (EVAL_BODY_TAG);

    }


    /**
     * Save the body content of this tag (if any), or throw a JspException
     * if the value was already defined.
     *
     * @exception JspException if value was defined by an attribute
     */
    public int doAfterBody() throws JspException {
        if (bodyContent != null) {
            body = bodyContent.getString();
            if (body != null) {
                body = body.trim();
            }
            if (body.length() < 1) {
                body = null;
            }
        }
        return (SKIP_BODY);

    }


    /**
     * Retrieve the required property and expose it as a scripting variable.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doEndTag() throws JspException {

        // Enforce restriction on ways to declare the new value
        int n = 0;
        if (this.body != null) {
            n++;
        }
        if (this.name != null) {
            n++;
        }
        if (this.value != null) {
            n++;
        }
        if (n > 1) {
            JspException e =
                    new JspException(messages.getMessage("define.value", new Object[]{id}, Locale.getDefault()));
            TagUtils.getInstance().saveException(pageContext, e);
            throw e;
        }

        // Retrieve the required property value
        Object value = this.value;
        if ((value == null) && (name != null)) {
            value = TagUtils.getInstance().lookup(pageContext, name, property, scope);
        }
        if ((value == null) && (body != null)) {
            value = body;
        }
        if (value == null) {
            JspException e =
                    new JspException(messages.getMessage("define.null", new Object[]{id}, Locale.getDefault()));
            TagUtils.getInstance().saveException(pageContext, e);
            throw e;
        }

        // Expose this value as a scripting variable
        int inScope = PageContext.PAGE_SCOPE;
        try {
            if (toScope != null) {
                inScope = TagUtils.getInstance().getScope(toScope);
            }
        } catch (JspException e) {
            log.warn("toScope was invalid name so we default to PAGE_SCOPE", e);
        }

        pageContext.setAttribute(id, value, inScope);

        // Continue processing this page
        return (EVAL_PAGE);

    }

    /**
     * Release all allocated resources.
     */
    public void release() {

        super.release();
        body = null;
        id = null;
        name = null;
        property = null;
        scope = null;
        toScope = "page";
        type = null;
        value = null;

    }}
