package me.grudzien.patryk.handlers.security;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import me.grudzien.patryk.domain.dto.login.AuthenticationEntryPointResponse;
import me.grudzien.patryk.handlers.web.HttpResponseHandler;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.log.LogMarkers;

/**
 * IMPORTANT NOTE:
 * {@link me.grudzien.patryk.handlers.exception.ExceptionHandlingController} only handles exceptions come from @Controller classes that's
 * why all exceptions which come from JWT will be omitted (they are specific to Servlet itself) !!!
 */
@Log4j2
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;
	private final LocaleMessagesCreator localeMessagesCreator;

	@Autowired
	public CustomAuthenticationEntryPoint(final ObjectMapper objectMapper, final LocaleMessagesCreator localeMessagesCreator) {
		this.objectMapper = objectMapper;
		this.localeMessagesCreator = localeMessagesCreator;
	}

	@Override
	public void commence(final HttpServletRequest request, final HttpServletResponse response,
	                     final AuthenticationException authException) throws IOException {

		log.info(LogMarkers.FLOW_MARKER, "User wanted to get secured resource but needs to be authenticated.");

		// notify client of response body content type
		response.addHeader(HttpResponseHandler.CONTENT_TYPE_KEY, HttpResponseHandler.CONTENT_TYPE_VALUE);
		// set the response status code
		// goal is to redirect user to login page (no error codes) in case of secured resource (redirection done on Vue.js)
		response.setStatus(HttpServletResponse.SC_OK);
		// set up the custom response body
		final AuthenticationEntryPointResponse customResponseBody = new AuthenticationEntryPointResponse(
				HttpResponseHandler.SECURED_RESOURCE_CODE,
				localeMessagesCreator.buildLocaleMessage("secured-resource-message", request));
		// write the custom response body
		objectMapper.writeValue(response.getOutputStream(), customResponseBody);
		// commit the response
		response.flushBuffer();
	}
}
