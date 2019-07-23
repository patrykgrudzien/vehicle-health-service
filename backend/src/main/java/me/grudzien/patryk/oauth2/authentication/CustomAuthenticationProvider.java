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

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.model.dto.JwtUser;
import me.grudzien.patryk.authentication.service.impl.MyUserDetailsService;
import me.grudzien.patryk.authentication.service.impl.UserAuthenticationServiceImpl;
import me.grudzien.patryk.jwt.exception.MissingAuthenticationResultException;
import me.grudzien.patryk.oauth2.authentication.chain.AuthenticationResult;
import me.grudzien.patryk.oauth2.authentication.chain.AuthenticationStepsFacade;
import me.grudzien.patryk.oauth2.authentication.checkers.AdditionalChecks;
import me.grudzien.patryk.oauth2.authentication.model.CustomAuthenticationToken;
import me.grudzien.patryk.oauth2.service.google.GooglePrincipalService;
import me.grudzien.patryk.oauth2.service.google.impl.GooglePrincipalServiceProxy;
import me.grudzien.patryk.oauth2.utils.CacheManagerHelper;
import me.grudzien.patryk.utils.factory.FactoryProvider;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;

import static me.grudzien.patryk.authentication.model.factory.ExceptionType.DYNAMIC_BASED_ON_INPUT;
import static me.grudzien.patryk.oauth2.authentication.chain.AuthenticationResult.Status.FAILED;
import static me.grudzien.patryk.oauth2.authentication.chain.AuthenticationResult.Status.OK;
import static me.grudzien.patryk.utils.factory.FactoryType.EXCEPTION;

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
     * {@link UserAuthenticationServiceImpl#authenticateUser(JwtAuthenticationRequest)}
     * only when:
     * @param authentication is of type {@link CustomAuthenticationToken} and all kind of exceptions thrown here
     * are handled by: {@link FailedAuthenticationCases}.
     */
	@Override
	public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
	    log.info("Starting custom authentication...");
        return AuthenticationStepsFacade.buildAuthenticationFlow()
                                        .withGooglePrincipalServiceProxy(googlePrincipalServiceProxy)
                                        .withCacheManagerHelper(cacheManagerHelper)
                                        .withUserDetailsService(userDetailsService)
                                        .withLocaleMessageCreator(localeMessagesCreator)
                                        .withPreUserDetailsChecker(customPreAuthenticationChecks)
                                        .withPostUserDetailsChecker(customPostAuthenticationChecks)
                                        .withAdditionalChecks(additionalChecks)
                                        .build()
                                        .performAuthenticationSteps(authentication)
                                        .map(authenticationResult -> Match(authenticationResult.getStatus()).of(
                                                Case($(is(OK)), authenticationResult::getCustomAuthenticationToken),
                                                Case($(is(FAILED)), () -> {
                                                    throw dynamicRuntimeException(authenticationResult);
                                                }))
                                        )
                                        .orElseThrow(() -> new MissingAuthenticationResultException(localeMessagesCreator.buildLocaleMessage(
                                                "missing-authentication-result-exception")));
	}

	private RuntimeException dynamicRuntimeException(final AuthenticationResult authenticationResult) {
	    return (RuntimeException) FactoryProvider.getFactory(EXCEPTION).create(
	            DYNAMIC_BASED_ON_INPUT,
                authenticationResult.getExceptionClass().getName(),
                authenticationResult.getExceptionMessage()
        );
    }

    /**
     * This implementation of {@link AuthenticationProvider} is being fired (executed) only when {@link Authentication} object is of type:
     * {@link CustomAuthenticationToken}.
     */
	@Override
	public boolean supports(final Class<?> authentication) {
        final Class<CustomAuthenticationToken> tokenClass = CustomAuthenticationToken.class;
        log.info("Can ({}) be authenticated using ({}) -> ({})",
                 tokenClass.getSimpleName(), CustomAuthenticationProvider.class.getSimpleName(), tokenClass.isAssignableFrom(authentication));
		return tokenClass.isAssignableFrom(authentication);
	}
}
