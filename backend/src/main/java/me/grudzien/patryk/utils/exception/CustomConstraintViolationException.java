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

    private static final long serialVersionUID = -345792305372280930L;

    private final List<String> validationErrors;
    private final String messageCode;

    private CustomConstraintViolationException(final Builder builder) {
        this(builder.validationMessage, builder.constraintViolations, builder.messageCode, builder.localeMessagesCreator);
    }

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
                        .map(localeMessagesCreator::buildLocaleMessage)
                        .collect(Collectors.toList());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String validationMessage;
        private Set<? extends ConstraintViolation<?>> constraintViolations;
        private String messageCode;
        private LocaleMessagesCreator localeMessagesCreator;

        public Builder validationMessage(final String validationMessage) {
            this.validationMessage = validationMessage;
            return this;
        }

        public Builder constraintViolations(final Set<? extends ConstraintViolation<?>> constraintViolations) {
            this.constraintViolations = constraintViolations;
            return this;
        }

        public Builder messageCode(final String messageCode) {
            this.messageCode = messageCode;
            return this;
        }

        public Builder localeMessagesCreator(final LocaleMessagesCreator localeMessagesCreator) {
            this.localeMessagesCreator = localeMessagesCreator;
            return this;
        }

        public CustomConstraintViolationException build() {
            return new CustomConstraintViolationException(this);
        }
    }
}
