package me.grudzien.patryk.config.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.PropertiesKeeper.FrontendRoutes;
import me.grudzien.patryk.PropertiesKeeper.StaticResources;
import me.grudzien.patryk.config.filters.GenericJwtTokenFilter;
import me.grudzien.patryk.config.filters.ServletExceptionHandlerFilter;
import me.grudzien.patryk.oauth2.handler.CustomOAuth2AuthenticationFailureHandler;
import me.grudzien.patryk.oauth2.handler.CustomOAuth2AuthenticationSuccessHandler;
import me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository;
import me.grudzien.patryk.oauth2.service.CustomOAuth2UserService;
import me.grudzien.patryk.oauth2.service.CustomOidcUserService;
import me.grudzien.patryk.oauth2.util.CacheHelper;
import me.grudzien.patryk.util.i18n.LocaleMessagesCreator;

final class SecurityConfigContext {

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
    static class Filters {
        static void addTokenAuthenticationFilters(final HttpSecurity httpSecurity, final UserDetailsService userDetailsService,
                                                  final PropertiesKeeper propertiesKeeper, final LocaleMessagesCreator localeMessageCreator) {
            // JWT filter
            final GenericJwtTokenFilter genericJwtTokenFilter = new GenericJwtTokenFilter(userDetailsService, propertiesKeeper, localeMessageCreator);
            httpSecurity.addFilterBefore(genericJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

            // ServletExceptionHandlerFilter (it is first and allows GenericJwtTokenFilter to process).
            // Catches exceptions thrown by GenericJwtTokenFilter.
            final ServletExceptionHandlerFilter servletExceptionHandlerFilter = new ServletExceptionHandlerFilter();
            httpSecurity.addFilterBefore(servletExceptionHandlerFilter, GenericJwtTokenFilter.class);

            /**
             * There is also another filter {@link me.grudzien.patryk.config.filters.LocaleDeterminerFilter} which is registered in
             * {@link me.grudzien.patryk.config.filters.registry.FiltersRegistryConfig#registerLocaleDeterminerFilter()}
             * to disable Spring Security on some endpoints like:
             * 1) "/auth"
             * 2) "/registration"
             * This filter is required to determine "Locale" which is needed to create appropriate messages using:
             * {@link me.grudzien.patryk.util.i18n.LocaleMessagesCreator#buildLocaleMessage(String)} or to take right email template inside:
             * {@link me.grudzien.patryk.service.registration.impl.EmailServiceImpl#sendMessageUsingTemplate(me.grudzien.patryk.domain.dto.registration.EmailDto)}.
             */
        }
    }

    /**
     * Configuring OAuth2 Client.
     */
    static class OAuth2 {
        static void configureOAuth2Client(final HttpSecurity httpSecurity, final PropertiesKeeper propertiesKeeper, final CacheHelper cacheHelper,
                                          final CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler,
                                          final CustomOAuth2AuthenticationFailureHandler customOAuth2AuthenticationFailureHandler,
                                          final CustomOidcUserService customOidcUserService,
                                          final CustomOAuth2UserService customOAuth2UserService) throws Exception {
            httpSecurity.oauth2Login()
                        .loginPage(propertiesKeeper.oAuth2().LOGIN_PAGE)
                        .authorizationEndpoint()
                            .authorizationRequestRepository(new CacheBasedOAuth2AuthorizationRequestRepository(cacheHelper))
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
                    .authorizeRequests()
                    .mvcMatchers(HttpMethod.OPTIONS, MvcPatterns.homeWildcard()).permitAll()
                    .mvcMatchers(HttpMethod.POST, MvcPatterns.auth(propertiesKeeper)).permitAll()
                    .mvcMatchers(HttpMethod.POST, MvcPatterns.authWildcard(propertiesKeeper)).permitAll()
                    .mvcMatchers(HttpMethod.POST, MvcPatterns.registrationWildcard(propertiesKeeper)).permitAll()
                    .mvcMatchers(HttpMethod.GET, MvcPatterns.registrationWildcard(propertiesKeeper)).permitAll()
                    .mvcMatchers(HttpMethod.POST, MvcPatterns.refreshToken(propertiesKeeper)).permitAll()
                    .mvcMatchers(MvcPatterns.userLoggedInUsingGoogleWildcard(propertiesKeeper)).permitAll()
                    .mvcMatchers(MvcPatterns.userNotFoundWildcard(propertiesKeeper)).permitAll()
                    .mvcMatchers(MvcPatterns.userIsDisabledWildcard(propertiesKeeper)).permitAll()
                    .mvcMatchers(MvcPatterns.userRegisteredUsingGoogleWildcard(propertiesKeeper)).permitAll()
                    .mvcMatchers(MvcPatterns.userAccountAlreadyExistsWildcard(propertiesKeeper)).permitAll()
                    .mvcMatchers(MvcPatterns.failureTargetUrlWildcard(propertiesKeeper)).permitAll()
                    // require authentication via JWT
                    .anyRequest().authenticated();
        }
    }

    /**
     * Configuring which requests should be ignored.
     */
    static class Web {
        static void configureIgnoredRequests(final WebSecurity webSecurity, final PropertiesKeeper propertiesKeeper) {
            webSecurity
                    .ignoring()
                        .mvcMatchers(HttpMethod.OPTIONS, MvcPatterns.homeWildcard())
                        .mvcMatchers(HttpMethod.POST, MvcPatterns.auth(propertiesKeeper))
                        .mvcMatchers(HttpMethod.POST, MvcPatterns.authWildcard(propertiesKeeper))
                        .mvcMatchers(HttpMethod.POST, MvcPatterns.registrationWildcard(propertiesKeeper))
                        .mvcMatchers(HttpMethod.GET, MvcPatterns.registrationWildcard(propertiesKeeper))
                        .mvcMatchers(HttpMethod.POST, MvcPatterns.refreshToken(propertiesKeeper))
                        // mvcMatchers() here doesn't want to work
                        .antMatchers(MvcPatterns.h2InMemoryWebConsole())
                        .mvcMatchers(HttpMethod.GET, StaticResources.ALL)
                        .mvcMatchers(HttpMethod.GET,
                                     FrontendRoutes.ABOUT_ME,
                                     FrontendRoutes.REGISTRATION_FORM,
                                     FrontendRoutes.REGISTRATION_CONFIRMED,
                                     FrontendRoutes.REGISTRATION_CONFIRMED_WILDCARD,
                                     FrontendRoutes.LOGIN,
                                     FrontendRoutes.MAIN_BOARD,
                                     FrontendRoutes.MAIN_BOARD_WILDCARD,
                                     FrontendRoutes.AUTHENTICATION_REQUIRED);
        }
    }
}
