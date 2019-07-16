package me.grudzien.patryk.configuration.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.configuration.filters.GenericJwtTokenFilter;
import me.grudzien.patryk.configuration.filters.LocaleDeterminerFilter;
import me.grudzien.patryk.configuration.filters.ServletExceptionHandlerFilter;
import me.grudzien.patryk.oauth2.handler.CustomOAuth2AuthenticationFailureHandler;
import me.grudzien.patryk.oauth2.handler.CustomOAuth2AuthenticationSuccessHandler;
import me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository;
import me.grudzien.patryk.oauth2.service.CustomOAuth2UserService;
import me.grudzien.patryk.oauth2.service.CustomOidcUserService;
import me.grudzien.patryk.oauth2.util.CacheManagerHelper;
import me.grudzien.patryk.service.jwt.JwtTokenClaimsRetriever;
import me.grudzien.patryk.service.jwt.JwtTokenValidator;
import me.grudzien.patryk.util.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.util.i18n.LocaleMessagesHelper;

@SuppressWarnings("JavadocReference")
public final class SecurityConfigContext {

    private SecurityConfigContext() {
        throw new UnsupportedOperationException("Creating object of this class is not allowed!");
    }

    static void sessionCreationPolicy(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    static void httpSessionRequestCache(final HttpSecurity httpSecurity, final HttpSessionRequestCache httpSessionRequestCache) throws Exception {
        httpSecurity.requestCache()
                    .requestCache(httpSessionRequestCache);
    }

    static void disableFormLogin(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity.formLogin()
                    .disable();
    }

    static void disableHttpBasic(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity.httpBasic()
                    .disable();
    }

    static void disableLogout(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity.logout()
                    .disable();
    }

    static void disableCSRF(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf()
                    .disable();
    }

	/**
	 * Adds a {@link org.springframework.web.filter.CorsFilter} to be used. If a bean by the name of {@linkplain corsFilter} is provided, that CorsFilter is used.
	 * Else if {@link org.springframework.web.cors.CorsConfigurationSource} is defined, then that {@linkplain CorsConfiguration} is used.
	 * Otherwise, if Spring MVC is on the classpath a {@link org.springframework.web.servlet.handler.HandlerMappingIntrospector} is used.
	 */
	static void addCORSFilter(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors();
    }

    static void frameOptionsSameOrigin(final HttpSecurity httpSecurity) throws Exception {
		httpSecurity.headers()
		            .frameOptions()
		            .sameOrigin();
    }

    static void exceptionHandling(final HttpSecurity httpSecurity, final AuthenticationEntryPoint authenticationEntryPoint) throws Exception {
        httpSecurity.exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint);
    }

    /**
     * Registering custom security filters.
     */
    public static class Filters {
        @SuppressWarnings("DanglingJavadoc")
        static void addTokenAuthenticationFilters(final HttpSecurity httpSecurity, final UserDetailsService userDetailsService,
                                                  final LocaleMessagesCreator localeMessageCreator, final JwtTokenClaimsRetriever jwtTokenClaimsRetriever,
                                                  final JwtTokenValidator jwtTokenValidator, final LocaleMessagesHelper localeMessagesHelper) {
            // JWT filter
            final GenericJwtTokenFilter genericJwtTokenFilter = new GenericJwtTokenFilter(userDetailsService, localeMessageCreator,
                                                                                          jwtTokenClaimsRetriever, jwtTokenValidator);
            httpSecurity.addFilterBefore(genericJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

            /**
             * ServletExceptionHandlerFilter (it is first and allows GenericJwtTokenFilter to process).
             * Catches exceptions thrown by GenericJwtTokenFilter.
             */
            final ServletExceptionHandlerFilter servletExceptionHandlerFilter = new ServletExceptionHandlerFilter();
            httpSecurity.addFilterBefore(servletExceptionHandlerFilter, GenericJwtTokenFilter.class);

            /**
             * This filter is required to determine "Locale" which is needed to create appropriate messages using:
             * {@link me.grudzien.patryk.util.i18n.LocaleMessagesCreator#buildLocaleMessage(String)} or to take right email template inside:
             * {@link me.grudzien.patryk.service.registration.impl.EmailClientServiceImpl#sendMessageUsingTemplate(me.grudzien.patryk.domain.dto.registration.EmailDto)}.
             */
            final LocaleDeterminerFilter localeDeterminerFilter = new LocaleDeterminerFilter(localeMessagesHelper);
            httpSecurity.addFilterBefore(localeDeterminerFilter, ServletExceptionHandlerFilter.class);
        }
    }

