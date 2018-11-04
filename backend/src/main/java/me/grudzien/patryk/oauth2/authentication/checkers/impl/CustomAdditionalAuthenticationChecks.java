package me.grudzien.patryk.oauth2.authentication.checkers.impl;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;

import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.domain.enums.registration.RegistrationProvider;
import me.grudzien.patryk.exceptions.login.BadCredentialsAuthenticationException;
import me.grudzien.patryk.oauth2.authentication.checkers.AdditionalChecks;
import me.grudzien.patryk.oauth2.exceptions.JwtTokenNotFoundException;
import me.grudzien.patryk.oauth2.exceptions.RegistrationProviderMismatchException;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

import static me.grudzien.patryk.utils.log.LogMarkers.SECURITY_MARKER;

@Log4j2
@Component
public class CustomAdditionalAuthenticationChecks implements AdditionalChecks<JwtUser> {

	private final LocaleMessagesCreator localeMessagesCreator;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public CustomAdditionalAuthenticationChecks(final LocaleMessagesCreator localeMessagesCreator, @Lazy final PasswordEncoder passwordEncoder) {
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
		Preconditions.checkNotNull(passwordEncoder, "passwordEncoder cannot be null!");
		this.localeMessagesCreator = localeMessagesCreator;
		this.passwordEncoder = passwordEncoder;
	}

    /**
     * In case of the Exceptions thrown below, code flow is caught by one of the "Case" inside:
     * {@link me.grudzien.patryk.oauth2.authentication.FailedAuthenticationCases} and translation to JSON format is done by:
     * {@link me.grudzien.patryk.handlers.exception.ExceptionHandlingController}
     */
	@Override
	public void additionalAuthenticationChecks(final JwtUser jwtUser, final Authentication authentication, final String jwtSubjectIdentifier) {
		// credentials in this case are -> JWT Token
		if (StringUtils.isEmpty(authentication.getCredentials())) {
			log.debug(SECURITY_MARKER, "Authentication failed: No credentials (JWT Token) provided during SSO login!");
			throw new JwtTokenNotFoundException(localeMessagesCreator.buildLocaleMessage("jwt-token-not-found-exception"));
		}
		if (!passwordEncoder.matches(jwtSubjectIdentifier, jwtUser.getPassword())) {
            if (jwtUser.getRegistrationProvider() == RegistrationProvider.CUSTOM) {
                log.debug(SECURITY_MARKER, "Authentication failed: User has been registered using CUSTOM provider!");
                throw new RegistrationProviderMismatchException(localeMessagesCreator.buildLocaleMessage("registration-provider-mismatch-exception"));
            }
			log.debug(SECURITY_MARKER, "Authentication failed: JWT Subject Identifier which acts as a password does not match stored value!");
			throw new BadCredentialsAuthenticationException(localeMessagesCreator.buildLocaleMessage("bad-credentials-exception"));
		}
	}
}
