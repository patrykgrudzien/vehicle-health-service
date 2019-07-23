package me.grudzien.patryk.utils.validation;

import org.springframework.stereotype.Service;

import org.apache.commons.lang3.ArrayUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import java.util.Set;

@Service
public class ValidationService {

    private static final String VALIDATION_FAILED_MSG_TEMPLATE = "Bean of type '%s' failed validation, number of violations=%d";

    private final Validator beanValidator;

    public ValidationService(final Validator beanValidator) {
        this.beanValidator = beanValidator;
    }

    public <T> void validate(final T bean) {
        this.validateObject(bean, ValidationSequence.class);
    }

    public <T> void validate(final T bean, final Class... groups) {
        this.validateObject(bean, ArrayUtils.add(groups, ValidationSequence.class));
    }

    private <T> void validateObject(final T bean, final Class... groups) {
        final Set<ConstraintViolation<T>> violations = beanValidator.validate(bean, groups);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(
                    String.format(VALIDATION_FAILED_MSG_TEMPLATE, bean.getClass().getSimpleName(), violations.size()),
                    violations
            );
        }
    }
}
