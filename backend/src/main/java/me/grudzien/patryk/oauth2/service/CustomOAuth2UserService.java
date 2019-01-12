package me.grudzien.patryk.oauth2.service;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import java.util.Optional;

import me.grudzien.patryk.oauth2.util.CacheHelper;
import me.grudzien.patryk.oauth2.util.OAuth2FlowDelegator;
import me.grudzien.patryk.oauth2.util.OAuth2FlowDelegator.OAuth2Flow;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.isIn;

import static me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser.AccountStatus;
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

	public static final String UNKNOWN_OAUTH2_USER_ERROR_CODE = "UNKNOWN_OAUTH2_USER_ERROR_CODE";
	private static final String UNKNOWN_OAUTH2_USER_ERROR_MESSAGE = "Some unknown exception occurred while loading OAuth2User...";

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
        // loading URL from cache where SSO has been clicked
        final String ssoButtonClickEventOriginUrl = cacheHelper.loadCache(OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME,
                                                                          SSO_BUTTON_CLICK_EVENT_ENDPOINT_URL_CACHE_KEY, () -> StringUtils.EMPTY);
        // determining user account OAuth2 flow
        final OAuth2Flow oAuth2Flow = oAuth2FlowDelegator.determineFlowBasedOnUrl(ssoButtonClickEventOriginUrl);
        // preparing principal with appropriate account OAuth2 flow
		return Optional.ofNullable(oAuth2FlowDelegator.handlePrincipalCreation(oAuth2User, oAuth2UserRequest.getClientRegistration(), oAuth2Flow))
                       .map(principal -> Match(principal.getAccountStatus()).of(
                               /**
                                * Successful case is later handled by {@link me.grudzien.patryk.oauth2.handler.CustomOAuth2AuthenticationSuccessHandler}
                                */
                               Case($(isIn(AccountStatus.get2xxStatuses())), principal),
                               /**
                                * Failed case is later handled by {@link me.grudzien.patryk.oauth2.handler.CustomOAuth2AuthenticationFailureHandler}
                                */
                               Case($(isIn(AccountStatus.get4xxStatuses())), accountStatus -> {
                                   final OAuth2Error oauth2Error = new OAuth2Error(accountStatus.name());
                                   throw new OAuth2AuthenticationException(oauth2Error, accountStatus.getAccountStatusDescription());
                               })
                       ))
                       .orElseThrow(() -> {
                           /**
                            * Failed case is later handled by {@link me.grudzien.patryk.oauth2.handler.CustomOAuth2AuthenticationFailureHandler}
                            */
                           final OAuth2Error oauth2Error = new OAuth2Error(UNKNOWN_OAUTH2_USER_ERROR_CODE);
                           return new OAuth2AuthenticationException(oauth2Error, UNKNOWN_OAUTH2_USER_ERROR_MESSAGE);
                       });
	}
}
