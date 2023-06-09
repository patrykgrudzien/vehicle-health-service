package me.grudzien.patryk.utils.web;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import java.util.stream.Stream;

import me.grudzien.patryk.utils.web.model.CustomResponse;

import static me.grudzien.patryk.utils.appplication.SpringAppProfiles.DEV_HOME;
import static me.grudzien.patryk.utils.appplication.SpringAppProfiles.H2_IN_MEMORY;

@Slf4j
@Component
public class HttpResponseCustomizer {

	private static Environment environment;
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	public HttpResponseCustomizer(final Environment environment) {
		HttpResponseCustomizer.environment = environment;
	}

	public static <T extends CustomResponse> void customizeHttpResponse(@NotNull final HttpServletResponse response, final HttpStatus status,
	                                                                    final T bodyMessage) {
		// response status
		response.setStatus(status.value());
		// notify client of response body content type
		response.addHeader(CustomResponse.Headers.CONTENT_TYPE.getKey(), CustomResponse.Headers.CONTENT_TYPE.getValue());
		// CORS - if the header below is not specified during development, JSON created by this method cannot be displayed in the Client
		Stream.of(environment.getActiveProfiles()).forEach(activeProfile -> {
			if (DEV_HOME.getYmlName().equals(activeProfile) || H2_IN_MEMORY.getYmlName().equals(activeProfile)) {

				// TODO: check if it's still required because there was CORS error in browser's console while debugging
				response.addHeader(CustomResponse.Headers.ACCESS_CONTROL_ALLOW_ORIGIN.getKey(), CustomResponse.Headers.ACCESS_CONTROL_ALLOW_ORIGIN.getValue());
			}
		});

		Try.run(() -> objectMapper.writeValue(response.getOutputStream(), bodyMessage))
		   .onSuccess(voidResult -> Try.run(response::flushBuffer)
		                               .onSuccess(successVoid -> log.info("Response successfully flushed."))
		                               .onFailure(throwable -> log.error("Error ({}) occurred during flushing the response!", throwable.getMessage())))
		   .onFailure(throwable -> {
			   log.error("Exception occurred in ({}) on getting output stream from response.", HttpResponseCustomizer.class.getSimpleName());
			   throw new RuntimeException(throwable.getMessage());
		   });
	}
}
