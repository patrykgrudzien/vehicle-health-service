package me.grudzien.patryk.oauth2.authentication;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Component;

import com.auth0.jwk.JwkException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.oauth2.service.google.GooglePrincipalService;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

@Log4j2
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

	private static final String KEY_ID_ATTRIBUTE = "kid";

	private final UserDetailsService userDetailsService;
	private final LocaleMessagesCreator localeMessagesCreator;
	private final GooglePrincipalService googlePrincipalService;

	public JwtAuthenticationProvider(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
	                                 final LocaleMessagesCreator localeMessagesCreator, final GooglePrincipalService googlePrincipalService) {
		this.userDetailsService = userDetailsService;
		this.localeMessagesCreator = localeMessagesCreator;
		this.googlePrincipalService = googlePrincipalService;
	}

	@Override
	public Authentication authenticate(final Authentication authentication) throws AuthenticationException {

		final String token = (String) authentication.getCredentials();
		final String keyId = JwtHelper.headers(token).get(KEY_ID_ATTRIBUTE);
		Map authInfo = null;
		try {
			final Jwt decodedJwt = JwtHelper.decodeAndVerify(token, googlePrincipalService.rsaVerifier(keyId));
			authInfo = new ObjectMapper().readValue(decodedJwt.getClaims(), Map.class);
		} catch (JwkException | IOException e) {
			e.printStackTrace();
		}
		final String email = (String) authInfo.getOrDefault("email", "");

		final JwtUser jwtUser = Optional.ofNullable((JwtUser) userDetailsService.loadUserByUsername(email))
		                                .orElseThrow(() -> new UsernameNotFoundException(
		                                		localeMessagesCreator.buildLocaleMessageWithParam("user-not-found-by-email", email)));

		return new JwtAuthenticationToken(jwtUser, token, jwtUser.getAuthorities());
	}

	@Override
	public boolean supports(final Class<?> authentication) {
		return JwtAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
