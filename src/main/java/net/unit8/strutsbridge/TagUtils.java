package net.unit8.strutsbridge;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import net.unit8.strutsbridge.config.ModuleConfig;
import net.unit8.strutsbridge.taglib.html.Constants;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TagUtils {
    /**
     * The message resources for this package.
     */
    private static final MessageSource messages;

    static {
        messages = new ResourceBundleMessageSource();
        ((ResourceBundleMessageSource)messages).setBasename("org.apache.struts.taglib.LocalStrings");
    }

    /**
     * The Singleton instance.
     */
    private static final TagUtils instance = new TagUtils();

    /**
     * Maps lowercase JSP scope names to their PageContext integer constant
     * values.
     */
    private static final Map<String, Integer> scopes = new HashMap<>();

    /**
     * Initialize the scope names map.
     */
    static {
        scopes.put("page", PageContext.PAGE_SCOPE);
        scopes.put("request", PageContext.REQUEST_SCOPE);
        scopes.put("session", PageContext.SESSION_SCOPE);
        scopes.put("application", PageContext.APPLICATION_SCOPE);
    }

    /**
     * Constructor for TagUtils.
     */
    protected TagUtils() {
        super();
    }

    /**
     * Returns the Singleton instance of TagUtils.
     */
    public static TagUtils getInstance() {
        return instance;
    }

    /**
     * Filter the specified string for characters that are sensitive to
     * HTML interpreters, returning the string with these characters replaced
     * by the corresponding character entities.
     *
     * @param value The string to be filtered and returned
     */
    public String filter(String value) {
        return HtmlUtils.htmlEscape(value);
    }

    /**
     * Converts the scope name into its corresponding PageContext constant value.
     * @param scopeName Can be "page", "request", "session", or "application" in any
     * case.
     * @return The constant representing the scope (ie. PageContext.REQUEST_SCOPE).
     * @throws JspException if the scopeName is not a valid name.
     */
    public int getScope(String scopeName) throws JspException {
        Integer scope = (Integer) scopes.get(scopeName.toLowerCase());

        if (scope == null) {
            throw new JspException(messages.getMessage("lookup.scope", new Object[]{scope}, Locale.getDefault()));
        }

        return scope.intValue();
    }

    /**
     * Locate and return the specified bean, from an optionally specified
     * scope, in the specified page context.  If no such bean is found,
     * return <code>null</code> instead.  If an exception is thrown, it will
     * have already been saved via a call to <code>saveException()</code>.
     *
     * @param pageContext Page context to be searched
     * @param name Name of the bean to be retrieved
     * @param scopeName Scope to be searched (page, request, session, application)
     *  or <code>null</code> to use <code>findAttribute()</code> instead
     * @return JavaBean in the specified page context
     * @exception JspException if an invalid scope name
     *  is requested
     */
    public Object lookup(PageContext pageContext, String name, String scopeName)
            throws JspException {

        if (scopeName == null) {
            return pageContext.findAttribute(name);
        }

        try {
            return pageContext.getAttribute(name, instance.getScope(scopeName));

        } catch (JspException e) {
            saveException(pageContext, e);
            throw e;
        }

    }

    /**
     * Locate and return the specified property of the specified bean, from
     * an optionally specified scope, in the specified page context.  If an
     * exception is thrown, it will have already been saved via a call to
     * <code>saveException()</code>.
     *
     * @param pageContext Page context to be searched
     * @param name Name of the bean to be retrieved
     * @param property Name of the property to be retrieved, or
     *  <code>null</code> to retrieve the bean itself
     * @param scope Scope to be searched (page, request, session, application)
     *  or <code>null</code> to use <code>findAttribute()</code> instead
     * @return property of specified JavaBean
     *
     * @exception JspException if an invalid scope name
     *  is requested
     * @exception JspException if the specified bean is not found
     * @exception JspException if accessing this property causes an
     *  IllegalAccessException, IllegalArgumentException,
     *  InvocationTargetException, or NoSuchMethodException
     */
    public Object lookup(
            PageContext pageContext,
            String name,
            String property,
            String scope)
            throws JspException {

        // Look up the requested bean, and return if requested
        Object bean = lookup(pageContext, name, scope);
        if (bean == null) {
            JspException e = null;
            if (scope == null) {
                e = new JspException(messages.getMessage("lookup.bean.any", new Object[]{name}, Locale.getDefault()));
            } else {
                e =
                        new JspException(
                                messages.getMessage("lookup.bean", new Object[]{name, scope}, Locale.getDefault()));
            }
            saveException(pageContext, e);
            throw e;
        }

        if (property == null) {
            return bean;
        }

        // Locate and return the specified property
        try {
            return PropertyUtils.getProperty(bean, property);

        } catch (IllegalAccessException e) {
            saveException(pageContext, e);
            throw new JspException(
                    messages.getMessage("lookup.access", new Object[]{property, name}, Locale.getDefault()));

        } catch (IllegalArgumentException e) {
            saveException(pageContext, e);
            throw new JspException(
                    messages.getMessage("lookup.argument", new Object[]{property, name}, Locale.getDefault()));

        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t == null) {
                t = e;
            }
            saveException(pageContext, t);
            throw new JspException(
                    messages.getMessage("lookup.target", new Object[]{property, name}, Locale.getDefault()));

        } catch (NoSuchMethodException e) {
            saveException(pageContext, e);

            String beanName = name;

            // Name defaults to Contants.BEAN_KEY if no name is specified by
            // an input tag. Thus lookup the bean under the key and use
            // its class name for the exception message.
            if (Constants.BEAN_KEY.equals(name)) {
                Object obj = pageContext.findAttribute(Constants.BEAN_KEY);
                if (obj != null) {
                    beanName = obj.getClass().getName();
                }
            }

            throw new JspException(
                    messages.getMessage("lookup.method", new Object[]{property, beanName}, Locale.getDefault()));
        }

    }

    /**
     * Look up and return a message string, based on the specified parameters.
     *
     * @param pageContext The PageContext associated with this request
     * @param bundle Name of the servlet context attribute for our
     *  message resources bundle
     * @param locale Name of the session attribute for our user's Locale
     * @param key Message key to be looked up and returned
     * @return message string
     *
     * @exception JspException if a lookup error occurs (will have been
     *  saved in the request already)
     */
    public String message(
            PageContext pageContext,
            String bundle,
            String locale,
            String key)
            throws JspException {

        return message(pageContext, bundle, locale, key, null);

    }

    /**
     * Look up and return a message string, based on the specified parameters.
     *
     * @param pageContext The PageContext associated with this request
     * @param bundle Name of the servlet context attribute for our
     *  message resources bundle
     * @param locale Name of the session attribute for our user's Locale
     * @param key Message key to be looked up and returned
     * @param args Replacement parameters for this message
     * @return message string
     * @exception JspException if a lookup error occurs (will have been
     *  saved in the request already)
     */
    public String message(
            PageContext pageContext,
            String bundle,
            String locale,
            String key,
            Object args[])
            throws JspException {

        MessageSource resources =
                retrieveMessageResources(pageContext, bundle, false);
        Locale userLocale = pageContext.getRequest().getLocale();
        String message = null;
        if (args == null) {
            message = resources.getMessage(userLocale, key);
        } else {
            message = resources.getMessage(userLocale, key, args);
        }
        if ((message == null) && log.isDebugEnabled()) {
            // log missing key to ease debugging
            log.debug(resources.getMessage("message.resources", key, bundle, locale));
        }
        return message;
    }

    /**
     * Returns the appropriate MessageResources object for the current module and
     * the given bundle.
     *
     * @param pageContext Search the context's scopes for the resources.
     * @param bundle The bundle name to look for.  If this is <code>null</code>, the
     * default bundle name is used.
     * @return MessageResources The bundle's resources stored in some scope.
     * @throws JspException if the MessageResources object could not be found.
     */
    public MessageSource retrieveMessageResources(
            PageContext pageContext,
            String bundle,
            boolean checkPageScope)
            throws JspException {

        MessageSource resources = null;

        if (bundle == null) {
            bundle = Globals.MESSAGES_KEY;
        }

        if (checkPageScope) {
            resources =
                    (MessageSource) pageContext.getAttribute(
                            bundle,
                            PageContext.PAGE_SCOPE);
        }

        if (resources == null) {
            resources =
                    (MessageSource) pageContext.getAttribute(
                            bundle,
                            PageContext.REQUEST_SCOPE);
        }

        if (resources == null) {
            ModuleConfig moduleConfig = getModuleConfig(pageContext);
            resources =
                    (MessageSource) pageContext.getAttribute(
                            bundle + moduleConfig.getPrefix(),
                            PageContext.APPLICATION_SCOPE);
        }

        if (resources == null) {
            resources =
                    (MessageSource) pageContext.getAttribute(
                            bundle,
                            PageContext.APPLICATION_SCOPE);
        }

        if (resources == null) {
            JspException e =
                    new JspException(messages.getMessage("message.bundle", new Object[]{bundle}, Locale.getDefault()));
            saveException(pageContext, e);
            throw e;
        }

        return resources;
    }

    /**
     * Save the specified exception as a request attribute for later use.
     *
     * @param pageContext The PageContext for the current page
     * @param exception The exception to be saved
     */
    public void saveException(PageContext pageContext, Throwable exception) {
        pageContext.setAttribute(
                Globals.EXCEPTION_KEY,
                exception,
                PageContext.REQUEST_SCOPE);
    }
    /**
     * Write the specified text as the response to the writer associated with
     * this page.  <strong>WARNING</strong> - If you are writing body content
     * from the <code>doAfterBody()</code> method of a custom tag class that
     * implements <code>BodyTag</code>, you should be calling
     * <code>writePrevious()</code> instead.
     *
     * @param pageContext The PageContext object for this page
     * @param text The text to be written
     *
     * @exception JspException if an input/output error occurs (already saved)
     */
    public void write(PageContext pageContext, String text)
            throws JspException {

        JspWriter writer = pageContext.getOut();

        try {
            writer.print(text);

        } catch (IOException e) {
            TagUtils.getInstance().saveException(pageContext, e);
            throw new JspException
                    (messages.getMessage("write.io", new Object[]{e.toString()}, Locale.getDefault()));
        }

    }

}
