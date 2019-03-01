package me.grudzien.patryk.oauth2.authentication;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.exception.security.MissingAuthenticationResultException;
import me.grudzien.patryk.oauth2.authentication.chain.AuthenticationResult.Status;
import me.grudzien.patryk.oauth2.authentication.chain.AuthenticationStepsFacade;
import me.grudzien.patryk.oauth2.authentication.checkers.AdditionalChecks;
import me.grudzien.patryk.oauth2.service.google.GooglePrincipalService;
import me.grudzien.patryk.oauth2.service.google.impl.GooglePrincipalServiceProxy;
import me.grudzien.patryk.oauth2.util.CacheManagerHelper;
import me.grudzien.patryk.service.login.impl.MyUserDetailsService;
import me.grudzien.patryk.util.i18n.LocaleMessagesCreator;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;

import static me.grudzien.patryk.factory.FactoryProvider.getFactory;
import static me.grudzien.patryk.factory.FactoryType.EXCEPTION;
import static me.grudzien.patryk.factory.exception.ExceptionType.DYNAMIC_BASED_ON_INPUT;
import static me.grudzien.patryk.util.log.LogMarkers.SECURITY_MARKER;

@Log4j2
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private final UserDetailsService userDetailsService;
	private final LocaleMessagesCreator localeMessagesCreator;
	private final UserDetailsChecker customPreAuthenticationChecks;
	private final UserDetailsChecker customPostAuthenticationChecks;
	private final AdditionalChecks<JwtUser> additionalChecks;
	private final CacheManagerHelper cacheManagerHelper;
	private final GooglePrincipalServiceProxy googlePrincipalServiceProxy;

	public CustomAuthenticationProvider(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
                                        final LocaleMessagesCreator localeMessagesCreator, final GooglePrincipalService googlePrincipalService,
                                        final UserDetailsChecker customPreAuthenticationChecks,
                                        final UserDetailsChecker customPostAuthenticationChecks,
                                        final AdditionalChecks<JwtUser> additionalChecks,
                                        final CacheManagerHelper cacheManagerHelper, final Environment environment) {

        checkNotNull(userDetailsService, "userDetailsService cannot be null!");
        checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
        checkNotNull(googlePrincipalService, "googlePrincipalService cannot be null!");
        checkNotNull(customPreAuthenticationChecks, "customPreAuthenticationChecks cannot be null!");
        checkNotNull(customPostAuthenticationChecks, "customPostAuthenticationChecks cannot be null!");
        checkNotNull(additionalChecks, "additionalChecks cannot be null!");
        checkNotNull(cacheManagerHelper, "cacheManagerHelper cannot be null!");

        this.userDetailsService = userDetailsService;
        this.localeMessagesCreator = localeMessagesCreator;
        this.customPreAuthenticationChecks = customPreAuthenticationChecks;
        this.customPostAuthenticationChecks = customPostAuthenticationChecks;
        this.additionalChecks = additionalChecks;
        this.cacheManagerHelper = cacheManagerHelper;
        this.googlePrincipalServiceProxy = new GooglePrincipalServiceProxy(googlePrincipalService, environment);
    }

    /**
     * This method is called by:
     * {@link me.grudzien.patryk.service.login.impl.UserAuthenticationServiceImpl#authenticateUser(JwtAuthenticationRequest)}
     * only when:
     * @param authentication is of type {@link CustomAuthenticationToken} and all kind of exceptions thrown here are handled by:
     * {@link me.grudzien.patryk.oauth2.authentication.FailedAuthenticationCases}.
     */
	@Override
	public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
	    log.info(SECURITY_MARKER, "Starting custom authentication...");
        return AuthenticationStepsFacade.buildAuthenticationFlow(googlePrincipalServiceProxy, cacheManagerHelper,
                                                                 userDetailsService, localeMessagesCreator,
                                                                 customPreAuthenticationChecks,
                                                                 customPostAuthenticationChecks, additionalChecks)
                                        .performAuthenticationSteps(authentication)
                                        .map(authenticationResult -> Match(authenticationResult.getStatus()).of(
                                                Case($(is(Status.OK)), authenticationResult::getCustomAuthenticationToken),
                                                Case($(is(Status.FAILED)), () -> {
                                                	throw (RuntimeException) getFactory(EXCEPTION).create(DYNAMIC_BASED_ON_INPUT,
	                                                                                                      authenticationResult.getExceptionClass().getName(),
	                                                                                                      authenticationResult.getExceptionMessage());
                                                }))
                                        )
                                        .orElseThrow(() -> new MissingAuthenticationResultException(localeMessagesCreator.buildLocaleMessage(
                                                "missing-authentication-result-exception")));
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
