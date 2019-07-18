package me.grudzien.patryk.configuration.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import me.grudzien.patryk.configuration.filters.GenericJwtTokenFilter;
import me.grudzien.patryk.configuration.filters.LocaleDeterminerFilter;
import me.grudzien.patryk.configuration.filters.ServletExceptionHandlerFilter;
import me.grudzien.patryk.jwt.service.JwtTokenClaimsRetriever;
import me.grudzien.patryk.jwt.service.JwtTokenValidator;
import me.grudzien.patryk.oauth2.handler.CustomOAuth2AuthenticationFailureHandler;
import me.grudzien.patryk.oauth2.handler.CustomOAuth2AuthenticationSuccessHandler;
import me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository;
import me.grudzien.patryk.oauth2.service.CustomOAuth2UserService;
import me.grudzien.patryk.oauth2.service.CustomOidcUserService;
import me.grudzien.patryk.oauth2.utils.CacheManagerHelper;
import me.grudzien.patryk.registration.model.dto.EmailDto;
import me.grudzien.patryk.registration.service.impl.EmailClientServiceImpl;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.i18n.LocaleMessagesHelper;
import me.grudzien.patryk.utils.web.MvcPattern;

import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.AUTHENTICATION_RESOURCE_ROOT;
import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.LOGIN;
import static me.grudzien.patryk.configuration.security.SecurityResourceDefinitions.APP_ROOT_CONTEXT;
import static me.grudzien.patryk.configuration.security.SecurityResourceDefinitions.STATIC_RESOURCES;
import static me.grudzien.patryk.jwt.resource.JwtResourceDefinitions.GENERATE_ACCESS_TOKEN;
import static me.grudzien.patryk.jwt.resource.JwtResourceDefinitions.GENERATE_REFRESH_TOKEN;
import static me.grudzien.patryk.jwt.resource.JwtResourceDefinitions.JWT_RESOURCE_ROOT;
import static me.grudzien.patryk.jwt.resource.JwtResourceDefinitions.REFRESH_ACCESS_TOKEN;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.BAD_CREDENTIALS;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.CREDENTIALS_HAVE_EXPIRED;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.EXCHANGE_SHORT_LIVED_TOKEN;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.FAILURE_TARGET_URL;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.GOOGLE_OAUTH2_RESOURCE_ROOT;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.JWT_TOKEN_NOT_FOUND;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.REGISTRATION_PROVIDER_MISMATCH;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_ACCOUNT_ALREADY_EXISTS;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_ACCOUNT_IS_EXPIRED;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_ACCOUNT_IS_LOCKED;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_IS_DISABLED;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_LOGGED_IN_USING_GOOGLE;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_NOT_FOUND;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_REGISTERED_USING_GOOGLE;
import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.REGISTRATION_RESOURCE_ROOT;
import static me.grudzien.patryk.utils.app.ApiVersion.API_VERSION;
import static me.grudzien.patryk.utils.web.FrontendRoutesDefinitions.UI_ROOT_CONTEXT;

@SuppressWarnings("JavadocReference")
public final class SecurityConfigContext {

    private SecurityConfigContext() {
        throw new UnsupportedOperationException("Creating object of this class is not allowed!");
    }

