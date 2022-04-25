package net.unit8.strutsbridge.taglib.bean;

import net.unit8.strutsbridge.taglib.AbstractTagTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockJspWriter;
import org.springframework.mock.web.MockPageContext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import static org.assertj.core.api.Assertions.assertThat;

class MessageTagTest extends AbstractTagTest {
    PageContext pageContext;

    @BeforeEach
    void setup() {
        pageContext = createPageContext();
    }

    @Test
    void key() throws JspException {
        final StringBuilder message = new StringBuilder();
        MessageTag tag = new MessageTag() {
			@Override
			protected void writeMessage(String msg) {
				message.append(msg);
			}
		};
        tag.setPageContext(pageContext);
        tag.setKey("test");
        tag.doStartTag();
        assertThat(tag.doEndTag()).isEqualTo(Tag.EVAL_PAGE);
        assertThat(message.toString()).isEqualTo("test message");
    }

    @Test
    void nameProperty() throws JspException {
        final StringBuilder message = new StringBuilder();
        MessageTag tag = new MessageTag() {
			@Override
			protected void writeMessage(String msg) {
				message.append(msg);
			}
		};
        tag.setPageContext(pageContext);
        pageContext.setAttribute("bean", new TestBean("test"));
        tag.setName("bean");
        tag.setProperty("prop1");
        tag.doStartTag();
        assertThat(tag.doEndTag()).isEqualTo(Tag.EVAL_PAGE);
        assertThat(message.toString()).isEqualTo("test message");
    }

}