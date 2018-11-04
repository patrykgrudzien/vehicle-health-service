package me.grudzien.patryk.oauth2.authentication.checkers.impl;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

import static me.grudzien.patryk.utils.log.LogMarkers.SECURITY_MARKER;

@Log4j2
@Component
public class CustomPostAuthenticationChecks implements UserDetailsChecker {

	private final LocaleMessagesCreator localeMessagesCreator;

	@Autowired
	public CustomPostAuthenticationChecks(final LocaleMessagesCreator localeMessagesCreator) {
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
		this.localeMessagesCreator = localeMessagesCreator;
	}

	@Override
	public void check(final UserDetails user) {
		if (!user.isCredentialsNonExpired()) {
			log.debug(SECURITY_MARKER, "User account credentials have expired!");
			throw new CredentialsExpiredException(localeMessagesCreator.buildLocaleMessage("user-account-credentials-have-expired"));
		}
	}
}