package me.grudzien.patryk.oauth2.authentication;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import me.grudzien.patryk.authentication.exception.BadCredentialsAuthenticationException;
import me.grudzien.patryk.authentication.exception.UserDisabledAuthenticationException;
import me.grudzien.patryk.authentication.service.impl.MyUserDetailsService;
import me.grudzien.patryk.jwt.exception.CustomAuthenticationUnknownException;
import me.grudzien.patryk.jwt.exception.MissingAuthenticationResultException;
import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepTemplate;
import me.grudzien.patryk.oauth2.exception.JwtTokenNotFoundException;
import me.grudzien.patryk.oauth2.exception.RegistrationProviderMismatchException;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.run;
import static io.vavr.Predicates.instanceOf;
import static lombok.AccessLevel.NONE;

@Log4j2
@NoArgsConstructor(access = NONE)
public final class FailedAuthenticationCases {

    public static Match.Case<UsernameNotFoundException, Void> UsernameNotFoundExceptionCase(final String email, final LocaleMessagesCreator localeMessagesCreator) {
        return Case($(instanceOf(UsernameNotFoundException.class)), exception -> run(() -> {
            throw new UsernameNotFoundException(localeMessagesCreator.buildLocaleMessageWithParam("user-not-found-by-email", email));
        }));
    }

    public static Match.Case<LockedException, Void> UserAccountIsLockedExceptionCase(final LocaleMessagesCreator localeMessagesCreator) {
        return Case($(instanceOf(LockedException.class)), exception -> run(() -> {
            log.error("User account is locked! Details -> {}", exception.getMessage());
            throw new LockedException(localeMessagesCreator.buildLocaleMessage("user-account-is-locked"));
        }));
    }

    public static Match.Case<DisabledException, Void> UserIsDisabledExceptionCase(final String email, final LocaleMessagesCreator localeMessagesCreator) {
        return Case($(instanceOf(DisabledException.class)), exception -> run(() -> {
            /**
             * Exception thrown below is determined in:
             * {@link AbstractUserDetailsAuthenticationProvider#preAuthenticationChecks}
             * which points to:
             * {@link AbstractUserDetailsAuthenticationProvider.DefaultPreAuthenticationChecks#check(UserDetails)}
             */
            log.error("User with {} is disabled! Error message -> {}", email, exception.getMessage());
            throw new UserDisabledAuthenticationException(localeMessagesCreator.buildLocaleMessage("user-disabled-exception"));
        }));
    }

    public static Match.Case<AccountExpiredException, Void> UserAccountIsExpiredExceptionCase(final LocaleMessagesCreator localeMessagesCreator) {
        return Case($(instanceOf(AccountExpiredException.class)), exception -> run(() -> {
            log.error("User account is expired! Details -> {}", exception.getMessage());
            throw new AccountExpiredException(localeMessagesCreator.buildLocaleMessage("user-account-is-expired"));
        }));
    }

    public static Match.Case<CredentialsExpiredException, Void> CredentialsHaveExpiredExceptionCase(final LocaleMessagesCreator localeMessagesCreator) {
        return Case($(instanceOf(CredentialsExpiredException.class)), exception -> run(() -> {
            log.error("User account credentials have expired! Details -> {}", exception.getMessage());
            throw new CredentialsExpiredException(localeMessagesCreator.buildLocaleMessage("user-account-credentials-have-expired"));
        }));
    }

    public static Match.Case<JwtTokenNotFoundException, Void> JwtTokenNotFoundExceptionCase(final LocaleMessagesCreator localeMessagesCreator) {
        return Case($(instanceOf(JwtTokenNotFoundException.class)), exception -> run(() -> {
            log.error("Authentication failed! No credentials (JWT Token) provided during SSO login! Details -> {}", exception.getMessage());
            throw new JwtTokenNotFoundException(localeMessagesCreator.buildLocaleMessage("jwt-token-not-found-exception"));
        }));
    }

    public static Match.Case<RegistrationProviderMismatchException, Void> RegistrationProviderMismatchExceptionCase(final LocaleMessagesCreator localeMessagesCreator) {
        return Case($(instanceOf(RegistrationProviderMismatchException.class)), exception -> run(() -> {
            throw new RegistrationProviderMismatchException(localeMessagesCreator.buildLocaleMessage("registration-provider-mismatch-exception"));
        }));
    }

	public static Match.Case<BadCredentialsException, Void> BadCredentialsExceptionCase(final LocaleMessagesCreator localeMessagesCreator) {
		return Case($(instanceOf(BadCredentialsException.class)), exception -> run(() -> {
			log.error("E-mail address or password is not correct! Error message -> {}", exception.getMessage());
			/**
			 * Exception thrown below is determined in:
			 * {@link AbstractUserDetailsAuthenticationProvider#authenticate(Authentication)}
			 * which tries to retrieve user using:
			 * {@link MyUserDetailsService}
			 */
			throw new BadCredentialsAuthenticationException(localeMessagesCreator.buildLocaleMessage("bad-credentials-exception"));
		}));
	}

    public static Match.Case<MissingAuthenticationResultException, Void> MissingAuthenticationResultExceptionCase(final LocaleMessagesCreator localeMessagesCreator) {
        return Case($(instanceOf(MissingAuthenticationResultException.class)), exception -> run(() -> {
            log.error("Authentication result has NOT been provided after authentication flow! Error message -> {}", exception.getMessage());
            throw new MissingAuthenticationResultException(localeMessagesCreator.buildLocaleMessage("missing-authentication-result-exception"));
        }));
    }

    /**
     * This case is matched when some specific exception of (single authentication operation) is thrown.
     * {@link AbstractAuthenticationStepTemplate#performSingleAuthOperation(Authentication)}
     */
    public static Match.Case<? extends RuntimeException, Void> CustomAuthenticationUnknownExceptionCase(final LocaleMessagesCreator localeMessagesCreator) {
        return Case($(), exception -> run(() -> {
            log.error("Some unknown exception was thrown during authentication flow! Error message -> {}", exception.getMessage());
            throw new CustomAuthenticationUnknownException(localeMessagesCreator.buildLocaleMessage("custom-authentication-unknown-exception"));
        }));
    }
}
