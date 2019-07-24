package me.grudzien.patryk.utils.exception;

import lombok.Getter;

import org.springframework.lang.NonNull;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

@Getter
public class CustomConstraintViolationException extends ConstraintViolationException {

    public final List<String> validationErrors;

    public CustomConstraintViolationException(final String message,
                                              final Set<? extends ConstraintViolation<?>> constraintViolations,
                                              final LocaleMessagesCreator localeMessagesCreator) {
        super(message, constraintViolations);
        this.validationErrors = translateViolationsToMessages(constraintViolations, localeMessagesCreator);
    }

    private static List<String> translateViolationsToMessages(@NonNull final Set<? extends ConstraintViolation<?>> constraintViolations,
                                                              @NonNull final LocaleMessagesCreator localeMessagesCreator) {
        return constraintViolations.stream()
                        .map(ConstraintViolation::getMessage)
                        .map(localeMessagesCreator::buildLocaleMessage)
                        .collect(Collectors.toList());
    }
}
