package me.grudzien.patryk.oauth2.service;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import java.util.Optional;

import me.grudzien.patryk.oauth2.utils.CacheManagerHelper;
import me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator;
import me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator.OAuth2Flow;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.isIn;

import static me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser.AccountStatus;
import static me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME;
import static me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository.SSO_BUTTON_CLICK_EVENT_ENDPOINT_URL_CACHE_KEY;

/**
 * Custom implementation of an {@link org.springframework.security.oauth2.client.userinfo.OAuth2UserService}
 * that supports OpenID Connect 1.0 Provider's.
 *
 * Logs in or registers a user after OpenID Connect SignIn/Up.
 */
@Log4j2
@Service
public class CustomOidcUserService extends OidcUserService {

	public static final String UNKNOWN_OIDC_USER_ERROR_CODE = "UNKNOWN_OIDC_USER_ERROR_CODE";
	private static final String UNKNOWN_OIDC_USER_ERROR_MESSAGE = "Some unknown exception occurred while loading OidcUser...";

    private final OAuth2FlowDelegator oAuth2FlowDelegator;
    private final CacheManagerHelper cacheManagerHelper;

	@Autowired
	public CustomOidcUserService(final OAuth2FlowDelegator oAuth2FlowDelegator, final CacheManagerHelper cacheManagerHelper) {
        Preconditions.checkNotNull(oAuth2FlowDelegator, "oAuth2FlowDelegator cannot be null!");
        Preconditions.checkNotNull(cacheManagerHelper, "cacheManagerHelper cannot be null!");
        this.oAuth2FlowDelegator = oAuth2FlowDelegator;
        this.cacheManagerHelper = cacheManagerHelper;
    }

	@Override
	public OidcUser loadUser(final OidcUserRequest userRequest) throws OAuth2AuthenticationException {
		final OidcUser oidcUser = super.loadUser(userRequest);
		// loading URL from cache where SSO has been clicked
		final String ssoButtonClickEventOriginUrl = cacheManagerHelper.getCacheValue(OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME,
                                                                                     SSO_BUTTON_CLICK_EVENT_ENDPOINT_URL_CACHE_KEY, String.class)
                                                                      .orElse(StringUtils.EMPTY);
		// determining user account OAuth2 flow
        final OAuth2Flow oAuth2Flow = oAuth2FlowDelegator.determineFlowBasedOnUrl(ssoButtonClickEventOriginUrl);
        // preparing principal with appropriate account OAuth2 flow
        return Optional.ofNullable(oAuth2FlowDelegator.handlePrincipalCreation(oidcUser, userRequest.getClientRegistration(), oAuth2Flow))
                       .map(principal -> Match(principal.getAccountStatus()).of(
                               /**
                                * Successful case is later handled by {@link me.grudzien.patryk.oauth2.handler.CustomOAuth2AuthenticationSuccessHandler}
                                */
                               Case($(isIn(AccountStatus.get2xxStatuses())), () -> {
                                   principal.setAttributes(oidcUser.getAttributes());
                                   principal.setOidcIdToken(oidcUser.getIdToken());
				                   principal.setOidcUserInfo(oidcUser.getUserInfo());
				                   return principal;
                               }),
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
                           final OAuth2Error oidcError = new OAuth2Error(UNKNOWN_OIDC_USER_ERROR_CODE);
                           return new OAuth2AuthenticationException(oidcError, UNKNOWN_OIDC_USER_ERROR_MESSAGE);
                       });
	}
}
