package net.unit8.strutsbridge.validator;

import org.apache.commons.validator.Field;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.util.ValidatorUtils;

public class CustomValidator {
  public static boolean validateRequired(Object bean, ValidatorAction action, Field field){
    String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
    return !GenericValidator.isBlankOrNull(value);
  }
}
