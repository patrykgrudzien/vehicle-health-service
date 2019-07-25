package me.grudzien.patryk.utils.validation;

import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.Set;

import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.web.ObjectDecoder;

@Service
public class ValidationService {

    private static final String VALIDATION_FAILED_MSG_TEMPLATE = "Bean of type '%s' failed validation, number of violations=%d";

    private final Validator beanValidator;
    private final LocaleMessagesCreator localeMessagesCreator;

    public ValidationService(final Validator beanValidator, final LocaleMessagesCreator localeMessagesCreator) {
        this.beanValidator = beanValidator;
        this.localeMessagesCreator = localeMessagesCreator;
    }

    public <T, Mapper> ValidationResult validate(final T bean,
                                                 final ObjectDecoder<T, Mapper> objectDecoder,
                                                 final Mapper mapper,
                                                 final String messageCode) {
        return this.validateObject(objectDecoder, bean, mapper, messageCode, ValidationSequence.class);
    }

    /*public <T, Mapper> void validate(final T bean,
                                     final ObjectDecoder<T, Mapper> objectDecoder,
                                     final Mapper mapper,
                                     final String messageCode,
                                     final Class... groups) {
        this.validateObject(objectDecoder, bean, mapper, messageCode, ArrayUtils.add(groups, ValidationSequence.class));
    }*/

    private <T, Mapper> ValidationResult validateObject(final ObjectDecoder<T, Mapper> objectDecoder,
                                                        final T bean,
                                                        final Mapper mapper, final String messageCode,
                                                        final Class... groups) {
        final Set<ConstraintViolation<T>> violations = beanValidator.validate(objectDecoder.apply(bean, mapper), groups);
//        if (!violations.isEmpty()) {
//            final String message = String.format(VALIDATION_FAILED_MSG_TEMPLATE, bean.getClass().getSimpleName(), violations.size());
//            throw CustomConstraintViolationException.builder()
//                                                    .validationMessage(message)
//                                                    .constraintViolations(violations)
//                                                    .messageCode(messageCode)
//                                                    .localeMessagesCreator(localeMessagesCreator)
//                                                    .build();
//        }
        return violations.isEmpty() ? ValidationResult.ok() : ValidationResult.errorWith(violations);
    }
}
