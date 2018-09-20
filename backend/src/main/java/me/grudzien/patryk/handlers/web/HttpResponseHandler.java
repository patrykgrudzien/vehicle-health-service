package me.grudzien.patryk.handlers.web;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.domain.enums.AppFLow;
import me.grudzien.patryk.exceptions.RedirectionException;
import me.grudzien.patryk.oauth2.handlers.CustomOAuth2AuthenticationSuccessHandler;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.web.ContextPathsResolver;

import static me.grudzien.patryk.domain.enums.AppFLow.ACCOUNT_ALREADY_ENABLED;
import static me.grudzien.patryk.domain.enums.AppFLow.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.domain.enums.AppFLow.GOOGLE_REDIRECTION_SUCCESSFUL;
import static me.grudzien.patryk.domain.enums.AppFLow.VERIFICATION_TOKEN_EXPIRED;
import static me.grudzien.patryk.domain.enums.AppFLow.VERIFICATION_TOKEN_NOT_FOUND;
import static me.grudzien.patryk.utils.log.LogMarkers.EXCEPTION_MARKER;
import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;

@Log4j2
@Component
public class HttpResponseHandler {

	private final ContextPathsResolver contextPathsResolver;
	private final CustomApplicationProperties customApplicationProperties;
	private final LocaleMessagesCreator localeMessagesCreator;

	private final PropertiesKeeper propertiesKeeper;

	@Autowired
	public HttpResponseHandler(final ContextPathsResolver contextPathsResolver, final CustomApplicationProperties customApplicationProperties,
	                           final LocaleMessagesCreator localeMessagesCreator, final PropertiesKeeper propertiesKeeper) {
		Preconditions.checkNotNull(contextPathsResolver, "contextPathsResolver cannot be null!");
		Preconditions.checkNotNull(customApplicationProperties, "customApplicationProperties cannot be null!");
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");

		this.contextPathsResolver = contextPathsResolver;
		this.customApplicationProperties = customApplicationProperties;
		this.localeMessagesCreator = localeMessagesCreator;

		Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");
		this.propertiesKeeper = propertiesKeeper;
	}

	public void redirectUserTo(final AppFLow appFLow, final HttpServletResponse response, final String... additionalParameters) {
		/**
		 * Creating base app URLs for:
		 * {@link me.grudzien.patryk.service.registration.impl.UserRegistrationServiceImpl#confirmRegistration(String, javax.servlet.http.HttpServletResponse)}
		 *
		 * @return Base app URls which are used to redirect user to specific screens on UI.
		 */
		switch (appFLow) {
			case ACCOUNT_ALREADY_ENABLED:
				final String userAlreadyEnabledUrl = customApplicationProperties.getEndpoints().getRegistration().getUserAlreadyEnabled();
				try {
					response.sendRedirect(contextPathsResolver.determineUrlFor(ACCOUNT_ALREADY_ENABLED) + userAlreadyEnabledUrl);
					log.info(FLOW_MARKER, ACCOUNT_ALREADY_ENABLED.getSuccessfulRedirectionLogInfoMessage(), userAlreadyEnabledUrl);
				} catch (final IOException exception) {
					log.error(EXCEPTION_MARKER, ACCOUNT_ALREADY_ENABLED.getRedirectionExceptionLogErrorMessage(), userAlreadyEnabledUrl);
					throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-user-already-enabled-url"));
				}
				break;
			case CONFIRM_REGISTRATION:
				final String registrationConfirmedUrl = customApplicationProperties.getEndpoints().getRegistration().getConfirmed();
				try {
					response.sendRedirect(contextPathsResolver.determineUrlFor(CONFIRM_REGISTRATION) + registrationConfirmedUrl);
					log.info(FLOW_MARKER, CONFIRM_REGISTRATION.getSuccessfulRedirectionLogInfoMessage(), registrationConfirmedUrl);
				} catch (final IOException exception) {
					log.error(EXCEPTION_MARKER, CONFIRM_REGISTRATION.getRedirectionExceptionLogErrorMessage(), registrationConfirmedUrl);
					throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-registration-confirmed-url"));
				}
				break;
			case VERIFICATION_TOKEN_NOT_FOUND:
				final String confirmedTokenNotFoundUrl = customApplicationProperties.getEndpoints().getRegistration().getConfirmedTokenNotFound();
				try {
					response.sendRedirect(contextPathsResolver.determineUrlFor(CONFIRM_REGISTRATION) + confirmedTokenNotFoundUrl);
					log.info(FLOW_MARKER, VERIFICATION_TOKEN_NOT_FOUND.getSuccessfulRedirectionLogInfoMessage(), confirmedTokenNotFoundUrl);
				} catch (final IOException exception) {
					log.error(EXCEPTION_MARKER, VERIFICATION_TOKEN_NOT_FOUND.getRedirectionExceptionLogErrorMessage(), confirmedTokenNotFoundUrl);
					throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-registration-confirmed-token-not-found-url"));
				}
				break;
			case VERIFICATION_TOKEN_EXPIRED:
				final String confirmedTokenExpired = customApplicationProperties.getEndpoints().getRegistration().getConfirmedTokenExpired();
				try {
					response.sendRedirect(contextPathsResolver.determineUrlFor(CONFIRM_REGISTRATION) + confirmedTokenExpired);
					log.info(FLOW_MARKER, VERIFICATION_TOKEN_EXPIRED.getSuccessfulRedirectionLogInfoMessage(), confirmedTokenExpired);
				} catch (final IOException exception) {
					log.error(EXCEPTION_MARKER, VERIFICATION_TOKEN_EXPIRED.getRedirectionExceptionLogErrorMessage(), confirmedTokenExpired);
					throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-registration-confirmed-token-expired-url"));
				}
				break;
			case GOOGLE_REDIRECTION_SUCCESSFUL:
				final String googleSuccessTargetUrl = propertiesKeeper.oAuth2().SUCCESS_TARGET_URL +
				                                      "?" +
				                                      CustomOAuth2AuthenticationSuccessHandler.SHORT_LIVED_AUTH_TOKEN_NAME +
				                                      "=" +
				                                      additionalParameters[0];
				try {
					response.sendRedirect("http://localhost:8080" + googleSuccessTargetUrl);
					log.info(FLOW_MARKER, GOOGLE_REDIRECTION_SUCCESSFUL.getSuccessfulRedirectionLogInfoMessage(), googleSuccessTargetUrl);
				} catch (final IOException exception) {
					log.error(EXCEPTION_MARKER, GOOGLE_REDIRECTION_SUCCESSFUL.getRedirectionExceptionLogErrorMessage(), googleSuccessTargetUrl);
					throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-registration-confirmed-token-expired-url"));
				}
				break;
		}
	}
}
