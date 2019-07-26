package me.grudzien.patryk.utils.web.security.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import me.grudzien.patryk.utils.web.model.ExceptionResponse;
import me.grudzien.patryk.utils.web.security.RequestParamPathVariableGuard;

import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * The {@link ControllerAdvice} annotation is a component annotation allowing
 * implementation classes to be auto-detected through classpath scanning.
 *
 * The {@link ControllerAdvice} listens across the whole application for exceptions.
 * When throws an exception, it'll catch and convert it to the meaningful message.
 */
@ControllerAdvice
public class SecurityExceptionsHandler {

    /**
     * This handler is used when
     * {@link RequestParamPathVariableGuard#isUserEmailAuthenticated(String)} returns false and
     * Spring Security forbids to execute methods annotated with e.g.
     * {@link @{@link org.springframework.security.access.prepost.PreAuthorize}}
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> accessDeniedException(final AccessDeniedException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), FORBIDDEN);
    }
}
