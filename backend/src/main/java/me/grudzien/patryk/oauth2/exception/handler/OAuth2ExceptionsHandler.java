package me.grudzien.patryk.oauth2.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import me.grudzien.patryk.jwt.exception.CustomAuthenticationUnknownException;
import me.grudzien.patryk.jwt.exception.MissingAuthenticationResultException;
import me.grudzien.patryk.oauth2.authentication.CustomAuthenticationProvider;
import me.grudzien.patryk.oauth2.authentication.FailedAuthenticationCases;
import me.grudzien.patryk.oauth2.exception.JwtTokenNotFoundException;
import me.grudzien.patryk.oauth2.exception.RegistrationProviderMismatchException;
import me.grudzien.patryk.oauth2.exception.UnknownOAuth2FlowException;
import me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser.AccountStatus;
import me.grudzien.patryk.utils.web.model.ExceptionResponse;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * The {@link ControllerAdvice} annotation is a component annotation allowing
 * implementation classes to be auto-detected through classpath scanning.
 *
 * The {@link ControllerAdvice} listens across the whole application for exceptions.
 * When throws an exception, it'll catch and convert it to the meaningful message.
 */
@ControllerAdvice
public class OAuth2ExceptionsHandler {

    @ExceptionHandler(AccountExpiredException.class)
    public ResponseEntity<ExceptionResponse> userAccountIsExpired(final AccountExpiredException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception, AccountStatus.USER_ACCOUNT_IS_EXPIRED), BAD_REQUEST);
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<ExceptionResponse> credentialsHaveExpired(final CredentialsExpiredException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception, AccountStatus.CREDENTIALS_HAVE_EXPIRED), BAD_REQUEST);
    }

    @ExceptionHandler(RegistrationProviderMismatchException.class)
    public ResponseEntity<ExceptionResponse> registrationProviderMismatch(final RegistrationProviderMismatchException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception, AccountStatus.REGISTRATION_PROVIDER_MISMATCH), BAD_REQUEST);
    }

    @ExceptionHandler(UnknownOAuth2FlowException.class)
    public ResponseEntity<ExceptionResponse> unknownDelegateException(final UnknownOAuth2FlowException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), INTERNAL_SERVER_ERROR);
    }

    /**
     * Handler(s) for exception(s) thrown by {@link CustomAuthenticationProvider} which is/are
     * later caught by {@link FailedAuthenticationCases} and translated here.
     */
    @ExceptionHandler(MissingAuthenticationResultException.class)
    public ResponseEntity<ExceptionResponse> missingAuthenticationResultException(final MissingAuthenticationResultException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CustomAuthenticationUnknownException.class)
    public ResponseEntity<ExceptionResponse> customAuthenticationUnknownException(final CustomAuthenticationUnknownException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(JwtTokenNotFoundException.class)
    public ResponseEntity<ExceptionResponse> jwtTokenNotFound(final JwtTokenNotFoundException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception, AccountStatus.JWT_TOKEN_NOT_FOUND), BAD_REQUEST);
    }
}
