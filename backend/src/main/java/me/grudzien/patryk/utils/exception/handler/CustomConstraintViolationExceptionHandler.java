package me.grudzien.patryk.utils.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import me.grudzien.patryk.registration.exception.CustomUserValidationException;
import me.grudzien.patryk.utils.exception.CustomConstraintViolationException;
import me.grudzien.patryk.utils.web.model.ExceptionResponse;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import static me.grudzien.patryk.utils.web.model.ExceptionResponse.buildBodyMessage;

/**
 * The {@link ControllerAdvice} annotation is a component annotation allowing
 * implementation classes to be auto-detected through classpath scanning.
 *
 * The {@link ControllerAdvice} listens across the whole application for exceptions.
 * When throws an exception, it'll catch and convert it to the meaningful message.
 */
@ControllerAdvice
public class CustomConstraintViolationExceptionHandler {

    @ExceptionHandler(CustomConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> test(final CustomConstraintViolationException e) {
        return new ResponseEntity<>(buildBodyMessage(e, e.getValidationErrors(), e.getMessageCode()), BAD_REQUEST);
    }

    @ExceptionHandler(CustomUserValidationException.class)
    public ResponseEntity<ExceptionResponse> customUserFieldsValidationException(final CustomUserValidationException exception) {
        final ExceptionResponse response = buildBodyMessage(exception);
        response.setErrors(exception.getValidationErrors());
        return new ResponseEntity<>(response, BAD_REQUEST);
    }
}
