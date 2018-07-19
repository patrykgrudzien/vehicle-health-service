package me.grudzien.patryk.utils.web;

import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import java.io.IOException;

import static me.grudzien.patryk.utils.log.LogMarkers.EXCEPTION_MARKER;

import me.grudzien.patryk.domain.dto.responses.CustomResponse;

@Log4j2
public abstract class HttpResponseCustomizer {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static <T extends CustomResponse> void customizeHttpResponse(@NotNull final HttpServletResponse response, final HttpStatus status,
	                                                                    final T bodyMessage) {
		try {
			// response status
			response.setStatus(status.value());
			// notify client of response body content type
			response.addHeader(CustomResponse.CONTENT_TYPE_KEY, CustomResponse.CONTENT_TYPE_VALUE);
			// write the custom response body
			objectMapper.writeValue(response.getOutputStream(), bodyMessage);
			// commit the response
			response.flushBuffer();
		} catch (final IOException exception) {
			log.error(EXCEPTION_MARKER, "Exception occurred in ({}) on getting output stream from response.", HttpResponseCustomizer.class.getSimpleName());
			throw new RuntimeException(exception.getMessage());
		}
	}
}
