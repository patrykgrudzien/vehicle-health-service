package me.grudzien.patryk.oauth2.service;

import lombok.extern.log4j.Log4j2;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class CustomOidcUserService extends OidcUserService {

	@Override
	public OidcUser loadUser(final OidcUserRequest userRequest) throws OAuth2AuthenticationException {
		return super.loadUser(userRequest);
	}
}
