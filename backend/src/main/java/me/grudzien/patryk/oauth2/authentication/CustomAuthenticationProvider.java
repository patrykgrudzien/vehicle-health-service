package me.grudzien.patryk.oauth2.authentication;

import io.vavr.CheckedFunction1;
import io.vavr.Function1;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import java.util.Map;
import java.util.Optional;

import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.oauth2.authentication.checkers.AdditionalChecks;
import me.grudzien.patryk.oauth2.service.google.GooglePrincipalService;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

import static me.grudzien.patryk.utils.log.LogMarkers.SECURITY_MARKER;

@Log4j2
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private static final String KEY_ID_ATTRIBUTE = "kid";

	private final UserDetailsService userDetailsService;
	private final LocaleMessagesCreator localeMessagesCreator;
	private final GooglePrincipalService googlePrincipalService;

	private final UserDetailsChecker customPreAuthenticationChecks;
	private final UserDetailsChecker customPostAuthenticationChecks;
	private final AdditionalChecks additionalChecks;

	public CustomAuthenticationProvider(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
	                                    final LocaleMessagesCreator localeMessagesCreator, final GooglePrincipalService googlePrincipalService,
	                                    final UserDetailsChecker customPreAuthenticationChecks,
	                                    final UserDetailsChecker customPostAuthenticationChecks,
	                                    final AdditionalChecks additionalChecks) {
		Preconditions.checkNotNull(userDetailsService, "userDetailsService cannot be null!");
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
		Preconditions.checkNotNull(googlePrincipalService, "googlePrincipalService cannot be null!");
		Preconditions.checkNotNull(customPreAuthenticationChecks, "customPreAuthenticationChecks cannot be null!");
		Preconditions.checkNotNull(customPostAuthenticationChecks, "customPostAuthenticationChecks cannot be null!");
		Preconditions.checkNotNull(additionalChecks, "additionalChecks cannot be null!");

		this.userDetailsService = userDetailsService;
		this.localeMessagesCreator = localeMessagesCreator;
		this.googlePrincipalService = googlePrincipalService;
		this.customPreAuthenticationChecks = customPreAuthenticationChecks;
		this.customPostAuthenticationChecks = customPostAuthenticationChecks;
		this.additionalChecks = additionalChecks;
	}

	@Override
	public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
	    log.info(SECURITY_MARKER, "Starting custom authentication...");
		final String token = (String) authentication.getCredentials();
		final String keyId = JwtHelper.headers(token).get(KEY_ID_ATTRIBUTE);

		// decoding JWT token
		final RsaVerifier rsaVerifier = Try.of(() -> googlePrincipalService.rsaVerifier(keyId))
                                           .getOrElseThrow(() -> new RuntimeException("Could NOT obtain RSA!"));
		final Jwt decodedJwt = JwtHelper.decodeAndVerify(token, rsaVerifier);

		// reading map of attributes from JWT token
        final Function1<String, Try<Map<String, String>>> liftTry = CheckedFunction1.liftTry(input -> new ObjectMapper()
                .readValue(input, new TypeReference<Map<String, String>>() {}));
		final Try<Map<String, String>> resultTry = liftTry.apply(decodedJwt.getClaims());
        final Map<String, String> authInfo = resultTry.isSuccess() ? resultTry.get() : null;

        // loading email attribute
        final String email = Optional.ofNullable(authInfo)
                                     .map(map -> map.getOrDefault(StandardClaimNames.EMAIL, null))
                                     .orElse(null);

        // loading subject attribute (in this case it's password which was used during registration by google)
		final String jwtSubjectIdentifier = Optional.ofNullable(authInfo)
		                                            .map(map -> map.get(StandardClaimNames.SUB))
		                                            .orElseThrow(() -> new RuntimeException("ERROR while obtaining \"(Subject identifier)\"! It should be always present!"));

        // loading user from DB
		final JwtUser jwtUser = Optional.ofNullable((JwtUser) userDetailsService.loadUserByUsername(email))
		                                .orElseThrow(() -> new UsernameNotFoundException(
		                                		localeMessagesCreator.buildLocaleMessageWithParam("user-not-found-by-email", email)));
		// 1. PRE-authentication checks
		customPreAuthenticationChecks.check(jwtUser);

		// 2. POST-authentication checks
		customPostAuthenticationChecks.check(jwtUser);

		// 3. ADDITIONAL-authentication checks
		final CustomAuthenticationToken customAuthenticationToken = new CustomAuthenticationToken(jwtUser, token, jwtUser.getAuthorities());
		additionalChecks.additionalAuthenticationChecks(jwtUser, customAuthenticationToken, jwtSubjectIdentifier);

		return customAuthenticationToken;
	}

    /**
     * This implementation of {@link AuthenticationProvider} is being fired (executed) only when {@link Authentication} object is of type:
     * {@link CustomAuthenticationToken}.
     */
	@Override
	public boolean supports(final Class<?> authentication) {
	    log.info(SECURITY_MARKER, "Can ({}) be authenticated using ({}) -> ({})", CustomAuthenticationToken.class.getSimpleName(),
	             CustomAuthenticationProvider.class.getSimpleName(), CustomAuthenticationToken.class.isAssignableFrom(authentication));
		return CustomAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
