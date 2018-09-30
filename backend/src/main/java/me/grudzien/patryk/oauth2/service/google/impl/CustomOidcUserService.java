package me.grudzien.patryk.oauth2.service.google.impl;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;
import static io.vavr.Predicates.isIn;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

import static me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser.AccountStatus;

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

	private final CustomOAuth2UserService customOAuth2UserService;

	@Autowired
	public CustomOidcUserService(final CustomOAuth2UserService customOAuth2UserService) {
		Preconditions.checkNotNull(customOAuth2UserService, "customOAuth2UserService cannot be null!");
		this.customOAuth2UserService = customOAuth2UserService;
	}

	@Override
	public OidcUser loadUser(final OidcUserRequest userRequest) throws OAuth2AuthenticationException {
		final OidcUser oidcUser = super.loadUser(userRequest);
		return customOAuth2UserService.determineFlowAndPreparePrincipal(oidcUser, userRequest.getClientRegistration())
		                              .map(principal -> Match(principal.getAccountStatus()).of(
		                              		Case($(isIn(AccountStatus.REGISTERED, AccountStatus.LOGGED)), () -> {
				                                principal.setAttributes(oidcUser.getAttributes());
				                                principal.setOidcIdToken(oidcUser.getIdToken());
				                                principal.setOidcUserInfo(oidcUser.getUserInfo());
				                                return principal;
			                                }),
			                                Case($(is(AccountStatus.ALREADY_EXISTS)), accountStatus -> {
				                                final OAuth2Error oauth2Error = new OAuth2Error(accountStatus.name());
				                                throw new OAuth2AuthenticationException(oauth2Error, accountStatus.getDescription());
			                                })
		                              ))
		                              .orElseThrow(() -> {
			                              final OAuth2Error oidcError = new OAuth2Error(UNKNOWN_OIDC_USER_ERROR_CODE);
			                              return new OAuth2AuthenticationException(oidcError, UNKNOWN_OIDC_USER_ERROR_MESSAGE);
		                              });
		// TODO: check all CACHES after whole flow !!!
	}
}
