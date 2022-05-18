package net.unit8.strutsbridge.validator;

import org.apache.commons.validator.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class CommonsValidator implements org.springframework.validation.Validator{
    private final ValidatorResources resources;

    public CommonsValidator(InputStream[] validationDef) throws SAXException, IOException {
        resources = new ValidatorResources(validationDef);
    }

    public CommonsValidator(String[] validationDef) throws SAXException, IOException {
        resources = new ValidatorResources(validationDef);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    private String formName(Class<?> clazz) {
        return Optional.ofNullable(AnnotationUtils.findAnnotation(clazz, FormName.class))
                .map(FormName::value)
                .orElseGet(clazz::getSimpleName);
    }

    @Override
    public void validate(Object target, Errors errors) {
        String formName = Optional.of(target)
                .map(Object::getClass)
                .map(this::formName)
                .orElse(null);
        Validator validator = new Validator(resources, formName);
        validator.setParameter(Validator.BEAN_PARAM, target);

        try {
            ValidatorResults results = validator.validate();
            for (String propName: results.getPropertyNames()) {
                FieldError fieldError = errors.getFieldError(propName);
                if (fieldError == null || !fieldError.isBindingFailure()) {
                    if (errors instanceof BindingResult) {
                        BindingResult bindingResult = (BindingResult) errors;
                        String nestedField = bindingResult.getNestedPath() + propName;
                        ValidatorResult validatorResult = results.getValidatorResult(propName);
                        List<ObjectError> messages = renderMessage(validatorResult, propName);
                        messages.forEach(bindingResult::addError);
                    }
                }
            }
        } catch (ValidatorException e) {
            e.printStackTrace();
        }
    }

    private List<ObjectError> renderMessage(ValidatorResult result, String objectName) {
        List<ObjectError> errorList = new ArrayList<>();
        Iterator<String> actions = result.getActions();
        while(actions.hasNext()) {
            String dependName = actions.next();
            if (result.isValid(dependName)) {
                continue;
            }
            ValidatorAction action = resources.getValidatorAction(dependName);
            Field field = result.getField();
            errorList.add(new ObjectError(objectName, new String[]{ action.getMsg()}, getArgs(action.getName(), field), action.getMsg()));
        }
        return errorList;
    }

    String[] getArgs(String actionName, Field field) {
        String[] argMessages = new String[4];

        Arg[] args =
            new Arg[] {
                field.getArg(actionName,0),
                field.getArg(actionName,1),
                field.getArg(actionName,2),
                field.getArg(actionName,3)};

        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                continue;
            }

            argMessages[i] = args[i].getKey();

        }

        return argMessages;
    }
}
