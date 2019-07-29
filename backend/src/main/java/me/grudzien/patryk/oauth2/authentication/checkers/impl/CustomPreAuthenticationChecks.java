package me.grudzien.patryk.oauth2.authentication.checkers.impl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import me.grudzien.patryk.oauth2.authentication.FailedAuthenticationCases;
import me.grudzien.patryk.oauth2.exception.handler.OAuth2ExceptionsHandler;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

@Slf4j
@Component
public class CustomPreAuthenticationChecks implements UserDetailsChecker {

	private final LocaleMessagesCreator localeMessagesCreator;

	@Autowired
	public CustomPreAuthenticationChecks(final LocaleMessagesCreator localeMessagesCreator) {
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
		if (!user.isAccountNonLocked()) {
			log.debug("User account is locked!");
			// using here Exception provided by Spring (no need to override it and create custom one)
			throw new LockedException(localeMessagesCreator.buildLocaleMessage("user-account-is-locked"));
		}
		if (!user.isEnabled()) {
			log.debug("User account is disabled!");
			throw new DisabledException(localeMessagesCreator.buildLocaleMessage("user-disabled-exception"));
		}
		if (!user.isAccountNonExpired()) {
			log.debug("User account is expired!");
			throw new AccountExpiredException(localeMessagesCreator.buildLocaleMessage("user-account-is-expired"));
		}
	}
}
