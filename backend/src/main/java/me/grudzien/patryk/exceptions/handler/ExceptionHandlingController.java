package me.grudzien.patryk.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import me.grudzien.patryk.exceptions.exception.UserEmailNotFoundException;
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

	@ExceptionHandler(UserEmailNotFoundException.class)
	public ResponseEntity<ExceptionResponse> resourceNotFound(final UserEmailNotFoundException exception) {
		final ExceptionResponse response = ExceptionResponse.Builder()
		                                                    .errorCode("Not Found")
		                                                    .errorMessage(exception.getMessage())
		                                                    .build();
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
}
