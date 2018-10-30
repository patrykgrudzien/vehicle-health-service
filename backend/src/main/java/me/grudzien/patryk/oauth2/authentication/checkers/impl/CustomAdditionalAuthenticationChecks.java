package me.grudzien.patryk.oauth2.authentication.checkers.impl;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;

import me.grudzien.patryk.exceptions.login.BadCredentialsAuthenticationException;
import me.grudzien.patryk.oauth2.authentication.checkers.AdditionalChecks;
import me.grudzien.patryk.oauth2.exceptions.JwtTokenNotFoundException;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

import static me.grudzien.patryk.utils.log.LogMarkers.SECURITY_MARKER;

@Log4j2
@Component
public class CustomAdditionalAuthenticationChecks implements AdditionalChecks {

	private final LocaleMessagesCreator localeMessagesCreator;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public CustomAdditionalAuthenticationChecks(final LocaleMessagesCreator localeMessagesCreator, @Lazy final PasswordEncoder passwordEncoder) {
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
		Preconditions.checkNotNull(passwordEncoder, "passwordEncoder cannot be null!");
		this.localeMessagesCreator = localeMessagesCreator;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void additionalAuthenticationChecks(final UserDetails userDetails, final Authentication authentication, final String jwtSubjectIdentifier) {
		// credentials in this case are -> JWT Token
		if (StringUtils.isEmpty(authentication.getCredentials())) {
			log.debug(SECURITY_MARKER, "Authentication failed: No credentials (JWT Token) provided during SSO login!");
			throw new JwtTokenNotFoundException(localeMessagesCreator.buildLocaleMessage("jwt-token-not-found-exception"));
		}
		if (!passwordEncoder.matches(jwtSubjectIdentifier, userDetails.getPassword())) {
			log.debug(SECURITY_MARKER, "Authentication failed: JWT Subject Identifier which acts as a password does not match stored value!");
			throw new BadCredentialsAuthenticationException(localeMessagesCreator.buildLocaleMessage("bad-credentials-exception"));
		}
	}
}
