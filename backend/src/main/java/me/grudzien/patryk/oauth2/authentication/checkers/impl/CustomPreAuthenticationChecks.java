package me.grudzien.patryk.oauth2.authentication.checkers.impl;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import me.grudzien.patryk.exception.login.UserDisabledAuthenticationException;
import me.grudzien.patryk.util.i18n.LocaleMessagesCreator;

import static me.grudzien.patryk.util.log.LogMarkers.SECURITY_MARKER;

@Log4j2
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
     * {@link me.grudzien.patryk.oauth2.authentication.FailedAuthenticationCases} and translation to JSON format is done by:
     * {@link me.grudzien.patryk.handler.exception.ExceptionHandlingController}
     */
	@Override
	public void check(final UserDetails user) {
		if (!user.isAccountNonLocked()) {
			log.debug(SECURITY_MARKER, "User account is locked!");
			// using here Exception provided by Spring (no need to override it and create custom one)
			throw new LockedException(localeMessagesCreator.buildLocaleMessage("user-account-is-locked"));
		}
		if (!user.isEnabled()) {
			log.debug(SECURITY_MARKER, "User account is disabled!");
			throw new UserDisabledAuthenticationException(localeMessagesCreator.buildLocaleMessage("user-disabled-exception"));
		}
		if (!user.isAccountNonExpired()) {
			log.debug(SECURITY_MARKER, "User account is expired!");
			throw new AccountExpiredException(localeMessagesCreator.buildLocaleMessage("user-account-is-expired"));
		}
	}
}
