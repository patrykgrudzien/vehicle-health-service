package me.grudzien.patryk.oauth2.util;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.function.Predicate;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.service.facebook.FacebookPrincipalService;
import me.grudzien.patryk.oauth2.service.google.GooglePrincipalService;
import me.grudzien.patryk.oauth2.service.google.impl.GooglePrincipalServiceProxy;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.anyOf;

import static me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME;
import static me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository.SSO_BUTTON_CLICK_EVENT_ENDPOINT_URL_CACHE_KEY;
import static me.grudzien.patryk.util.log.LogMarkers.OAUTH2_MARKER;

@Log4j2
@Component
public class OAuth2FlowDelegator {

	private final Predicate<String> URL_CONTAINS_LOGIN_OR_LOGOUT = input -> input.contains(PropertiesKeeper.FrontendRoutes.LOGIN) ||
	                                                                        input.contains(PropertiesKeeper.FrontendRoutes.LOGOUT);
	private final Predicate<String> URL_EQUALS_REGISTRATION_CONFIRMED = input -> input.equalsIgnoreCase(PropertiesKeeper.FrontendRoutes.REGISTRATION_CONFIRMED);
	private final Predicate<String> URL_EQUALS_REGISTRATION_FORM = input -> input.equalsIgnoreCase(PropertiesKeeper.FrontendRoutes.REGISTRATION_FORM);

	private final CacheManagerHelper cacheManagerHelper;
	private final GooglePrincipalServiceProxy googlePrincipalServiceProxy;
	private final FacebookPrincipalService facebookPrincipalService;

	@Value("${spring.security.oauth2.client.registration.google.client-name}")
	private String GOOGLE_CLIENT_NAME;
	@Value("${spring.security.oauth2.client.registration.facebook.client-name}")
	private String FACEBOOK_CLIENT_NAME;

	public enum OAuth2Flow {
		LOGIN, REGISTRATION, UNKNOWN
	}

	@Autowired
	public OAuth2FlowDelegator(final CacheManagerHelper cacheManagerHelper,
                               final GooglePrincipalService googlePrincipalService,
                               final FacebookPrincipalService facebookPrincipalService,
                               final Environment environment) {

		checkNotNull(cacheManagerHelper, "cacheManagerHelper cannot be null!");
		checkNotNull(facebookPrincipalService, "facebookPrincipalService cannot be null!");

		this.cacheManagerHelper = cacheManagerHelper;
		this.googlePrincipalServiceProxy = new GooglePrincipalServiceProxy(googlePrincipalService, environment);
		this.facebookPrincipalService = facebookPrincipalService;
	}

    public OAuth2Flow determineFlowBasedOnUrl(@NonNull final String url) {
        return Match(url).of(
                Case($(anyOf(URL_CONTAINS_LOGIN_OR_LOGOUT, URL_EQUALS_REGISTRATION_CONFIRMED)), () -> {
                    log.info(OAUTH2_MARKER, "{} flow determined based on \"{}\" URL.", OAuth2Flow.LOGIN.name(), url);
                    return OAuth2Flow.LOGIN;
                }),
                Case($(URL_EQUALS_REGISTRATION_FORM), () -> {
                    log.info(OAUTH2_MARKER, "{} flow determined based on \"{}\" URL.", OAuth2Flow.REGISTRATION.name(), url);
                    return OAuth2Flow.REGISTRATION;
                }),
                Case($(), () -> {
                    log.warn(OAUTH2_MARKER, "Cannot determine flow based on \"{}\" URL.", url);
                    return OAuth2Flow.UNKNOWN;
                })
        );
    }

	public CustomOAuth2OidcPrincipalUser handlePrincipalCreation(@NonNull final OAuth2User oAuth2User, final ClientRegistration clientRegistration,
                                                                 final OAuth2Flow oAuth2Flow) {
		final Predicate<String> isGoogleProvider = providerName -> !StringUtils.isEmpty(providerName) && providerName.equalsIgnoreCase(GOOGLE_CLIENT_NAME);
		final Predicate<String> isFacebookProvider = providerName -> !StringUtils.isEmpty(providerName) && providerName.equalsIgnoreCase(FACEBOOK_CLIENT_NAME);
		final String clientName = clientRegistration.getClientName();

		return Match(clientName).of(
		        Case($(isGoogleProvider), () -> {
		            log.info(OAUTH2_MARKER, "Processing OAuth2 using ({}) provider.", clientName);
					cacheManagerHelper.evictCache(OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME, SSO_BUTTON_CLICK_EVENT_ENDPOINT_URL_CACHE_KEY);
					return googlePrincipalServiceProxy.prepareGooglePrincipal(oAuth2Flow, oAuth2User);
				}),
				Case($(isFacebookProvider), () -> {
					log.info(OAUTH2_MARKER, "Processing OAuth2 using ({}) provider.", clientName);
					cacheManagerHelper.evictCache(OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME, SSO_BUTTON_CLICK_EVENT_ENDPOINT_URL_CACHE_KEY);
					return facebookPrincipalService.prepareFacebookPrincipal(oAuth2Flow, oAuth2User);
				}),
				Case($(), () -> {
					log.warn(OAUTH2_MARKER, "Unknown OAuth2 provider...");
					cacheManagerHelper.evictCache(OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME, SSO_BUTTON_CLICK_EVENT_ENDPOINT_URL_CACHE_KEY);
					return null;
				})
		);
	}
}