    /**
     * Configuring OAuth2 Client.
     */
    static class OAuth2 {
        static void configureOAuth2Client(final HttpSecurity httpSecurity, final PropertiesKeeper propertiesKeeper, final CacheManagerHelper cacheManagerHelper,
                                          final CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler,
                                          final CustomOAuth2AuthenticationFailureHandler customOAuth2AuthenticationFailureHandler,
                                          final CustomOidcUserService customOidcUserService,
                                          final CustomOAuth2UserService customOAuth2UserService) throws Exception {
            httpSecurity.oauth2Login()
                        .loginPage(propertiesKeeper.oAuth2().LOGIN_PAGE)
                        .authorizationEndpoint()
                            .authorizationRequestRepository(new CacheBasedOAuth2AuthorizationRequestRepository(cacheManagerHelper))
                                .and()
                        .successHandler(customOAuth2AuthenticationSuccessHandler)
                        .failureHandler(customOAuth2AuthenticationFailureHandler)
                        .userInfoEndpoint()
                            // OpenID Connect (Google)
                            .oidcUserService(customOidcUserService)
                            // OAuth2 2.0 (Facebook)
                            .userService(customOAuth2UserService);
        }
    }

    /**
     * Configuring Spring Security restricting access against custom endpoints.
     */
    static class Requests {
        static void authorizeRequests(final HttpSecurity httpSecurity, final PropertiesKeeper propertiesKeeper) throws Exception {
	        httpSecurity
                    // creating custom SecurityFilterChain
			        .mvcMatcher(MvcPatterns.API(propertiesKeeper))
			            .authorizeRequests()
                    // creating custom SecurityFilterChain
			        .mvcMatchers(HttpMethod.POST,
			                     MvcPatterns.auth(propertiesKeeper),
			                     MvcPatterns.generateAccessToken(propertiesKeeper),
			                     MvcPatterns.generateRefreshToken(propertiesKeeper),
			                     MvcPatterns.refreshAccessToken(propertiesKeeper))
			            .permitAll()
                    .mvcMatchers(MvcPatterns.registration(propertiesKeeper),
                                 MvcPatterns.userLoggedInUsingGoogle(propertiesKeeper),
                                 MvcPatterns.userLoggedInUsingGoogle(propertiesKeeper),
                                 MvcPatterns.userNotFound(propertiesKeeper),
                                 MvcPatterns.userAccountIsLocked(propertiesKeeper),
                                 MvcPatterns.userIsDisabled(propertiesKeeper),
                                 MvcPatterns.userAccountIsExpired(propertiesKeeper),
                                 MvcPatterns.userRegisteredUsingGoogle(propertiesKeeper),
                                 MvcPatterns.userAccountAlreadyExists(propertiesKeeper),
                                 MvcPatterns.credentialsHaveExpired(propertiesKeeper),
                                 MvcPatterns.jwtTokenNotFound(propertiesKeeper),
                                 MvcPatterns.registrationProviderMismatch(propertiesKeeper),
                                 MvcPatterns.badCredentials(propertiesKeeper),
                                 MvcPatterns.exchangeShortLivedToken(propertiesKeeper),
                                 MvcPatterns.failureTargetUrl(propertiesKeeper))
                        .permitAll()
                    // require authentication via JWT
			        .anyRequest()
			            .authenticated();
        }
    }

    /**
     * Configuring which requests should be ignored.
     */
    static class Web {
        static void configureIgnoredRequests(final WebSecurity webSecurity) {
        	webSecurity
                    .ignoring()
                        .mvcMatchers(MvcPatterns.h2InMemoryWebConsole())
                        .mvcMatchers(HttpMethod.OPTIONS, MvcPatterns.root())
                        .mvcMatchers(HttpMethod.GET, MvcPatterns.staticResources())
	                    .mvcMatchers(HttpMethod.GET, MvcPatterns.UIContextPath());
        }
    }
}