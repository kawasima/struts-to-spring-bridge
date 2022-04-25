package net.unit8.strutsbridge.taglib;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.Locale;

public class ScopeLookup {
    private static final MessageSource messages;

    static {
        messages = new ResourceBundleMessageSource();
        ((ResourceBundleMessageSource)messages).setBasename("org.apache.struts.taglib.LocalStrings");
    }

    public Object lookup(PageContext pageContext, String name, String scopeName) {
        if (scopeName == null) {
            return pageContext.findAttribute(name);
        }
        return pageContext.getAttribute(name, pageContext.getAttributesScope(scopeName));
    }
    public Object lookup(PageContext pageContext,
                          String name,
                          String property,
                          String scope) throws JspException {
        Locale locale = pageContext.getRequest().getLocale();
        // Look up the requested bean, and return if requested
        Object bean = lookup(pageContext, name, scope);
        if (bean == null) {
            JspException e = null;
            if (scope == null) {
                e = new JspException(messages.getMessage("lookup.bean.any", new Object[]{name}, locale));
            } else {
                e =
                        new JspException(
                                messages.getMessage("lookup.bean", new Object[]{name, scope}, locale));
            }
            throw e;
        }

        if (property == null) {
            return bean;
        }

        // Locate and return the specified property
        try {
            BeanWrapper wrapper = new BeanWrapperImpl(bean);
            return wrapper.getPropertyValue(property);
        } catch (IllegalArgumentException e) {
            throw new JspException(
                    messages.getMessage("lookup.argument", new Object[]{property, name}, locale));
        }
    }
}
