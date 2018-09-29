package me.grudzien.patryk.oauth2.utils;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;

import java.util.function.Predicate;

import static me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME;
import static me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository.SSO_BUTTON_CLICK_EVENT_ENDPOINT_URL_CACHE_KEY;
import static me.grudzien.patryk.utils.log.LogMarkers.OAUTH2_MARKER;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.service.facebook.FacebookPrincipalService;
import me.grudzien.patryk.oauth2.service.google.GooglePrincipalService;

@Log4j2
@Component
public class OAuth2FlowDelegator {

	private final PropertiesKeeper propertiesKeeper;
	private final CacheHelper cacheHelper;
	private final GooglePrincipalService googlePrincipalService;
	private final FacebookPrincipalService facebookPrincipalService;

	@Value("${spring.security.oauth2.client.registration.google.client-name}")
	private String GOOGLE_CLIENT_NAME;
	@Value("${spring.security.oauth2.client.registration.facebook.client-name}")
	private String FACEBOOK_CLIENT_NAME;

	public enum OAuth2Flow {
		LOGIN, REGISTRATION, UNKNOWN
	}

	@Autowired
	public OAuth2FlowDelegator(final PropertiesKeeper propertiesKeeper, final CacheHelper cacheHelper,
	                           final GooglePrincipalService googlePrincipalService, final FacebookPrincipalService facebookPrincipalService) {

		Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");
		Preconditions.checkNotNull(cacheHelper, "cacheHelper cannot be null!");
		Preconditions.checkNotNull(googlePrincipalService, "googlePrincipalService cannot be null!");
		Preconditions.checkNotNull(facebookPrincipalService, "facebookPrincipalService cannot be null!");

		this.propertiesKeeper = propertiesKeeper;
		this.cacheHelper = cacheHelper;
		this.googlePrincipalService = googlePrincipalService;
		this.facebookPrincipalService = facebookPrincipalService;
	}

	public CustomOAuth2OidcPrincipalUser determineFlowAndPreparePrincipal(final ClientRegistration clientRegistration, final String ssoButtonClickEventOriginUrl,
	                                                                      @NonNull final OAuth2User oAuth2User) {

		final Predicate<String> isGoogleProvider = providerName -> !StringUtils.isEmpty(providerName) && providerName.equalsIgnoreCase(GOOGLE_CLIENT_NAME);
		final Predicate<String> isFacebookProvider = providerName -> !StringUtils.isEmpty(providerName) && providerName.equalsIgnoreCase(FACEBOOK_CLIENT_NAME);
		final String clientName = clientRegistration.getClientName();
		final OAuth2Flow oAuth2Flow = determineFlowBasedOnUrl(ssoButtonClickEventOriginUrl);

		if (isGoogleProvider.test(clientName)) {
			log.info(OAUTH2_MARKER, "Processing OAuth2 using ({}) provider.", clientName);
			return googlePrincipalService.finishOAuthFlowAndPreparePrincipal(oAuth2Flow, oAuth2User);
		} else if (isFacebookProvider.test(clientName)) {
			log.info(OAUTH2_MARKER, "Processing OAuth2 using ({}) provider.", clientName);
			return facebookPrincipalService.finishOAuthFlowAndPreparePrincipal(oAuth2Flow, oAuth2User);
		}
		log.warn(OAUTH2_MARKER, "Unknown OAuth2 provider...");
		cacheHelper.evictCacheByNameAndKey(OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME, SSO_BUTTON_CLICK_EVENT_ENDPOINT_URL_CACHE_KEY);
		return null;
	}

	private OAuth2Flow determineFlowBasedOnUrl(@NonNull final String url) {
		if (url.contains(PropertiesKeeper.FrontendRoutes.LOGIN) || url.contains(PropertiesKeeper.FrontendRoutes.LOGOUT)) {
			log.info(OAUTH2_MARKER, "{} flow determined based on \"{}\" URL.", OAuth2Flow.LOGIN.name(), url);
			return OAuth2Flow.LOGIN;
		}
		else if (url.contains(propertiesKeeper.endpoints().REGISTRATION)) {
			log.info(OAUTH2_MARKER, "{} flow determined based on \"{}\" URL.", OAuth2Flow.REGISTRATION.name(), url);
			return OAuth2Flow.REGISTRATION;
		}
		else {
			log.warn(OAUTH2_MARKER, "Cannot determine flow based on \"{}\" URL.", url);
			return OAuth2Flow.UNKNOWN;
		}
	}
}
