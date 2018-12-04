package me.grudzien.patryk.handler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import me.grudzien.patryk.domain.dto.responses.ExceptionResponse;
import me.grudzien.patryk.exception.RedirectionException;
import me.grudzien.patryk.exception.login.BadCredentialsAuthenticationException;
import me.grudzien.patryk.exception.login.UserDisabledAuthenticationException;
import me.grudzien.patryk.exception.registration.CustomUserValidationException;
import me.grudzien.patryk.exception.registration.EmailVerificationTokenExpiredException;
import me.grudzien.patryk.exception.registration.EmailVerificationTokenNotFoundException;
import me.grudzien.patryk.exception.registration.UserAlreadyExistsException;
import me.grudzien.patryk.exception.vehicle.VehicleNotFoundException;
import me.grudzien.patryk.oauth2.exception.JwtTokenNotFoundException;
import me.grudzien.patryk.oauth2.exception.RegistrationProviderMismatchException;
import me.grudzien.patryk.oauth2.exception.UnknownOAuth2FlowException;

import static me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser.AccountStatus;

/**
 * The @ControllerAdvice annotation is a component annotation allowing implementation classes to be auto-detected
 * through classpath scanning.
 *
 * The @ControllerAdvice listens across the whole application for exceptions. When throws an exception, it'll catch
 * and convert it to the meaningful message.
 */
@ControllerAdvice
public class ExceptionHandlingController {

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ExceptionResponse> userNotFoundForEmail(final UsernameNotFoundException exception) {
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception, AccountStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
	}

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> userAccountIsLocked(final LockedException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception, AccountStatus.USER_ACCOUNT_IS_LOCKED), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserDisabledAuthenticationException.class)
    public ResponseEntity<ExceptionResponse> userIsDisabled(final UserDisabledAuthenticationException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception, AccountStatus.USER_IS_DISABLED), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountExpiredException.class)
    public ResponseEntity<ExceptionResponse> userAccountIsExpired(final AccountExpiredException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception, AccountStatus.USER_ACCOUNT_IS_EXPIRED), HttpStatus.BAD_REQUEST);
    }

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ExceptionResponse> userAlreadyExists(final UserAlreadyExistsException exception) {
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception, AccountStatus.ALREADY_EXISTS), HttpStatus.BAD_REQUEST);
	}

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<ExceptionResponse> credentialsHaveExpired(final CredentialsExpiredException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception, AccountStatus.CREDENTIALS_HAVE_EXPIRED), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JwtTokenNotFoundException.class)
    public ResponseEntity<ExceptionResponse> jwtTokenNotFound(final JwtTokenNotFoundException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception, AccountStatus.JWT_TOKEN_NOT_FOUND), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RegistrationProviderMismatchException.class)
    public ResponseEntity<ExceptionResponse> registrationProviderMismatch(final RegistrationProviderMismatchException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception, AccountStatus.REGISTRATION_PROVIDER_MISMATCH), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsAuthenticationException.class)
    public ResponseEntity<ExceptionResponse> badCredentials(final BadCredentialsAuthenticationException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception, AccountStatus.BAD_CREDENTIALS), HttpStatus.BAD_REQUEST);
    }

	@ExceptionHandler(CustomUserValidationException.class)
	public ResponseEntity<ExceptionResponse> customUserFieldsValidationException(final CustomUserValidationException exception) {
		final ExceptionResponse response = ExceptionResponse.buildBodyMessage(exception);
		response.setErrors(exception.getValidationErrors());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EmailVerificationTokenNotFoundException.class)
	public ResponseEntity<ExceptionResponse> emailVerificationTokenNotFoundException(final EmailVerificationTokenNotFoundException exception) {
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EmailVerificationTokenExpiredException.class)
	public ResponseEntity<ExceptionResponse> emailVerificationTokenExpiredException(final EmailVerificationTokenExpiredException exception) {
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RedirectionException.class)
	public ResponseEntity<ExceptionResponse> cannotRedirectUser(final RedirectionException exception) {
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(VehicleNotFoundException.class)
	public ResponseEntity<ExceptionResponse> vehicleNotFoundException(final VehicleNotFoundException exception) {
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), HttpStatus.BAD_REQUEST);
	}

	/**
	 * This handler is used when
	 * {@link me.grudzien.patryk.util.web.security.RequestParamPathVariableGuard#isUserEmailAuthenticated(String)} returns false and
	 * Spring Security forbids to execute methods annotated with e.g.
	 * {@link @{@link org.springframework.security.access.prepost.PreAuthorize}}
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ExceptionResponse> accessDeniedException(final AccessDeniedException exception) {
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(UnknownOAuth2FlowException.class)
	public ResponseEntity<ExceptionResponse> unknownDelegateException(final UnknownOAuth2FlowException exception) {
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
