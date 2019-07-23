package me.grudzien.patryk.oauth2.authentication.checkers.impl;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import me.grudzien.patryk.authentication.exception.BadCredentialsAuthenticationException;
import me.grudzien.patryk.authentication.model.dto.JwtUser;
import me.grudzien.patryk.oauth2.authentication.checkers.AdditionalChecks;
import me.grudzien.patryk.oauth2.exception.JwtTokenNotFoundException;
import me.grudzien.patryk.oauth2.exception.RegistrationProviderMismatchException;
import me.grudzien.patryk.registration.model.enums.RegistrationProvider;
import me.grudzien.patryk.utils.handler.ExceptionHandlingController;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

import static com.google.common.base.Preconditions.checkNotNull;

@Log4j2
@Component
public class CustomAdditionalAuthenticationChecks implements AdditionalChecks<JwtUser> {

	private final LocaleMessagesCreator localeMessagesCreator;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public CustomAdditionalAuthenticationChecks(final LocaleMessagesCreator localeMessagesCreator, @Lazy final PasswordEncoder passwordEncoder) {
		checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
		checkNotNull(passwordEncoder, "passwordEncoder cannot be null!");

		this.localeMessagesCreator = localeMessagesCreator;
		this.passwordEncoder = passwordEncoder;
	}

    /**
     * In case of the Exceptions thrown below, code flow is caught by one of the "Case" inside:
     * {@link me.grudzien.patryk.oauth2.authentication.FailedAuthenticationCases} and translation to JSON format is done by:
     * {@link ExceptionHandlingController}
     */
	@Override
	public void additionalAuthenticationChecks(final JwtUser jwtUser, final Authentication authentication, final String jwtSubjectIdentifier) {
		// credentials in this case are -> JWT Token
		if (StringUtils.isEmpty(authentication.getCredentials())) {
			log.debug("Authentication failed: No credentials (JWT Token) provided during SSO login!");
			throw new JwtTokenNotFoundException(localeMessagesCreator.buildLocaleMessage("jwt-token-not-found-exception"));
		}
		if (!passwordEncoder.matches(jwtSubjectIdentifier, jwtUser.getPassword())) {
            if (jwtUser.getRegistrationProvider() == RegistrationProvider.CUSTOM) {
                log.debug("Authentication failed: User has been registered using CUSTOM provider!");
                throw new RegistrationProviderMismatchException(localeMessagesCreator.buildLocaleMessage("registration-provider-mismatch-exception"));
            }
			log.debug("Authentication failed: JWT Subject Identifier which acts as a password does not match stored value!");
			throw new BadCredentialsAuthenticationException(localeMessagesCreator.buildLocaleMessage("bad-credentials-exception"));
		}
	}
}
