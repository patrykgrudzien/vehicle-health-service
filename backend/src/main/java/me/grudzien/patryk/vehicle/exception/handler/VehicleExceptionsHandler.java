package me.grudzien.patryk.vehicle.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import me.grudzien.patryk.utils.web.model.ExceptionResponse;
import me.grudzien.patryk.vehicle.exception.VehicleNotFoundException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * The {@link ControllerAdvice} annotation is a component annotation allowing
 * implementation classes to be auto-detected through classpath scanning.
 *
 * The {@link ControllerAdvice} listens across the whole application for exceptions.
 * When throws an exception, it'll catch and convert it to the meaningful message.
 */
@ControllerAdvice
public class VehicleExceptionsHandler {

    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<ExceptionResponse> vehicleNotFoundException(final VehicleNotFoundException exception) {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(exception), BAD_REQUEST);
    }
}
