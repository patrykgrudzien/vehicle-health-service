package me.grudzien.patryk.handlers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import me.grudzien.patryk.exceptions.exception.CustomUserValidationException;
import me.grudzien.patryk.exceptions.exception.TokenExpiredException;
import me.grudzien.patryk.exceptions.exception.TokenNotFoundException;
import me.grudzien.patryk.exceptions.exception.UserAlreadyExistsException;
import me.grudzien.patryk.exceptions.pojo.ExceptionResponse;

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
		final ExceptionResponse response = ExceptionResponse.Builder()
		                                                    .errorMessage(exception.getMessage())
		                                                    .build();
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ExceptionResponse> userAlreadyExists(final UserAlreadyExistsException exception) {
		final ExceptionResponse response = ExceptionResponse.Builder()
		                                                    .errorMessage(exception.getMessage())
		                                                    .build();
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(CustomUserValidationException.class)
	public ResponseEntity<ExceptionResponse> customUserFieldsValidationException(final CustomUserValidationException exception) {
		final ExceptionResponse response = ExceptionResponse.Builder()
		                                                    .errorMessage(exception.getMessage())
		                                                    .errors(exception.getValidationErrors())
		                                                    .build();
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(TokenNotFoundException.class)
	public ResponseEntity<ExceptionResponse> tokenNotFoundException(final TokenNotFoundException exception) {
		final ExceptionResponse response = ExceptionResponse.Builder()
		                                                    .errorMessage(exception.getMessage())
		                                                    .build();
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(TokenExpiredException.class)
	public ResponseEntity<ExceptionResponse> tokenExpiredException(final TokenExpiredException exception) {
		final ExceptionResponse response = ExceptionResponse.Builder()
		                                                    .errorMessage(exception.getMessage())
		                                                    .build();
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
}
