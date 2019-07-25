package me.grudzien.patryk.utils.validation;

import lombok.Getter;

import org.springframework.lang.NonNull;

import javax.validation.ConstraintViolation;

import java.util.Set;

import static me.grudzien.patryk.utils.common.Predicates.isEmpty;

public final class ValidationResult<T> {

    @Getter
    private final Set<ConstraintViolation<T>> constraintViolations;

    private ValidationResult() {
        this(null);
    }

    private ValidationResult(final Set<ConstraintViolation<T>> constraintViolations) {
        this.constraintViolations = constraintViolations;
    }

    static ValidationResult ok() {
        return new ValidationResult();
    }

    static <T> ValidationResult errorWith(@NonNull final Set<ConstraintViolation<T>> constraintViolations) {
        return new ValidationResult<>(constraintViolations);
    }

    public final ValidationResult onErrors() {
        if (isEmpty(constraintViolations)) return ok();
        return null;
    }
}
