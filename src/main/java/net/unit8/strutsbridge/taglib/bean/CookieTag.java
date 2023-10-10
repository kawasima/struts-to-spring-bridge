package net.unit8.strutsbridge.taglib.bean;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import net.unit8.strutsbridge.TagUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import java.util.ArrayList;

public class CookieTag extends RequestContextAwareTag {
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
     * The message resources for this package.
     */
    private static final MessageSource messages;

    static {
        messages = new ResourceBundleMessageSource();
        ((ResourceBundleMessageSource)messages).setBasename("org.apache.struts.taglib.bean.LocalStrings");
    }

    /**
     * Return an array of Cookies if <code>multiple</code> is non-null.
     */
    protected String multiple = null;

    public String getMultiple() {
        return (this.multiple);
    }

    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }


    /**
     * The name of the cookie whose value is to be exposed.
     */
    protected String name = null;

    public String getName() {
        return (this.name);
    }

    public void setName(String name) {
        this.name = name;
    }


    /**
     * The default value to return if no cookie of the specified name is found.
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
     * Retrieve the required property and expose it as a scripting variable.
     *
     * @exception JspException if a JSP exception has occurred
     */
    @Override
    public int doStartTagInternal() throws JspException {

        // Retrieve the required cookie value(s)
        ArrayList values = new ArrayList();
        Cookie cookies[] =
                ((HttpServletRequest) pageContext.getRequest()).getCookies();
        if (cookies == null)
            cookies = new Cookie[0];

        for (int i = 0; i < cookies.length; i++) {
            if (name.equals(cookies[i].getName()))
                values.add(cookies[i]);
        }
        if ((values.size() < 1) && (value != null))
            values.add(new Cookie(name, value));
        if (values.size() < 1) {
            JspException e = new JspException
                    (messages.getMessage("cookie.get", new Object[]{name}, getRequestContext().getLocale()));
            TagUtils.getInstance().saveException(pageContext, e);
            throw e;
        }

        // Expose an appropriate variable containing these results
        if (multiple == null) {
            Cookie cookie = (Cookie) values.get(0);
            pageContext.setAttribute(id, cookie);
        } else {
            cookies = new Cookie[values.size()];
            pageContext.setAttribute(id, values.toArray(cookies));
        }
        return (SKIP_BODY);

    }


    /**
     * Release all allocated resources.
     */
    public void release() {

        super.release();
        id = null;
        multiple = null;
        name = null;
        value = null;

    }
}
