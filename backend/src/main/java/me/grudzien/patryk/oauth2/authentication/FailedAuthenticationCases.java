package me.grudzien.patryk.oauth2.authentication;

import lombok.extern.log4j.Log4j2;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import me.grudzien.patryk.exceptions.login.BadCredentialsAuthenticationException;
import me.grudzien.patryk.exceptions.login.UserDisabledAuthenticationException;
import me.grudzien.patryk.oauth2.exceptions.JwtTokenNotFoundException;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.run;
import static io.vavr.Predicates.instanceOf;

import static me.grudzien.patryk.utils.log.LogMarkers.EXCEPTION_MARKER;

@Log4j2
public abstract class FailedAuthenticationCases {

	public static Match.Case<DisabledException, Void> UserDisabledExceptionCase(final String email, final LocaleMessagesCreator localeMessagesCreator) {
		return Case($(instanceOf(DisabledException.class)), exception -> run(() -> {
			/**
			 * Exception thrown below is determined in:
			 * {@link org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider#preAuthenticationChecks}
			 * which points to:
			 * {@link org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider.DefaultPreAuthenticationChecks#check(
			 * org.springframework.security.core.userdetails.UserDetails)}
			 */
			log.error(EXCEPTION_MARKER, "User with {} is disabled! Error message -> {}", email, exception.getMessage());
			throw new UserDisabledAuthenticationException(localeMessagesCreator.buildLocaleMessage("user-disabled-exception"));
		}));
	}

	public static Match.Case<BadCredentialsException, Void> BadCredentialsExceptionCase(final LocaleMessagesCreator localeMessagesCreator) {
		return Case($(instanceOf(BadCredentialsException.class)), exception -> run(() -> {
			log.error(EXCEPTION_MARKER, "E-mail address or password is not correct! Error message -> {}", exception.getMessage());
			/**
			 * Exception thrown below is determined in:
			 * {@link org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider#authenticate(org.springframework.security.core.Authentication)}
			 * which tries to retrieve user using:
			 * {@link me.grudzien.patryk.service.security.MyUserDetailsService}
			 */
			throw new BadCredentialsAuthenticationException(localeMessagesCreator.buildLocaleMessage("bad-credentials-exception"));
		}));
	}

	public static Match.Case<LockedException, Void> UserAccountIsLockedExceptionCase(final LocaleMessagesCreator localeMessagesCreator) {
		return Case($(instanceOf(LockedException.class)), exception -> run(() -> {
			log.error(EXCEPTION_MARKER, "User account is locked! Details -> {}", exception.getMessage());
			throw new LockedException(localeMessagesCreator.buildLocaleMessage("user-account-is-locked"));
		}));
	}

	public static Match.Case<AccountExpiredException, Void> UserAccountIsExpiredExceptionCase(final LocaleMessagesCreator localeMessagesCreator) {
		return Case($(instanceOf(AccountExpiredException.class)), exception -> run(() -> {
			log.error(EXCEPTION_MARKER, "User account is expired! Details -> {}", exception.getMessage());
			throw new AccountExpiredException(localeMessagesCreator.buildLocaleMessage("user-account-is-expired"));
		}));
	}

	public static Match.Case<CredentialsExpiredException, Void> CredentialsExpiredExceptionCase(final LocaleMessagesCreator localeMessagesCreator) {
		return Case($(instanceOf(CredentialsExpiredException.class)), exception -> run(() -> {
			log.error(EXCEPTION_MARKER, "User account credentials have expired! Details -> {}", exception.getMessage());
			throw new CredentialsExpiredException(localeMessagesCreator.buildLocaleMessage("user-account-credentials-have-expired"));
		}));
	}

	public static Match.Case<JwtTokenNotFoundException, Void> JwtTokenNotFoundExceptionCase(final LocaleMessagesCreator localeMessagesCreator) {
		return Case($(instanceOf(JwtTokenNotFoundException.class)), exception -> run(() -> {
			log.error(EXCEPTION_MARKER, "Authentication failed! No credentials (JWT Token) provided during SSO login! Details -> {}", exception.getMessage());
			throw new JwtTokenNotFoundException(localeMessagesCreator.buildLocaleMessage("jwt-token-not-found-exception"));
		}));
	}

	public static Match.Case<UsernameNotFoundException, Void> UsernameNotFoundExceptionCase(final String email, final LocaleMessagesCreator localeMessagesCreator) {
		return Case($(instanceOf(UsernameNotFoundException.class)), exception -> run(() -> {
			throw new UsernameNotFoundException(localeMessagesCreator.buildLocaleMessageWithParam("user-not-found-by-email", email));
		}));
	}
}
