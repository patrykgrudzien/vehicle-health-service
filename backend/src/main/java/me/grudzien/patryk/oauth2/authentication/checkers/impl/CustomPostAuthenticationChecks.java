package me.grudzien.patryk.oauth2.authentication.checkers.impl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import me.grudzien.patryk.oauth2.authentication.FailedAuthenticationCases;
import me.grudzien.patryk.oauth2.exception.handler.OAuth2ExceptionsHandler;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

@Slf4j
@Component
public class CustomPostAuthenticationChecks implements UserDetailsChecker {

	private final LocaleMessagesCreator localeMessagesCreator;

	@Autowired
	public CustomPostAuthenticationChecks(final LocaleMessagesCreator localeMessagesCreator) {
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
		this.localeMessagesCreator = localeMessagesCreator;
	}

    /**
     * In case of the Exceptions thrown below, code flow is caught by one of the "Case" inside:
     * {@link FailedAuthenticationCases} and translation to JSON format is done by:
     * {@link OAuth2ExceptionsHandler}.
     */
	@Override
	public void check(final UserDetails user) {
		if (!user.isCredentialsNonExpired()) {
			log.debug("User account credentials have expired!");
			throw new CredentialsExpiredException(localeMessagesCreator.buildLocaleMessage("user-account-credentials-have-expired"));
		}
	}
}
