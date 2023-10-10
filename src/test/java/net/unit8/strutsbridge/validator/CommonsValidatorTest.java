package net.unit8.strutsbridge.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.MapBindingResult;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class CommonsValidatorTest {
    CommonsValidator validator;
    MessageSource messages;

    @BeforeEach
    void setup() throws IOException, SAXException {
        try(InputStream is = getClass().getResourceAsStream("/validator/validation-1.xml")) {
            validator = new CommonsValidator(new InputStream[]{is});
        }
        messages = new ResourceBundleMessageSource();
        ((ResourceBundleMessageSource) messages).setBasename("ValidationMessages");
    }

    @Test
    void test() {
        MapBindingResult bindingResult = new MapBindingResult(new HashMap<>(), "");
        ProductForm form = new ProductForm();
        //form.setName("PRD1");
        validator.validate(form, bindingResult);
        bindingResult.getAllErrors().forEach(err -> {
            System.out.println(messages.getMessage(err.getCode(), err.getArguments(), Locale.getDefault()));
        });
    }

}