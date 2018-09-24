package me.grudzien.patryk.oauth2.utils;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;

import java.util.Optional;
import java.util.function.Predicate;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUserFactory;
import me.grudzien.patryk.oauth2.exceptions.UnknownDelegateException;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

import static me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME;
import static me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository.SSO_BUTTON_CLICK_EVENT_ENDPOINT_URL_CACHE_KEY;

@Log4j2
@Component
public class OAuth2FlowDelegator {

	private final PropertiesKeeper propertiesKeeper;
	private final UserDetailsService userDetailsService;
	private final CacheHelper cacheHelper;
	private final LocaleMessagesCreator localeMessagesCreator;

	@Value("${spring.security.oauth2.client.registration.google.client-name}")
	private String GOOGLE_CLIENT_NAME;
	@Value("${spring.security.oauth2.client.registration.facebook.client-name}")
	private String FACEBOOK_CLIENT_NAME;

	private enum OAuth2Flow {
		LOGIN, LOGOUT, REGISTRATION, UNKNOWN
	}

	@Autowired
	public OAuth2FlowDelegator(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService, final PropertiesKeeper propertiesKeeper,
	                           final CacheHelper cacheHelper, final LocaleMessagesCreator localeMessagesCreator) {

		Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");
		Preconditions.checkNotNull(userDetailsService, "userDetailsService cannot be null!");
		Preconditions.checkNotNull(cacheHelper, "cacheHelper cannot be null!");
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");

		this.userDetailsService = userDetailsService;
		this.propertiesKeeper = propertiesKeeper;
		this.cacheHelper = cacheHelper;
		this.localeMessagesCreator = localeMessagesCreator;
	}

	public CustomOAuth2OidcPrincipalUser determineFlowAndPreparePrincipal(final ClientRegistration clientRegistration, final String ssoButtonClickEventOriginUrl,
	                                                                      @NonNull final String oAuth2Email) {

		final Predicate<String> isGoogleProvider = providerName -> !StringUtils.isEmpty(providerName) && providerName.equalsIgnoreCase(GOOGLE_CLIENT_NAME);
		final Predicate<String> isFacebookProvider = providerName -> !StringUtils.isEmpty(providerName) && providerName.equalsIgnoreCase(FACEBOOK_CLIENT_NAME);
		final String clientName = clientRegistration.getClientName();

		if (isGoogleProvider.test(clientName)) {
			return proceedAndPreparePrincipal(ssoButtonClickEventOriginUrl, oAuth2Email);
		} else if (isFacebookProvider.test(clientName)) {
			return proceedAndPreparePrincipal(ssoButtonClickEventOriginUrl, oAuth2Email);
		}
		cacheHelper.evictCacheByNameAndKey(OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME, SSO_BUTTON_CLICK_EVENT_ENDPOINT_URL_CACHE_KEY);
		return null;
	}

	private CustomOAuth2OidcPrincipalUser proceedAndPreparePrincipal(@NonNull final String url, final String emailAddress) {
		switch (determineFlowBasedOnUrl(url)) {
			case LOGIN:
				final Optional<JwtUser> jwtUser = Optional.ofNullable((JwtUser) userDetailsService.loadUserByUsername(emailAddress));
				return jwtUser.map(CustomOAuth2OidcPrincipalUserFactory::create)
				              .orElseThrow(() -> new UsernameNotFoundException(
				              		localeMessagesCreator.buildLocaleMessageWithParam("user-not-found-by-email", emailAddress)));
			case REGISTRATION:
				return null;
			case UNKNOWN:
				throw new UnknownDelegateException(localeMessagesCreator.buildLocaleMessage("unknown-delegate-exception"));
		}
		return null;
	}

	private OAuth2Flow determineFlowBasedOnUrl(@NonNull final String url) {
		if (url.contains(propertiesKeeper.endpoints().LOGIN) || url.contains(propertiesKeeper.endpoints().LOGOUT))
			return OAuth2Flow.LOGIN;
		else if (url.contains(propertiesKeeper.endpoints().REGISTRATION))
			return OAuth2Flow.REGISTRATION;
		else
			return OAuth2Flow.UNKNOWN;
	}
}
