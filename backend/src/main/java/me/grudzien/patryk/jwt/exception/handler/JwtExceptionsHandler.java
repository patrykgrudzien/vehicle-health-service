package me.grudzien.patryk.jwt.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import me.grudzien.patryk.jwt.exception.NoEmailProvidedException;
import me.grudzien.patryk.jwt.exception.NoRefreshTokenProvidedException;
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
public class JwtExceptionsHandler {

    @ExceptionHandler(NoRefreshTokenProvidedException.class)
    public ResponseEntity<ExceptionResponse> noRefreshTokenProvidedException(final NoRefreshTokenProvidedException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), BAD_REQUEST);
    }

    @ExceptionHandler(NoEmailProvidedException.class)
    public ResponseEntity<ExceptionResponse> noEmailProvidedException(final NoEmailProvidedException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), BAD_REQUEST);
    }
}
