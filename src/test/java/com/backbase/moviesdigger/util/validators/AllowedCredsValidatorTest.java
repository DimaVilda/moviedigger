package com.backbase.moviesdigger.util.validators;


import com.backbase.moviesdigger.utils.validation.validators.AllowedCreds;
import com.backbase.moviesdigger.utils.validation.validators.AllowedCredsValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class AllowedCredsValidatorTest {

    @Mock
    private ConstraintValidatorContext validatorContext;
    @Mock
    private HibernateConstraintValidatorContext hibernateConstraintValidatorContext;
    @Mock
    private HibernateConstraintViolationBuilder violationBuilder;
    private AllowedCredsValidator allowedCredsValidator;

    @BeforeEach
    void setUp() {
        allowedCredsValidator = new AllowedCredsValidator();
    }

    @Test
    void shouldReturnTrueWhenUserNameCredValid() {
        AllowedCreds allowedCreds = mock(AllowedCreds.class);
        String userName = "dima*";

        allowedCredsValidator.initialize(allowedCreds);

        assertThat(allowedCredsValidator.isValid(userName, validatorContext), is(Boolean.TRUE));
    }

    @Test
    void shouldReturnFalseWhenUserNameCredIsEmpty() {
        AllowedCreds allowedCreds = mock(AllowedCreds.class);
        String userName = "";
        allowedCredsValidator.initialize(allowedCreds);

        doReturn(hibernateConstraintValidatorContext).when(validatorContext)
                .unwrap(HibernateConstraintValidatorContext.class);
        doReturn(hibernateConstraintValidatorContext)
                .when(hibernateConstraintValidatorContext).addMessageParameter("credValue", userName);
        doReturn(violationBuilder)
                .when(hibernateConstraintValidatorContext).buildConstraintViolationWithTemplate(
                        "Your provided creds contain illegal characters:" + userName);
        doReturn(validatorContext).when(violationBuilder).addConstraintViolation();

        assertThat(allowedCredsValidator.isValid("", validatorContext), is(Boolean.FALSE));
    }

    @Test
    void shouldReturnFalseWhenUserNameHasNotAccessibleChars() {
        AllowedCreds allowedCreds = mock(AllowedCreds.class);
        String userName = "dima@alp#";

        allowedCredsValidator.initialize(allowedCreds);

        doReturn(hibernateConstraintValidatorContext).when(validatorContext)
                .unwrap(HibernateConstraintValidatorContext.class);
        doReturn(hibernateConstraintValidatorContext)
                .when(hibernateConstraintValidatorContext).addMessageParameter("credValue", userName);
        doReturn(violationBuilder)
                .when(hibernateConstraintValidatorContext).buildConstraintViolationWithTemplate(
                        "Your provided creds contain illegal characters:" + userName);
        doReturn(validatorContext).when(violationBuilder).addConstraintViolation();

        assertThat(allowedCredsValidator.isValid(userName, validatorContext), is(Boolean.FALSE));
    }
}
