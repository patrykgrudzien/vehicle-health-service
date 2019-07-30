package me.grudzien.patryk.utils.exception;

import static java.util.stream.Collectors.toList;

import lombok.Builder;
import lombok.Getter;

import org.springframework.lang.NonNull;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import java.util.List;
import java.util.Set;

import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

@Getter
public class CustomConstraintViolationException extends ConstraintViolationException {

    private static final long serialVersionUID = 2257797681054773142L;

    private final List<String> validationErrors;
    private final String messageCode;

    @Builder(builderClassName = "LombokBuilder")
    private CustomConstraintViolationException(final String validationMessage,
                                               final Set<? extends ConstraintViolation<?>> constraintViolations,
                                               final String messageCode,
                                               final LocaleMessagesCreator localeMessagesCreator) {
        super(validationMessage, constraintViolations);
        this.validationErrors = translateValidationErrors(constraintViolations, localeMessagesCreator);
        this.messageCode = messageCode;
    }

    private static List<String> translateValidationErrors(@NonNull final Set<? extends ConstraintViolation<?>> constraintViolations,
                                                          @NonNull final LocaleMessagesCreator localeMessagesCreator) {
        return constraintViolations.stream()
                        .map(ConstraintViolation::getMessage)
						.distinct()
                        .map(localeMessagesCreator::buildLocaleMessage)
                        .collect(toList());
    }
}
