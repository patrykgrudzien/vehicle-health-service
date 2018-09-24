package me.grudzien.patryk.handlers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import me.grudzien.patryk.domain.dto.responses.ExceptionResponse;
import me.grudzien.patryk.exceptions.RedirectionException;
import me.grudzien.patryk.exceptions.login.BadCredentialsAuthenticationException;
import me.grudzien.patryk.exceptions.login.UserDisabledAuthenticationException;
import me.grudzien.patryk.exceptions.registration.CustomUserValidationException;
import me.grudzien.patryk.exceptions.registration.TokenExpiredException;
import me.grudzien.patryk.exceptions.registration.TokenNotFoundException;
import me.grudzien.patryk.exceptions.registration.UserAlreadyExistsException;
import me.grudzien.patryk.exceptions.vehicle.VehicleNotFoundException;
import me.grudzien.patryk.oauth2.exceptions.UnknownDelegateException;

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
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ExceptionResponse> userAlreadyExists(final UserAlreadyExistsException exception) {
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(CustomUserValidationException.class)
	public ResponseEntity<ExceptionResponse> customUserFieldsValidationException(final CustomUserValidationException exception) {
		final ExceptionResponse response = ExceptionResponse.buildBodyMessage(exception);
		response.setErrors(exception.getValidationErrors());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(TokenNotFoundException.class)
	public ResponseEntity<ExceptionResponse> tokenNotFoundException(final TokenNotFoundException exception) {
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(TokenExpiredException.class)
	public ResponseEntity<ExceptionResponse> tokenExpiredException(final TokenExpiredException exception) {
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RedirectionException.class)
	public ResponseEntity<ExceptionResponse> cannotRedirectUser(final RedirectionException exception) {
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UserDisabledAuthenticationException.class)
	public ResponseEntity<ExceptionResponse> userIsDisabled(final UserDisabledAuthenticationException exception) {
		// returning 200 OK status just to catch error message and display it on UI
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), HttpStatus.OK);
	}

	@ExceptionHandler(BadCredentialsAuthenticationException.class)
	public ResponseEntity<ExceptionResponse> badCredentials(final BadCredentialsAuthenticationException exception) {
		// returning 200 OK status just to catch error message and display it on UI
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), HttpStatus.OK);
	}

	@ExceptionHandler(VehicleNotFoundException.class)
	public ResponseEntity<ExceptionResponse> vehicleNotFoundException(final VehicleNotFoundException exception) {
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), HttpStatus.BAD_REQUEST);
	}

	/**
	 * This handler is used when
	 * {@link me.grudzien.patryk.utils.web.security.RequestParamPathVariableGuard#isUserEmailAuthenticated(String)} returns false and
	 * Spring Security forbids to execute methods annotated with e.g.
	 * {@link @{@link org.springframework.security.access.prepost.PreAuthorize}}
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ExceptionResponse> accessDeniedException(final AccessDeniedException exception) {
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(UnknownDelegateException.class)
	public ResponseEntity<ExceptionResponse> unknownDelegateException(final UnknownDelegateException exception) {
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), HttpStatus.BAD_REQUEST);
	}
}
