package me.grudzien.patryk.utils.validation;

import org.springframework.stereotype.Service;

import org.apache.commons.lang3.ArrayUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.Set;

import me.grudzien.patryk.utils.exception.CustomConstraintViolationException;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.mapping.ObjectDecoder;

@Service
public class ValidationService {

    private static final String VALIDATION_FAILED_MSG_TEMPLATE = "Bean of type '%s' failed validation, number of violations=%d";

    private final Validator beanValidator;
    private final LocaleMessagesCreator localeMessagesCreator;

    public ValidationService(final Validator beanValidator, final LocaleMessagesCreator localeMessagesCreator) {
        this.beanValidator = beanValidator;
        this.localeMessagesCreator = localeMessagesCreator;
    }

    public <T, Mapper> void validate(final T bean,
                                     final ObjectDecoder<T, Mapper> objectDecoder,
                                     final Mapper mapper) {
        this.validateObject(objectDecoder, bean, mapper, ValidationSequence.class);
    }

    public <T, Mapper> void validate(final T bean,
                                     final ObjectDecoder<T, Mapper> objectDecoder,
                                     final Mapper mapper,
                                     final Class... groups) {
        this.validateObject(objectDecoder, bean, mapper, ArrayUtils.add(groups, ValidationSequence.class));
    }

    private <T, Mapper> void validateObject(final ObjectDecoder<T, Mapper> objectDecoder,
                                            final T bean,
                                            final Mapper mapper,
                                            final Class... groups) {
        final Set<ConstraintViolation<T>> violations = beanValidator.validate(objectDecoder.apply(bean, mapper), groups);
        if (!violations.isEmpty()) {
            throw new CustomConstraintViolationException(
                    String.format(VALIDATION_FAILED_MSG_TEMPLATE, bean.getClass().getSimpleName(), violations.size()),
                    violations,
                    localeMessagesCreator
            );
        }
    }
}
