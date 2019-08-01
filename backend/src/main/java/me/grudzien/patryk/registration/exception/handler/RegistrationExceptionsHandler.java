package me.grudzien.patryk.registration.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Optional;

import me.grudzien.patryk.registration.exception.EmailVerificationTokenExpiredException;
import me.grudzien.patryk.registration.exception.EmailVerificationTokenNotFoundException;
import me.grudzien.patryk.registration.exception.UserAlreadyExistsException;
import me.grudzien.patryk.utils.web.model.ExceptionResponse;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * The {@link ControllerAdvice} annotation is a component annotation allowing
 * implementation classes to be auto-detected through classpath scanning.
 *
 * The {@link ControllerAdvice} listens across the whole application for exceptions.
 * When throws an exception, it'll catch and convert it to the meaningful message.
 */
@ControllerAdvice
public class RegistrationExceptionsHandler {

    // TODO: consolidate those 3 methods into 1
    @ExceptionHandler(EmailVerificationTokenNotFoundException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> emailVerificationTokenNotFoundException(final EmailVerificationTokenNotFoundException exception) {
        return ResponseEntity.of(Optional.of(ExceptionResponse.buildBodyMessage(exception)));
    }

    @ExceptionHandler(EmailVerificationTokenExpiredException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> emailVerificationTokenExpiredException(final EmailVerificationTokenExpiredException exception) {
        return ResponseEntity.of(Optional.of(ExceptionResponse.buildBodyMessage(exception)));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> userAlreadyExistsException(final UserAlreadyExistsException exception) {
        return ResponseEntity.of(Optional.of(ExceptionResponse.buildBodyMessage(exception)));
    }
}
