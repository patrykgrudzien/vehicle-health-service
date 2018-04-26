package me.grudzien.patryk.handlers.security;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import me.grudzien.patryk.domain.dto.login.AuthenticationEntryPointResponse;
import me.grudzien.patryk.handlers.web.HttpResponseHandler;
import me.grudzien.patryk.utils.log.LogMarkers;

@Log4j2
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	@Autowired
	public CustomAuthenticationEntryPoint(final ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void commence(final HttpServletRequest request, final HttpServletResponse response,
	                     final AuthenticationException authException) throws IOException, ServletException {

		log.info(LogMarkers.FLOW_MARKER, "User wanted to get secured resource but needs to be authenticated.");

		// notify client of response body content type
		response.addHeader(HttpResponseHandler.CONTENT_TYPE_KEY, HttpResponseHandler.CONTENT_TYPE_VALUE);
		// set the response status code
		// goal is to redirect user to login page (no error codes) in case of secured resource (redirection done on Vue.js)
		response.setStatus(HttpServletResponse.SC_OK);
		// set up the custom response body
		final AuthenticationEntryPointResponse customResponseBody = new AuthenticationEntryPointResponse(
				HttpResponseHandler.SECURED_RESOURCE_CODE,
				"You are NOT allowed to check secured resource!");
		// write the custom response body
		objectMapper.writeValue(response.getOutputStream(), customResponseBody);
		// commit the response
		response.flushBuffer();
	}
}
