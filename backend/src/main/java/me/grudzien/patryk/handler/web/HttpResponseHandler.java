package me.grudzien.patryk.handler.web;

import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.domain.enums.AppFLow;
import me.grudzien.patryk.exception.RedirectionException;
import me.grudzien.patryk.util.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.util.web.ContextPathsResolver;
import me.grudzien.patryk.util.web.CustomURLBuilder;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.run;
import static io.vavr.Predicates.is;

import static me.grudzien.patryk.domain.enums.AppFLow.ACCOUNT_ALREADY_ENABLED;
import static me.grudzien.patryk.domain.enums.AppFLow.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.domain.enums.AppFLow.USER_LOGGED_IN_USING_GOOGLE;
import static me.grudzien.patryk.domain.enums.AppFLow.VERIFICATION_TOKEN_EXPIRED;
import static me.grudzien.patryk.domain.enums.AppFLow.VERIFICATION_TOKEN_NOT_FOUND;
import static me.grudzien.patryk.util.log.LogMarkers.EXCEPTION_MARKER;
import static me.grudzien.patryk.util.log.LogMarkers.FLOW_MARKER;
import static me.grudzien.patryk.util.web.CustomURLBuilder.AdditionalParamsDelimiterType;

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
					final String userAlreadyEnabledURL = propertiesKeeper.endpoints().USER_ALREADY_ENABLED;
					Try.run(() -> {
						response.sendRedirect(contextPathsResolver.determineUrlFor(ACCOUNT_ALREADY_ENABLED) + userAlreadyEnabledURL);
						log.info(FLOW_MARKER, ACCOUNT_ALREADY_ENABLED.getSuccessfulRedirectionLogInfoMessage(), userAlreadyEnabledURL);
					}).onFailure(throwable -> {
						log.error(EXCEPTION_MARKER, ACCOUNT_ALREADY_ENABLED.getRedirectionExceptionLogErrorMessage(), userAlreadyEnabledURL);
						throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-user-already-enabled-url"));
					});
				})),
				Case($(is(CONFIRM_REGISTRATION)), flow -> run(() -> {
					final String registrationConfirmedURL = propertiesKeeper.endpoints().REGISTRATION_CONFIRMED;
					Try.run(() -> {
						response.sendRedirect(contextPathsResolver.determineUrlFor(CONFIRM_REGISTRATION) + registrationConfirmedURL);
						log.info(FLOW_MARKER, CONFIRM_REGISTRATION.getSuccessfulRedirectionLogInfoMessage(), registrationConfirmedURL);
					}).onFailure(throwable -> {
						log.error(EXCEPTION_MARKER, CONFIRM_REGISTRATION.getRedirectionExceptionLogErrorMessage(), registrationConfirmedURL);
						throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-registration-confirmed-url"));
					});
				})),
				Case($(is(VERIFICATION_TOKEN_NOT_FOUND)), flow -> run(() -> {
					final String confirmedTokenNotFoundURL = propertiesKeeper.endpoints().CONFIRMATION_TOKEN_NOT_FOUND;
					Try.run(() -> {
						response.sendRedirect(contextPathsResolver.determineUrlFor(CONFIRM_REGISTRATION) + confirmedTokenNotFoundURL);
						log.info(FLOW_MARKER, VERIFICATION_TOKEN_NOT_FOUND.getSuccessfulRedirectionLogInfoMessage(), confirmedTokenNotFoundURL);
					}).onFailure(throwable -> {
						log.error(EXCEPTION_MARKER, VERIFICATION_TOKEN_NOT_FOUND.getRedirectionExceptionLogErrorMessage(), confirmedTokenNotFoundURL);
						throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-registration-confirmed-token-not-found-url"));
					});
				})),
				Case($(is(VERIFICATION_TOKEN_EXPIRED)), flow -> run(() -> {
					final String confirmedTokenExpiredURL = propertiesKeeper.endpoints().CONFIRMATION_TOKEN_EXPIRED;
					Try.run(() -> {
						response.sendRedirect(contextPathsResolver.determineUrlFor(CONFIRM_REGISTRATION) + confirmedTokenExpiredURL);
						log.info(FLOW_MARKER, VERIFICATION_TOKEN_EXPIRED.getSuccessfulRedirectionLogInfoMessage(), confirmedTokenExpiredURL);
					}).onFailure(throwable -> {
						log.error(EXCEPTION_MARKER, VERIFICATION_TOKEN_EXPIRED.getRedirectionExceptionLogErrorMessage(), confirmedTokenExpiredURL);
						throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("cannot-redirect-to-registration-confirmed-token-expired-url"));
					});
				})),
				Case($(is(USER_LOGGED_IN_USING_GOOGLE)), flow -> run(() -> {
					final String userLoggedInUsingGoogleURL = CustomURLBuilder.buildURL(propertiesKeeper.oAuth2().USER_LOGGED_IN_USING_GOOGLE,
					                                                                    AdditionalParamsDelimiterType.REQUEST_PARAM, additionalParameters);
					Try.run(() -> {
						response.sendRedirect(contextPathsResolver.determineUrlFor(USER_LOGGED_IN_USING_GOOGLE) + userLoggedInUsingGoogleURL);
						log.info(FLOW_MARKER, USER_LOGGED_IN_USING_GOOGLE.getSuccessfulRedirectionLogInfoMessage(), userLoggedInUsingGoogleURL);
					}).onFailure(throwable -> {
						log.error(EXCEPTION_MARKER, USER_LOGGED_IN_USING_GOOGLE.getRedirectionExceptionLogErrorMessage(), userLoggedInUsingGoogleURL);
						throw new RedirectionException(localeMessagesCreator.buildLocaleMessage("google-redirection-failure"));
					});
				}))
		);
	}
}
