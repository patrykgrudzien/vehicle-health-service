package me.grudzien.patryk.oauth2.service;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

/**
 * Custom implementation of an {@link org.springframework.security.oauth2.client.userinfo.OAuth2UserService}
 * that supports OpenID Connect 1.0 Provider's.
 *
 * Logs in or registers a user after OpenID Connect SignIn/Up.
 */
@Log4j2
@Service
public class CustomOidcUserService extends OidcUserService {

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
		                              .map(principal -> {
		                              	principal.setAttributes(oidcUser.getAttributes());
		                              	principal.setOidcIdToken(oidcUser.getIdToken());
		                              	principal.setOidcUserInfo(oidcUser.getUserInfo());
		                              	return principal;
		                              })
		                              // TODO
		                              .orElseThrow(() -> new RuntimeException("// TODO:"));
	}
}
