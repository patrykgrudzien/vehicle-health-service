package me.grudzien.patryk.utils.validation;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.stereotype.Service;

import org.apache.commons.lang3.ArrayUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.Set;
import java.util.function.Function;

import me.grudzien.patryk.utils.exception.CustomConstraintViolationException;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.web.ObjectDecoder;

@Service
public class ValidationService {

    private static final String VALIDATION_FAILED_MSG_TEMPLATE = "Bean of type '%s' failed validation!";
    private static final Function<String, String> VALIDATION_FAILED_MSG = beanType -> format(VALIDATION_FAILED_MSG_TEMPLATE, beanType);

    private final Validator beanValidator;
    private final LocaleMessagesCreator localeMessagesCreator;

    public ValidationService(final Validator beanValidator, final LocaleMessagesCreator localeMessagesCreator) {
        this.beanValidator = beanValidator;
        this.localeMessagesCreator = localeMessagesCreator;
    }

    public <T> void validate(final T bean) {
        this.validateObject(bean, ValidationSequence.class);
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

    public <T> ValidationResult validateWithResult(final T bean) {
        return this.validateObjectWithResult(bean, ValidationSequence.class);
    }

    public <T, Mapper> ValidationResult validateWithResult(final T bean,
                                                           final ObjectDecoder<T, Mapper> objectDecoder,
                                                           final Mapper mapper) {
        return this.validateObjectWithResult(objectDecoder, bean, mapper, ValidationSequence.class);
    }

    public <T, Mapper> ValidationResult validateWithResult(final T bean,
                                                           final ObjectDecoder<T, Mapper> objectDecoder,
                                                           final Mapper mapper,
                                                           final Class... groups) {
        return this.validateObjectWithResult(objectDecoder, bean, mapper, ArrayUtils.add(groups, ValidationSequence.class));
    }

    private <T, Mapper> void validateObject(final ObjectDecoder<T, Mapper> objectDecoder,
                                            final T bean,
                                            final Mapper mapper,
                                            final Class... groups) {
        final Set<? extends ConstraintViolation<?>> violations = beanValidator.validate(objectDecoder.apply(bean, mapper), groups);
        final String message = VALIDATION_FAILED_MSG.apply(bean.getClass().getSimpleName());
        if (!violations.isEmpty())
            validationResult().throwViolationException(message, violations, null);
    }

    private <T> void validateObject(final T bean, final Class... groups) {
        final Set<? extends ConstraintViolation<?>> violations = beanValidator.validate(bean, groups);
        final String message = VALIDATION_FAILED_MSG.apply(bean.getClass().getSimpleName());
        if (!violations.isEmpty())
            validationResult().throwViolationException(message, violations, null);
    }

    private <T, Mapper> ValidationResult validateObjectWithResult(final ObjectDecoder<T, Mapper> objectDecoder,
                                                                  final T bean,
                                                                  final Mapper mapper,
                                                                  final Class... groups) {
        final Set<? extends ConstraintViolation<?>> violations = beanValidator.validate(objectDecoder.apply(bean, mapper), groups);
        final String message = VALIDATION_FAILED_MSG.apply(bean.getClass().getSimpleName());
        return violations.isEmpty() ?
                validationResult() : validationResult().failedWith(message, violations);
    }

    private <T> ValidationResult validateObjectWithResult(final T bean, final Class... groups) {
        final Set<? extends ConstraintViolation<?>> violations = beanValidator.validate(bean, groups);
        final String message = VALIDATION_FAILED_MSG.apply(bean.getClass().getSimpleName());
        return violations.isEmpty() ?
                validationResult() : validationResult().failedWith(message, violations);
    }

    private ValidationResult validationResult() {
        return new ValidationResult();
    }

    @NoArgsConstructor(access = PRIVATE)
    @Setter(PRIVATE)
    public class ValidationResult {

        private String message;
        private Set<? extends ConstraintViolation<?>> violations;

        public final void onErrorsSetExceptionMessageCode(final String messageCode) {
            if (nonNull(violations) && !violations.isEmpty())
                throwViolationException(message, violations, messageCode);
        }

        private void throwViolationException(final String message, final Set<? extends ConstraintViolation<?>> violations,
                                             final String messageCode) {
            throw CustomConstraintViolationException.builder()
                                .validationMessage(message)
                                .constraintViolations(violations)
                                .messageCode(messageCode)
                                .localeMessagesCreator(localeMessagesCreator)
                                .build();
        }

        private ValidationResult failedWith(final String message, final Set<? extends ConstraintViolation<?>> violations) {
            setMessage(message);
            setViolations(violations);
            return this;
        }
    }
}
