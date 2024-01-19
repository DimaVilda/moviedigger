package com.backbase.moviesdigger.utils.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

/**
 * Validator logic to check if provided userName and(or) password creds are valid
 */
public class AllowedCredsValidator implements ConstraintValidator<AllowedCreds, String> {


    @Override
    public boolean isValid(String credValue, ConstraintValidatorContext context) {
        if (isValidCred(credValue)) {
            return true;
        }
        buildViolationForInvalidCred(credValue, context);
        return false;
    }
    private boolean isValidCred(String credValue) {
        String regex = "^[\\w*!&?@$]+$";
        return credValue.matches(regex);
    }

    private void buildViolationForInvalidCred(String credValue, ConstraintValidatorContext context) {
        context.unwrap(HibernateConstraintValidatorContext.class)
                .addMessageParameter("credValue", credValue)
                .buildConstraintViolationWithTemplate("Your provided creds contain illegal characters:" + credValue)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
    }
}
