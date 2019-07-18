package me.grudzien.patryk.utils.web;

import io.vavr.Tuple2;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions;
import me.grudzien.patryk.utils.appplication.AppFLow;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;

import static me.grudzien.patryk.utils.appplication.AppFLow.ACCOUNT_ALREADY_ENABLED;
import static me.grudzien.patryk.utils.appplication.AppFLow.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.utils.appplication.AppFLow.SYSTEM_COULD_NOT_ENABLE_USER_ACCOUNT;
import static me.grudzien.patryk.utils.appplication.AppFLow.USER_LOGGED_IN_USING_GOOGLE;
import static me.grudzien.patryk.utils.appplication.AppFLow.VERIFICATION_TOKEN_EXPIRED;
import static me.grudzien.patryk.utils.appplication.AppFLow.VERIFICATION_TOKEN_NOT_FOUND;
import static me.grudzien.patryk.utils.web.CustomURLBuilder.AdditionalParamsDelimiterType;
import static me.grudzien.patryk.utils.web.FrontendRoutesDefinitions.REGISTRATION_CONFIRMED;
import static me.grudzien.patryk.utils.web.FrontendRoutesDefinitions.REGISTRATION_CONFIRMED_SYSTEM_ERROR;
import static me.grudzien.patryk.utils.web.FrontendRoutesDefinitions.REGISTRATION_CONFIRMED_TOKEN_EXPIRED;
import static me.grudzien.patryk.utils.web.FrontendRoutesDefinitions.REGISTRATION_CONFIRMED_TOKEN_NOT_FOUND;
import static me.grudzien.patryk.utils.web.FrontendRoutesDefinitions.REGISTRATION_CONFIRMED_USER_ALREADY_ENABLED;

@Log4j2
@Component
public class HttpLocationHeaderCreator {

	private final ContextPathsResolver contextPathsResolver;

	@Autowired
	public HttpLocationHeaderCreator(final ContextPathsResolver contextPathsResolver) {
		checkNotNull(contextPathsResolver, "contextPathsResolver cannot be null!");
		this.contextPathsResolver = contextPathsResolver;
	}

	@SafeVarargs
	public final String createRedirectionUrl(final AppFLow appFLow, final Tuple2<String, String>... additionalParameters) {
		return Match(appFLow).of(
				Case($(is(ACCOUNT_ALREADY_ENABLED)), () -> {
					log.info(ACCOUNT_ALREADY_ENABLED.getSuccessfulRedirectionLogInfoMessage(), REGISTRATION_CONFIRMED_USER_ALREADY_ENABLED);
					return contextPathsResolver.determineUrlFor(ACCOUNT_ALREADY_ENABLED) + REGISTRATION_CONFIRMED_USER_ALREADY_ENABLED;
				}),
				Case($(is(CONFIRM_REGISTRATION)), () -> {
					log.info(CONFIRM_REGISTRATION.getSuccessfulRedirectionLogInfoMessage(), REGISTRATION_CONFIRMED);
					return contextPathsResolver.determineUrlFor(CONFIRM_REGISTRATION) + REGISTRATION_CONFIRMED;
				}),
				Case($(is(VERIFICATION_TOKEN_NOT_FOUND)), () -> {
					log.info(VERIFICATION_TOKEN_NOT_FOUND.getSuccessfulRedirectionLogInfoMessage(), REGISTRATION_CONFIRMED_TOKEN_NOT_FOUND);
					return contextPathsResolver.determineUrlFor(CONFIRM_REGISTRATION) + REGISTRATION_CONFIRMED_TOKEN_NOT_FOUND;
				}),
				Case($(is(VERIFICATION_TOKEN_EXPIRED)), () -> {
					log.info(VERIFICATION_TOKEN_EXPIRED.getSuccessfulRedirectionLogInfoMessage(), REGISTRATION_CONFIRMED_TOKEN_EXPIRED);
					return contextPathsResolver.determineUrlFor(CONFIRM_REGISTRATION) + REGISTRATION_CONFIRMED_TOKEN_EXPIRED;
				}),
                Case($(is(SYSTEM_COULD_NOT_ENABLE_USER_ACCOUNT)), () -> {
                    log.info(SYSTEM_COULD_NOT_ENABLE_USER_ACCOUNT.getSuccessfulRedirectionLogInfoMessage(), REGISTRATION_CONFIRMED_SYSTEM_ERROR);
                    return contextPathsResolver.determineUrlFor(SYSTEM_COULD_NOT_ENABLE_USER_ACCOUNT) + REGISTRATION_CONFIRMED_SYSTEM_ERROR;
                }),
				Case($(is(USER_LOGGED_IN_USING_GOOGLE)), () -> {
					final String userLoggedInUsingGoogleURL = CustomURLBuilder.buildURL(GoogleResourceDefinitions.USER_LOGGED_IN_USING_GOOGLE,
                                                                                        AdditionalParamsDelimiterType.REQUEST_PARAM, additionalParameters);
					log.info(USER_LOGGED_IN_USING_GOOGLE.getSuccessfulRedirectionLogInfoMessage(), userLoggedInUsingGoogleURL);
					return contextPathsResolver.determineUrlFor(USER_LOGGED_IN_USING_GOOGLE) + userLoggedInUsingGoogleURL;
				})
		);
	}
}
