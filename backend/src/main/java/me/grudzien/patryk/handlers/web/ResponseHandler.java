package me.grudzien.patryk.handlers.web;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import me.grudzien.patryk.exceptions.exception.RedirectionException;
import me.grudzien.patryk.utils.HerokuAppEndpointResolver;
import me.grudzien.patryk.utils.LogMarkers;

@Log4j2
@Component
public class ResponseHandler {

	private final HerokuAppEndpointResolver herokuAppEndpointResolver;

	@Autowired
	public ResponseHandler(final HerokuAppEndpointResolver herokuAppEndpointResolver) {
		this.herokuAppEndpointResolver = herokuAppEndpointResolver;
	}

	public void redirectUserToConfirmedPage(final HttpServletResponse response, final String redirectionEndpoint) {
		try {
			response.sendRedirect(herokuAppEndpointResolver.determineBaseAppUrlForConfirmRegistration() + redirectionEndpoint);
			log.info("User successfully redirected to confirm registration screen.");
		} catch (final IOException exception) {
			log.error(LogMarkers.EXCEPTION_MARKER, "Cannot redirect user to confirm registration screen.");
			throw new RedirectionException("Cannot redirect user to confirm registration screen.");
		}
	}

	public void redirectUserToLoginPage(final HttpServletResponse response, final String redirectionEndpoint) {
		try {
			response.sendRedirect(herokuAppEndpointResolver.determineBaseAppUrlForLoginPage() + redirectionEndpoint);
			log.info("User successfully redirected to login page.");
		} catch (final IOException exception) {
			log.error(LogMarkers.EXCEPTION_MARKER, "Cannot redirect user to login page.");
			throw new RedirectionException("Cannot redirect user to login page.");
		}
	}
}
