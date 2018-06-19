package me.grudzien.patryk.handlers.web;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.exceptions.RedirectionException;
import me.grudzien.patryk.utils.web.HerokuAppEndpointResolver;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.log.LogMarkers;

@Log4j2
@Component
public class HttpResponseHandler {

	public static final String SECURED_RESOURCE_CODE = "SECURED";
	public static final String CONTENT_TYPE_KEY = "Content-Type";
	public static final String CONTENT_TYPE_VALUE = "application/json;charset=UTF-8";

	private final HerokuAppEndpointResolver herokuAppEndpointResolver;
	private final CustomApplicationProperties customApplicationProperties;
	private final LocaleMessagesCreator localeMessagesCreator;

	@Autowired
	public HttpResponseHandler(final HerokuAppEndpointResolver herokuAppEndpointResolver,
	                           final CustomApplicationProperties customApplicationProperties,
	                           final LocaleMessagesCreator localeMessagesCreator) {
		this.herokuAppEndpointResolver = herokuAppEndpointResolver;
		this.customApplicationProperties = customApplicationProperties;
		this.localeMessagesCreator = localeMessagesCreator;
	}

	public void redirectUserToAccountAlreadyEnabledUrl(final HttpServletResponse response) {
		try {
			response.sendRedirect(herokuAppEndpointResolver.determineBaseAppUrlForUserAlreadyEnabled() +
			                      customApplicationProperties.getEndpoints().getRegistration().getUserAlreadyEnabled());
			log.info("User successfully redirected to (user already enabled) url.");
		} catch (final IOException exception) {
			log.error(LogMarkers.EXCEPTION_MARKER, "Cannot redirect user to (user already enabled) url.");
			throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-user-already-enabled-url"));
		}
	}

	public void redirectUserToEmailTokenExpiredUrl(final HttpServletResponse response) {
		try {
			response.sendRedirect(herokuAppEndpointResolver.determineBaseAppUrlForConfirmRegistration() +
			                      customApplicationProperties.getEndpoints().getRegistration().getConfirmedTokenExpired());
			log.info("User successfully redirected to registration confirmed token expired url.");
		} catch (final IOException exception) {
			log.error(LogMarkers.EXCEPTION_MARKER, "Cannot redirect user to registration confirmed token expired url.");
			throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-registration-confirmed-token-expired-url"));
		}
	}

	public void redirectUserToConfirmedUrl(final HttpServletResponse response) {
		try {
			response.sendRedirect(herokuAppEndpointResolver.determineBaseAppUrlForConfirmRegistration() +
			                      customApplicationProperties.getEndpoints().getRegistration().getConfirmed());
			log.info("User successfully redirected to registration confirmed url.");
		} catch (final IOException exception) {
			log.error(LogMarkers.EXCEPTION_MARKER, "Cannot redirect user to registration confirmed url.");
			throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-registration-confirmed-url"));
		}
	}

	public void redirectUserToEmailTokenNotFoundUrl(final HttpServletResponse response) {
		try {
			response.sendRedirect(herokuAppEndpointResolver.determineBaseAppUrlForConfirmRegistration() +
			                      customApplicationProperties.getEndpoints().getRegistration().getConfirmedTokenNotFound());
			log.info("User successfully redirected to registration confirmed token not found url.");
		} catch (final IOException exception) {
			log.error(LogMarkers.EXCEPTION_MARKER, "Cannot redirect user to registration confirmed token not found url.");
			throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-registration-confirmed-token-not-found-url"));
		}
	}
}
