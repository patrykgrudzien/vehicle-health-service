package me.grudzien.patryk.handlers.web;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
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
	private final CustomApplicationProperties customApplicationProperties;

	@Autowired
	public HttpResponseHandler(final HerokuAppEndpointResolver herokuAppEndpointResolver,
	                           final CustomApplicationProperties customApplicationProperties) {
		this.herokuAppEndpointResolver = herokuAppEndpointResolver;
		this.customApplicationProperties = customApplicationProperties;
	}

	public void redirectUserToConfirmedUrl(final HttpServletResponse response) {
		try {
			response.sendRedirect(herokuAppEndpointResolver.determineBaseAppUrlForConfirmRegistration() +
			                      customApplicationProperties.getEndpoints().getRegistration().getConfirmed());
			log.info("User successfully redirected to registration confirmed url.");
		} catch (final IOException exception) {
			log.error(LogMarkers.EXCEPTION_MARKER, "Cannot redirect user to registration confirmed url.");
			throw new RedirectionException("Cannot redirect user to registration confirmed url.");
		}
	}

	public void redirectUserToEmailTokenNotFoundUrl(final HttpServletResponse response) {
		try {
			response.sendRedirect(herokuAppEndpointResolver.determineBaseAppUrlForConfirmRegistration() +
			                      customApplicationProperties.getEndpoints().getRegistration().getConfirmedTokenNotFound());
			log.info("User successfully redirected to registration confirmed token not found url.");
		} catch (final IOException exception) {
			log.error(LogMarkers.EXCEPTION_MARKER, "Cannot redirect user to registration confirmed token not found url.");
			throw new RedirectionException("Cannot redirect user to registration confirmed token not found url.");
		}
	}
}
