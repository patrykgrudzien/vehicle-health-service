package me.grudzien.patryk.oauth2.service;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

import java.util.Map;

import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.utils.CacheHelper;
import me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator;
import me.grudzien.patryk.oauth2.utils.OAuth2OidcAttributesExtractor;

import static me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME;
import static me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository.SSO_BUTTON_CLICK_EVENT_ENDPOINT_URL_CACHE_KEY;

/**
 * An implementation of an {@link org.springframework.security.oauth2.client.userinfo.OAuth2UserService}
 * that supports standard OAuth 2.0 Provider's.
 *
 * Logs in or registers a user after OAuth2 SignIn/Up.
 */
@Log4j2
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final OAuth2FlowDelegator oAuth2FlowDelegator;
	private final CacheHelper cacheHelper;

	@Autowired
	public CustomOAuth2UserService(final OAuth2FlowDelegator oAuth2FlowDelegator, final CacheHelper cacheHelper) {
		Preconditions.checkNotNull(oAuth2FlowDelegator, "oAuth2FlowDelegator cannot be null!");
		Preconditions.checkNotNull(cacheHelper, "cacheHelper cannot be null!");

		this.oAuth2FlowDelegator = oAuth2FlowDelegator;
		this.cacheHelper = cacheHelper;
	}

	@Override
	public OAuth2User loadUser(final OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
		final OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
		return this.determineFlowAndPreparePrincipal(oAuth2User, oAuth2UserRequest.getClientRegistration());
	}

	public CustomOAuth2OidcPrincipalUser determineFlowAndPreparePrincipal(final OAuth2User oAuth2User, final ClientRegistration clientRegistration) {
		final Map<String, Object> attributes = oAuth2User.getAttributes();
		final String oAuth2Email = OAuth2OidcAttributesExtractor.getOAuth2AttributeValue(attributes, StandardClaimNames.EMAIL);
		final String ssoButtonClickEventOriginUrl = cacheHelper.loadCache(OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME,
		                                                                  SSO_BUTTON_CLICK_EVENT_ENDPOINT_URL_CACHE_KEY, () -> "");
		// determining either LOGIN or REGISTRATION flow
		return oAuth2FlowDelegator.determineFlowAndPreparePrincipal(clientRegistration, ssoButtonClickEventOriginUrl, oAuth2Email);
	}
}
