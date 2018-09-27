package me.grudzien.patryk.oauth2.utils.rest;

import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import me.grudzien.patryk.domain.dto.responses.ExceptionResponse;

@Log4j2
public class CustomRestTemplateResponseErrorHandler extends DefaultResponseErrorHandler {

	@Override
	public void handleError(final ClientHttpResponse response) throws IOException {
		final ExceptionResponse exceptionResponse = new ObjectMapper().readValue(response.getBody(), ExceptionResponse.class);
		log.error(exceptionResponse.getMessage());
	}

	@Override
	protected boolean hasError(final HttpStatus statusCode) {
		return statusCode.is4xxClientError();
	}
}
