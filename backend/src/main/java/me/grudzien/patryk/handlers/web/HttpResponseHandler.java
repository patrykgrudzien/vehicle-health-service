package me.grudzien.patryk.handlers.web;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import me.grudzien.patryk.exceptions.RedirectionException;
import me.grudzien.patryk.utils.HerokuAppEndpointResolver;
import me.grudzien.patryk.utils.log.LogMarkers;

@Log4j2
@Component
public class HttpResponseHandler {

	public static final String SECURED_RESOURCE_CODE = "SECURED-RESOURCE";
	public static final String CONTENT_TYPE_KEY = "Content-Type";
	public static final String CONTENT_TYPE_VALUE = "application/json;charset=UTF-8";

	private final HerokuAppEndpointResolver herokuAppEndpointResolver;

	@Autowired
	public HttpResponseHandler(final HerokuAppEndpointResolver herokuAppEndpointResolver) {
		this.herokuAppEndpointResolver = herokuAppEndpointResolver;
	}

	public void redirectUserToConfirmedPage(final HttpServletResponse response, final String redirectionEndpoint) {
		try {
			response.sendRedirect(herokuAppEndpointResolver.determineBaseAppUrlForConfirmRegistration() + redirectionEndpoint);
			log.debug("User successfully redirected to confirm registration screen.");
		} catch (final IOException exception) {
			log.error(LogMarkers.EXCEPTION_MARKER, "Cannot redirect user to confirm registration screen.");
			throw new RedirectionException("Cannot redirect user to confirm registration screen.");
		}
	}
}