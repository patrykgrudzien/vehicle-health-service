package me.grudzien.patryk.util.web;

import io.vavr.Tuple2;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.domain.enums.AppFLow;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;

import static me.grudzien.patryk.domain.enums.AppFLow.ACCOUNT_ALREADY_ENABLED;
import static me.grudzien.patryk.domain.enums.AppFLow.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.domain.enums.AppFLow.SYSTEM_COULD_NOT_ENABLE_USER_ACCOUNT;
import static me.grudzien.patryk.domain.enums.AppFLow.USER_LOGGED_IN_USING_GOOGLE;
import static me.grudzien.patryk.domain.enums.AppFLow.VERIFICATION_TOKEN_EXPIRED;
import static me.grudzien.patryk.domain.enums.AppFLow.VERIFICATION_TOKEN_NOT_FOUND;
import static me.grudzien.patryk.util.log.LogMarkers.FLOW_MARKER;
import static me.grudzien.patryk.util.web.CustomURLBuilder.AdditionalParamsDelimiterType;

@Log4j2
@Component
public class HttpLocationHeaderCreator {

	private final ContextPathsResolver contextPathsResolver;
	private final PropertiesKeeper propertiesKeeper;

	@Autowired
	public HttpLocationHeaderCreator(final ContextPathsResolver contextPathsResolver, final PropertiesKeeper propertiesKeeper) {
		Preconditions.checkNotNull(contextPathsResolver, "contextPathsResolver cannot be null!");
		Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");

		this.contextPathsResolver = contextPathsResolver;
		this.propertiesKeeper = propertiesKeeper;
	}

	@SafeVarargs
	public final String createRedirectionUrl(final AppFLow appFLow, final Tuple2<String, String>... additionalParameters) {
		return Match(appFLow).of(
				Case($(is(ACCOUNT_ALREADY_ENABLED)), () -> {
					final String userAlreadyEnabledURL = propertiesKeeper.endpoints().USER_ALREADY_ENABLED;
					log.info(FLOW_MARKER, ACCOUNT_ALREADY_ENABLED.getSuccessfulRedirectionLogInfoMessage(), userAlreadyEnabledURL);
					return contextPathsResolver.determineUrlFor(ACCOUNT_ALREADY_ENABLED) + userAlreadyEnabledURL;
				}),
				Case($(is(CONFIRM_REGISTRATION)), () -> {
					final String registrationConfirmedURL = propertiesKeeper.endpoints().REGISTRATION_CONFIRMED;
					log.info(FLOW_MARKER, CONFIRM_REGISTRATION.getSuccessfulRedirectionLogInfoMessage(), registrationConfirmedURL);
					return contextPathsResolver.determineUrlFor(CONFIRM_REGISTRATION) + registrationConfirmedURL;
				}),
				Case($(is(VERIFICATION_TOKEN_NOT_FOUND)), () -> {
					final String confirmedTokenNotFoundURL = propertiesKeeper.endpoints().CONFIRMATION_TOKEN_NOT_FOUND;
					log.info(FLOW_MARKER, VERIFICATION_TOKEN_NOT_FOUND.getSuccessfulRedirectionLogInfoMessage(), confirmedTokenNotFoundURL);
					return contextPathsResolver.determineUrlFor(CONFIRM_REGISTRATION) + confirmedTokenNotFoundURL;
				}),
				Case($(is(VERIFICATION_TOKEN_EXPIRED)), () -> {
					final String confirmedTokenExpiredURL = propertiesKeeper.endpoints().CONFIRMATION_TOKEN_EXPIRED;
					log.info(FLOW_MARKER, VERIFICATION_TOKEN_EXPIRED.getSuccessfulRedirectionLogInfoMessage(), confirmedTokenExpiredURL);
					return contextPathsResolver.determineUrlFor(CONFIRM_REGISTRATION) + confirmedTokenExpiredURL;
				}),
                Case($(is(SYSTEM_COULD_NOT_ENABLE_USER_ACCOUNT)), () -> {
                    final String systemCouldNotEnableUserAccountURL = propertiesKeeper.endpoints().SYSTEM_COULD_NOT_ENABLE_USER_ACCOUNT;
                    log.info(FLOW_MARKER, SYSTEM_COULD_NOT_ENABLE_USER_ACCOUNT.getSuccessfulRedirectionLogInfoMessage(), systemCouldNotEnableUserAccountURL);
                    return contextPathsResolver.determineUrlFor(SYSTEM_COULD_NOT_ENABLE_USER_ACCOUNT) + systemCouldNotEnableUserAccountURL;
                }),
				Case($(is(USER_LOGGED_IN_USING_GOOGLE)), () -> {
					final String userLoggedInUsingGoogleURL = CustomURLBuilder.buildURL(propertiesKeeper.oAuth2().USER_LOGGED_IN_USING_GOOGLE,
					                                                                    AdditionalParamsDelimiterType.REQUEST_PARAM, additionalParameters);
					log.info(FLOW_MARKER, USER_LOGGED_IN_USING_GOOGLE.getSuccessfulRedirectionLogInfoMessage(), userLoggedInUsingGoogleURL);
					return contextPathsResolver.determineUrlFor(USER_LOGGED_IN_USING_GOOGLE) + userLoggedInUsingGoogleURL;
				})
		);
	}
}
