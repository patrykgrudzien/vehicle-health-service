package me.grudzien.patryk.oauth2.service;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUserFactory;
import me.grudzien.patryk.oauth2.utils.OAuth2OidcUtils;
import me.grudzien.patryk.service.security.MyUserDetailsService;

/**
 * An implementation of an {@link org.springframework.security.oauth2.client.userinfo.OAuth2UserService}
 * that supports standard OAuth 2.0 Provider's.
 *
 * Logs in or registers a user after OAuth2 SignIn/Up.
 */
@Log4j2
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserDetailsService userDetailsService;

	@Autowired
	public CustomOAuth2UserService(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	public OAuth2User loadUser(final OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
		final OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
		return this.buildPrincipal(oAuth2User, oAuth2UserRequest.getClientRegistration().getRegistrationId());
	}

	public CustomOAuth2OidcPrincipalUser buildPrincipal(final OAuth2User oAuth2User, final String registrationId) {
		final Map<String, Object> attributes = oAuth2User.getAttributes();
		final String oAuth2Email = OAuth2OidcUtils.getOAuth2Email(registrationId, attributes);

		final JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(oAuth2Email);
		return CustomOAuth2OidcPrincipalUserFactory.create(jwtUser);
	}
}
