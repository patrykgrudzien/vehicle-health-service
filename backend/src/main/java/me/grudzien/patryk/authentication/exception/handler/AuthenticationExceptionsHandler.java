package me.grudzien.patryk.authentication.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser.AccountStatus;
import me.grudzien.patryk.utils.web.model.ExceptionResponse;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * The {@link ControllerAdvice} annotation is a component annotation allowing
 * implementation classes to be auto-detected through classpath scanning.
 *
 * The {@link ControllerAdvice} listens across the whole application for exceptions.
 * When throws an exception, it'll catch and convert it to the meaningful message.
 */
@ControllerAdvice
public class AuthenticationExceptionsHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> badCredentials(final BadCredentialsException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception, AccountStatus.BAD_CREDENTIALS), BAD_REQUEST);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> userIsDisabled(final DisabledException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception, AccountStatus.USER_IS_DISABLED), BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionResponse> userNotFoundForEmail(final UsernameNotFoundException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception, AccountStatus.NOT_FOUND), NOT_FOUND);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> userAccountIsLocked(final LockedException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception, AccountStatus.USER_ACCOUNT_IS_LOCKED), BAD_REQUEST);
    }
}
