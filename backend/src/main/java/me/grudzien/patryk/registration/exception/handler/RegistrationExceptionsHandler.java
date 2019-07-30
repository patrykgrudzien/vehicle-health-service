package me.grudzien.patryk.registration.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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

    @ExceptionHandler(EmailVerificationTokenNotFoundException.class)
    public ResponseEntity<ExceptionResponse> emailVerificationTokenNotFoundException(final EmailVerificationTokenNotFoundException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), BAD_REQUEST);
    }

    @ExceptionHandler(EmailVerificationTokenExpiredException.class)
    public ResponseEntity<ExceptionResponse> emailVerificationTokenExpiredException(final EmailVerificationTokenExpiredException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> userAlreadyExistsException(final UserAlreadyExistsException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), BAD_REQUEST);
    }
}
