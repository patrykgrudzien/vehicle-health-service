package me.grudzien.patryk.handlers.web;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.domain.enums.AppFLow;
import me.grudzien.patryk.exceptions.RedirectionException;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.web.AppUrlsResolver;

import static me.grudzien.patryk.domain.enums.AppFLow.ACCOUNT_ALREADY_ENABLED;
import static me.grudzien.patryk.domain.enums.AppFLow.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.domain.enums.AppFLow.VERIFICATION_TOKEN_EXPIRED;
import static me.grudzien.patryk.domain.enums.AppFLow.VERIFICATION_TOKEN_NOT_FOUND;
import static me.grudzien.patryk.utils.log.LogMarkers.EXCEPTION_MARKER;
import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;

@Log4j2
@Component
public class HttpResponseHandler {

	private final AppUrlsResolver appUrlsResolver;
	private final CustomApplicationProperties customApplicationProperties;
	private final LocaleMessagesCreator localeMessagesCreator;

	@Autowired
	public HttpResponseHandler(final AppUrlsResolver appUrlsResolver,
	                           final CustomApplicationProperties customApplicationProperties,
	                           final LocaleMessagesCreator localeMessagesCreator) {
		Preconditions.checkNotNull(appUrlsResolver, "appUrlsResolver cannot be null!");
		Preconditions.checkNotNull(customApplicationProperties, "customApplicationProperties cannot be null!");
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");

		this.appUrlsResolver = appUrlsResolver;
		this.customApplicationProperties = customApplicationProperties;
		this.localeMessagesCreator = localeMessagesCreator;
	}

	public void redirectUserTo(final AppFLow appFLow, final HttpServletResponse response) {
		/**
		 * Creating base app URLs for:
		 * {@link me.grudzien.patryk.service.registration.UserRegistrationServiceImpl#confirmRegistration(String, javax.servlet.http.HttpServletResponse)}
		 *
		 * @return Base app URls which are used to redirect user to specific screens on UI.
		 */
		switch (appFLow) {
			case ACCOUNT_ALREADY_ENABLED:
				final String userAlreadyEnabledUrl = customApplicationProperties.getEndpoints().getRegistration().getUserAlreadyEnabled();
				try {
					response.sendRedirect(appUrlsResolver.determineAppUrlFor(ACCOUNT_ALREADY_ENABLED) + userAlreadyEnabledUrl);
					log.info(FLOW_MARKER, ACCOUNT_ALREADY_ENABLED.getSuccessfulRedirectionLogInfoMessage(), userAlreadyEnabledUrl);
				} catch (final IOException exception) {
					log.error(EXCEPTION_MARKER, ACCOUNT_ALREADY_ENABLED.getRedirectionExceptionLogErrorMessage(), userAlreadyEnabledUrl);
					throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-user-already-enabled-url"));
				}
				break;
			case CONFIRM_REGISTRATION:
				final String registrationConfirmedUrl = customApplicationProperties.getEndpoints().getRegistration().getConfirmed();
				try {
					response.sendRedirect(appUrlsResolver.determineAppUrlFor(CONFIRM_REGISTRATION) + registrationConfirmedUrl);
					log.info(FLOW_MARKER, CONFIRM_REGISTRATION.getSuccessfulRedirectionLogInfoMessage(), registrationConfirmedUrl);
				} catch (final IOException exception) {
					log.error(EXCEPTION_MARKER, CONFIRM_REGISTRATION.getRedirectionExceptionLogErrorMessage(), registrationConfirmedUrl);
					throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-registration-confirmed-url"));
				}
				break;
			case VERIFICATION_TOKEN_NOT_FOUND:
				final String confirmedTokenNotFoundUrl = customApplicationProperties.getEndpoints().getRegistration().getConfirmedTokenNotFound();
				try {
					response.sendRedirect(appUrlsResolver.determineAppUrlFor(CONFIRM_REGISTRATION) + confirmedTokenNotFoundUrl);
					log.info(FLOW_MARKER, VERIFICATION_TOKEN_NOT_FOUND.getSuccessfulRedirectionLogInfoMessage(), confirmedTokenNotFoundUrl);
				} catch (final IOException exception) {
					log.error(EXCEPTION_MARKER, VERIFICATION_TOKEN_NOT_FOUND.getRedirectionExceptionLogErrorMessage(), confirmedTokenNotFoundUrl);
					throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-registration-confirmed-token-not-found-url"));
				}
				break;
			case VERIFICATION_TOKEN_EXPIRED:
				final String confirmedTokenExpired = customApplicationProperties.getEndpoints().getRegistration().getConfirmedTokenExpired();
				try {
					response.sendRedirect(appUrlsResolver.determineAppUrlFor(CONFIRM_REGISTRATION) + confirmedTokenExpired);
					log.info(FLOW_MARKER, VERIFICATION_TOKEN_EXPIRED.getSuccessfulRedirectionLogInfoMessage(), confirmedTokenExpired);
				} catch (final IOException exception) {
					log.error(EXCEPTION_MARKER, VERIFICATION_TOKEN_EXPIRED.getRedirectionExceptionLogErrorMessage(), confirmedTokenExpired);
					throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-registration-confirmed-token-expired-url"));
				}
				break;
		}
	}
}
