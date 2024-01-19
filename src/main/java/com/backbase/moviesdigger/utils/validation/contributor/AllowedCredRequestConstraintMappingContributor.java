package com.backbase.moviesdigger.utils.validation.contributor;

import com.backbase.moviesdigger.client.spec.model.UserInformationRequestBody;
import com.backbase.moviesdigger.utils.validation.groups.UserNameGroup;
import com.backbase.moviesdigger.utils.validation.groups.UserPasswordGroup;
import com.backbase.moviesdigger.utils.validation.validators.AllowedCreds;
import org.hibernate.validator.cfg.GenericConstraintDef;
import org.hibernate.validator.spi.cfg.ConstraintMappingContributor;

import java.lang.annotation.Annotation;

public class AllowedCredRequestConstraintMappingContributor implements ConstraintMappingContributor {

    @Override
    public void createConstraintMappings(ConstraintMappingBuilder constraintMappingBuilder) {
        constraintMappingBuilder.addConstraintMapping()
                .type(UserInformationRequestBody.class)
                .field("userName")
                .constraint(createConstraint(AllowedCreds.class, UserNameGroup.class))
                .field("password")
                .constraint(createConstraint(AllowedCreds.class, UserPasswordGroup.class));
    }

    private <T extends Annotation> GenericConstraintDef<T> createConstraint(Class<T> annotation, Class<?> group) {
        return new GenericConstraintDef<>(annotation).groups(group);
    }
}
