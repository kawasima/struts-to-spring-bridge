package net.unit8.strutsbridge.taglib.bean;

import net.unit8.strutsbridge.taglib.ScopeLookup;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;
import org.springframework.web.util.JavaScriptUtils;
import org.springframework.web.util.TagUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import java.io.IOException;
import java.util.Locale;

public class MessageTag extends HtmlEscapingAwareTag {
    private static final MessageSource messages;

    static {
        messages = new ResourceBundleMessageSource();
        ((ResourceBundleMessageSource)messages).setBasename("org.apache.struts.taglib.bean.LocalStrings");
    }

    private final ScopeLookup scopeLookup;
    private MessageSourceResolvable message;
	@Nullable
	private String var;

	private boolean javaScriptEscape = false;
    protected String arg0 = null;
    protected String arg1 = null;
    protected String arg2 = null;
    protected String arg3 = null;
    protected String arg4 = null;
    protected String bundle = null;
    /**
     * @deprecated
     */
    protected static final Locale defaultLocale = Locale.getDefault();
    protected String key = null;
    protected String name = null;
    protected String property = null;
    protected String scope = null;
    protected String localeKey = "org.apache.struts.action.LOCALE";

    public MessageTag() {
        scopeLookup = new ScopeLookup();
    }

    public String getArg0() {
        return this.arg0;
    }

    public void setArg0(String arg0) {
        this.arg0 = arg0;
    }

    public String getArg1() {
        return this.arg1;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public String getArg2() {
        return this.arg2;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }

    public String getArg3() {
        return this.arg3;
    }

    public void setArg3(String arg3) {
        this.arg3 = arg3;
    }

    public String getArg4() {
        return this.arg4;
    }

    public void setArg4(String arg4) {
        this.arg4 = arg4;
    }

    public String getBundle() {
        return this.bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getLocale() {
        return this.localeKey;
    }

    public void setLocale(String localeKey) {
        this.localeKey = localeKey;
    }

    @Override
    protected final int doStartTagInternal() throws JspException, IOException {
		return EVAL_BODY_INCLUDE;
    }
    /*
    public int doStartTag() throws JspException {
        String key = this.key;
        if (key == null) {
            Object value = TagUtils.getInstance().lookup(super.pageContext, this.name, this.property, this.scope);
            if (value != null && !(value instanceof String)) {
                JspException e = new JspException(messages.getMessage("message.property", key));
                TagUtils.getInstance().saveException(super.pageContext, e);
                throw e;
            }

            key = (String) value;
        }

        Object[] args = new Object[]{this.arg0, this.arg1, this.arg2, this.arg3, this.arg4};
        String message = TagUtils.getInstance().message(super.pageContext, this.bundle, this.localeKey, key, args);
        if (message == null) {
            JspException e = new JspException(messages.getMessage("message.message", "\"" + key + "\""));
            TagUtils.getInstance().saveException(super.pageContext, e);
            throw e;
        } else {
            TagUtils.getInstance().write(super.pageContext, message);
            return 0;
        }
    }
    */
	@Override
	public int doEndTag() throws JspException {
		try {
			// Resolve the unescaped message.
			String msg = resolveMessage();

			// HTML and/or JavaScript escape, if demanded.
			msg = htmlEscape(msg);
			msg = this.javaScriptEscape ? JavaScriptUtils.javaScriptEscape(msg) : msg;

			// Expose as variable, if demanded, else write to the page.
			if (this.var != null) {
				this.pageContext.setAttribute(this.var, msg, TagUtils.getScope(this.scope));
			}
			else {
				writeMessage(msg);
			}

			return EVAL_PAGE;
		}
		catch (IOException ex) {
			throw new JspTagException(ex.getMessage(), ex);
		}
		catch (NoSuchMessageException ex) {
			throw new JspTagException(getNoSuchMessageExceptionDescription(ex));
		}
	}

    public void release() {
        super.release();
        this.arg0 = null;
        this.arg1 = null;
        this.arg2 = null;
        this.arg3 = null;
        this.arg4 = null;
        this.bundle = "org.apache.struts.action.MESSAGE";
        this.key = null;
        this.name = null;
        this.property = null;
        this.scope = null;
        this.localeKey = "org.apache.struts.action.LOCALE";
    }

	protected String resolveMessage() throws JspException, NoSuchMessageException {
		MessageSource messageSource = getMessageSource();

		// Evaluate the specified MessageSourceResolvable, if any.
		if (this.message != null) {
			// We have a given MessageSourceResolvable.
			return messageSource.getMessage(this.message, getRequestContext().getLocale());
		}
        String key = this.key;
        if (key == null) {
            Object value = scopeLookup.lookup(super.pageContext, this.name, this.property, this.scope);
            if (value != null && !(value instanceof String)) {
                JspException e = new JspException(messages.getMessage("message.property", new Object[]{key}, getRequestContext().getLocale()));
                throw e;
            }

            key = (String) value;
        }
		if (key != null) {
			// We have a code or default text that we need to resolve.
			Object[] argumentsArray = { arg0, arg1, arg2, arg3, arg4 };
            return messageSource.getMessage(
                    key, argumentsArray, getRequestContext().getLocale());
		}

		throw new JspTagException("No resolvable message");
	}

	/**
	 * Write the message to the page.
	 * <p>Can be overridden in subclasses, e.g. for testing purposes.
	 * @param msg the message to write
	 * @throws IOException if writing failed
	 */
	protected void writeMessage(String msg) throws IOException {
		this.pageContext.getOut().write(msg);
	}

	/**
	 * Use the current RequestContext's application context as MessageSource.
	 */
	protected MessageSource getMessageSource() {
		return getRequestContext().getMessageSource();
	}

	/**
	 * Return default exception message.
	 */
	protected String getNoSuchMessageExceptionDescription(NoSuchMessageException ex) {
		return ex.getMessage();
	}
}
