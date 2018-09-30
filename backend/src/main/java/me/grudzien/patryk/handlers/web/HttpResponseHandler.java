package me.grudzien.patryk.handlers.web;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.run;
import static io.vavr.Predicates.is;

import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import javax.servlet.http.HttpServletResponse;

import static me.grudzien.patryk.domain.enums.AppFLow.ACCOUNT_ALREADY_ENABLED;
import static me.grudzien.patryk.domain.enums.AppFLow.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.domain.enums.AppFLow.GOOGLE_REDIRECTION_SUCCESSFUL;
import static me.grudzien.patryk.domain.enums.AppFLow.VERIFICATION_TOKEN_EXPIRED;
import static me.grudzien.patryk.domain.enums.AppFLow.VERIFICATION_TOKEN_NOT_FOUND;
import static me.grudzien.patryk.utils.log.LogMarkers.EXCEPTION_MARKER;
import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;
import static me.grudzien.patryk.utils.web.CustomURLBuilder.AdditionalParamsDelimiterType;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.domain.enums.AppFLow;
import me.grudzien.patryk.exceptions.RedirectionException;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.web.ContextPathsResolver;
import me.grudzien.patryk.utils.web.CustomURLBuilder;

@Log4j2
@Component
public class HttpResponseHandler {

	private final ContextPathsResolver contextPathsResolver;
	private final LocaleMessagesCreator localeMessagesCreator;
	private final PropertiesKeeper propertiesKeeper;

	@Autowired
	public HttpResponseHandler(final ContextPathsResolver contextPathsResolver, final LocaleMessagesCreator localeMessagesCreator,
	                           final PropertiesKeeper propertiesKeeper) {
		Preconditions.checkNotNull(contextPathsResolver, "contextPathsResolver cannot be null!");
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
		Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");

		this.contextPathsResolver = contextPathsResolver;
		this.localeMessagesCreator = localeMessagesCreator;
		this.propertiesKeeper = propertiesKeeper;
	}

	@SafeVarargs
	public final void redirectUserTo(final AppFLow appFLow, final HttpServletResponse response, final Tuple2<String, String>... additionalParameters) {
		Match(appFLow).of(
				Case($(is(ACCOUNT_ALREADY_ENABLED)), flow -> run(() -> {
					final String userAlreadyEnabledUrl = propertiesKeeper.endpoints().USER_ALREADY_ENABLED;
					Try.run(() -> {
						response.sendRedirect(contextPathsResolver.determineUrlFor(ACCOUNT_ALREADY_ENABLED) + userAlreadyEnabledUrl);
						log.info(FLOW_MARKER, ACCOUNT_ALREADY_ENABLED.getSuccessfulRedirectionLogInfoMessage(), userAlreadyEnabledUrl);
					}).onFailure(throwable -> {
						log.error(EXCEPTION_MARKER, ACCOUNT_ALREADY_ENABLED.getRedirectionExceptionLogErrorMessage(), userAlreadyEnabledUrl);
						throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-user-already-enabled-url"));
					});
				})),
				Case($(is(CONFIRM_REGISTRATION)), flow -> run(() -> {
					final String registrationConfirmedUrl = propertiesKeeper.endpoints().REGISTRATION_CONFIRMED;
					Try.run(() -> {
						response.sendRedirect(contextPathsResolver.determineUrlFor(CONFIRM_REGISTRATION) + registrationConfirmedUrl);
						log.info(FLOW_MARKER, CONFIRM_REGISTRATION.getSuccessfulRedirectionLogInfoMessage(), registrationConfirmedUrl);
					}).onFailure(throwable -> {
						log.error(EXCEPTION_MARKER, CONFIRM_REGISTRATION.getRedirectionExceptionLogErrorMessage(), registrationConfirmedUrl);
						throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-registration-confirmed-url"));
					});
				})),
				Case($(is(VERIFICATION_TOKEN_NOT_FOUND)), flow -> run(() -> {
					final String confirmedTokenNotFoundUrl = propertiesKeeper.endpoints().CONFIRMATION_TOKEN_NOT_FOUND;
					Try.run(() -> {
						response.sendRedirect(contextPathsResolver.determineUrlFor(CONFIRM_REGISTRATION) + confirmedTokenNotFoundUrl);
						log.info(FLOW_MARKER, VERIFICATION_TOKEN_NOT_FOUND.getSuccessfulRedirectionLogInfoMessage(), confirmedTokenNotFoundUrl);
					}).onFailure(throwable -> {
						log.error(EXCEPTION_MARKER, VERIFICATION_TOKEN_NOT_FOUND.getRedirectionExceptionLogErrorMessage(), confirmedTokenNotFoundUrl);
						throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-registration-confirmed-token-not-found-url"));
					});
				})),
				Case($(is(VERIFICATION_TOKEN_EXPIRED)), flow -> run(() -> {
					final String confirmedTokenExpired = propertiesKeeper.endpoints().CONFIRMATION_TOKEN_EXPIRED;
					Try.run(() -> {
						response.sendRedirect(contextPathsResolver.determineUrlFor(CONFIRM_REGISTRATION) + confirmedTokenExpired);
						log.info(FLOW_MARKER, VERIFICATION_TOKEN_EXPIRED.getSuccessfulRedirectionLogInfoMessage(), confirmedTokenExpired);
					}).onFailure(throwable -> {
						log.error(EXCEPTION_MARKER, VERIFICATION_TOKEN_EXPIRED.getRedirectionExceptionLogErrorMessage(), confirmedTokenExpired);
						throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-registration-confirmed-token-expired-url"));
					});
				})),
				Case($(is(GOOGLE_REDIRECTION_SUCCESSFUL)), flow -> run(() -> {
					final String googleSuccessTargetUrl = CustomURLBuilder.buildURL(propertiesKeeper.oAuth2().SUCCESS_TARGET_URL,
					                                                                AdditionalParamsDelimiterType.REQUEST_PARAM, additionalParameters);
					Try.run(() -> {
						response.sendRedirect(contextPathsResolver.determineUrlFor(GOOGLE_REDIRECTION_SUCCESSFUL) + googleSuccessTargetUrl);
						log.info(FLOW_MARKER, GOOGLE_REDIRECTION_SUCCESSFUL.getSuccessfulRedirectionLogInfoMessage(), googleSuccessTargetUrl);
					}).onFailure(throwable -> {
						log.error(EXCEPTION_MARKER, GOOGLE_REDIRECTION_SUCCESSFUL.getRedirectionExceptionLogErrorMessage(), googleSuccessTargetUrl);
						throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("google-redirection-failure"));
					});
				}))
		);
	}
}