    static void sessionManagementStateless(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    static void httpSessionRequestCache(final HttpSecurity httpSecurity, final HttpSessionRequestCache httpSessionRequestCache) throws Exception {
        httpSecurity.requestCache()
                    .requestCache(httpSessionRequestCache);
    }

    static void disableUnusedSecurityOptions(final HttpSecurity httpSecurity) throws Exception {
        // we are stateless so these things are not needed
        disableFormLogin(httpSecurity);
        disableHttpBasic(httpSecurity);
        disableLogout(httpSecurity);
        // don't need CSRF because JWT token is invulnerable
        disableCSRF(httpSecurity);
        // Disabling frame options
        frameOptionsSameOrigin(httpSecurity);
    }

    private static void disableFormLogin(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity.formLogin()
                    .disable();
    }

    private static void disableHttpBasic(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity.httpBasic()
                    .disable();
    }

    private static void disableLogout(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity.logout()
                    .disable();
    }

    private static void disableCSRF(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf()
                    .disable();
    }

    private static void frameOptionsSameOrigin(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity.headers()
                    .frameOptions()
                    .sameOrigin();
    }

	/**
	 * Adds a {@link org.springframework.web.filter.CorsFilter} to be used. If a bean by the name of {@linkplain corsFilter} is provided, that CorsFilter is used.
	 * Else if {@link org.springframework.web.cors.CorsConfigurationSource} is defined, then that {@linkplain CorsConfiguration} is used.
	 * Otherwise, if Spring MVC is on the classpath a {@link org.springframework.web.servlet.handler.HandlerMappingIntrospector} is used.
	 */
	static void addCORSFilter(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors();
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
             * {@link me.grudzien.patryk.utils.i18n.LocaleMessagesCreator#buildLocaleMessage(String)} or to take right email template inside:
             * {@link EmailClientServiceImpl#sendMessageUsingTemplate(EmailDto)}.
             */
            final LocaleDeterminerFilter localeDeterminerFilter = new LocaleDeterminerFilter(localeMessagesHelper);
            httpSecurity.addFilterBefore(localeDeterminerFilter, ServletExceptionHandlerFilter.class);
        }
    }

    /**
     * Configuring OAuth2 Client.
     */
    static class OAuth2 {
        static void configureOAuth2Client(final HttpSecurity httpSecurity, final CacheManagerHelper cacheManagerHelper,
                                          final CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler,
                                          final CustomOAuth2AuthenticationFailureHandler customOAuth2AuthenticationFailureHandler,
                                          final CustomOidcUserService customOidcUserService,
                                          final CustomOAuth2UserService customOAuth2UserService) throws Exception {
            httpSecurity.oauth2Login()
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
        static void authorizeRequests(final HttpSecurity httpSecurity) throws Exception {
	        httpSecurity
                    // creating custom SecurityFilterChain
			        .mvcMatcher(MvcPattern.of(API_VERSION, "/**"))
			            .authorizeRequests()
                    // creating custom SecurityFilterChain
			        .mvcMatchers(HttpMethod.POST,
                                 MvcPattern.of(AUTHENTICATION_RESOURCE_ROOT, LOGIN, "**"),
                                 MvcPattern.of(JWT_RESOURCE_ROOT, GENERATE_ACCESS_TOKEN),
                                 MvcPattern.of(JWT_RESOURCE_ROOT, GENERATE_REFRESH_TOKEN),
                                 MvcPattern.of(JWT_RESOURCE_ROOT, REFRESH_ACCESS_TOKEN))
			            .permitAll()
                    .mvcMatchers(MvcPattern.of(REGISTRATION_RESOURCE_ROOT, "/**"),
                                 MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, USER_LOGGED_IN_USING_GOOGLE),
                                 MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, USER_NOT_FOUND),
                                 MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, USER_ACCOUNT_IS_LOCKED),
                                 MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, USER_IS_DISABLED),
                                 MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, USER_ACCOUNT_IS_EXPIRED),
                                 MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, USER_REGISTERED_USING_GOOGLE),
                                 MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, USER_ACCOUNT_ALREADY_EXISTS),
                                 MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, CREDENTIALS_HAVE_EXPIRED),
                                 MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, JWT_TOKEN_NOT_FOUND),
                                 MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, REGISTRATION_PROVIDER_MISMATCH),
                                 MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, BAD_CREDENTIALS),
                                 MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, EXCHANGE_SHORT_LIVED_TOKEN),
                                 MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, FAILURE_TARGET_URL))
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
                        .mvcMatchers("/h2-console/**")
                        .mvcMatchers(HttpMethod.OPTIONS, APP_ROOT_CONTEXT)
                        .mvcMatchers(HttpMethod.GET, STATIC_RESOURCES)
	                    .mvcMatchers(HttpMethod.GET, UI_ROOT_CONTEXT);
        }
    }
}
