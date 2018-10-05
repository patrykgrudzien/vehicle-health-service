package me.grudzien.patryk.oauth2.authentication;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.jwt.JwtTokenUtil;

@Log4j2
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

	private final UserDetailsService userDetailsService;
	private final LocaleMessagesCreator localeMessagesCreator;

	public JwtAuthenticationProvider(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
	                                 final LocaleMessagesCreator localeMessagesCreator) {
		this.userDetailsService = userDetailsService;
		this.localeMessagesCreator = localeMessagesCreator;
	}

	@Override
	public Authentication authenticate(final Authentication authentication) throws AuthenticationException {

		final String token = (String) authentication.getCredentials();
		final String email = JwtTokenUtil.Retriever.getUserEmailFromToken(token);

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
