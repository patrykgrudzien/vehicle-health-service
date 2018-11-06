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
import me.grudzien.patryk.oauth2.handlers.CustomOAuth2AuthenticationFailureHandler;
import me.grudzien.patryk.oauth2.handlers.CustomOAuth2AuthenticationSuccessHandler;
import me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository;
import me.grudzien.patryk.oauth2.service.CustomOAuth2UserService;
import me.grudzien.patryk.oauth2.service.CustomOidcUserService;
import me.grudzien.patryk.oauth2.utils.CacheHelper;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

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
             * {@link me.grudzien.patryk.utils.i18n.LocaleMessagesCreator#buildLocaleMessage(String)} or to take right email template inside:
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
                    // allow calls for request methods of "OPTIONS" type -> (CORS purpose) without checking JWT token
                    // (this helps to avoid duplicate calls before the specific ones)
                    .mvcMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    // /auth
                    .mvcMatchers(HttpMethod.POST, propertiesKeeper.endpoints().REFRESH_TOKEN).permitAll()
                    // /auth/**
                    .mvcMatchers(HttpMethod.POST, propertiesKeeper.endpoints().AUTH + "/**").permitAll()
                    // /registration/**  (/register-user-account)
                    .mvcMatchers(HttpMethod.POST, propertiesKeeper.endpoints().REGISTRATION + "/**").permitAll()
                    // /registration/**
                    .mvcMatchers(HttpMethod.GET, propertiesKeeper.endpoints().REGISTRATION + "/**").permitAll()
                    // /refresh-token
                    .mvcMatchers(HttpMethod.POST, propertiesKeeper.endpoints().REFRESH_TOKEN).permitAll()
                    // //user-logged-in-using-google**
                    .mvcMatchers(propertiesKeeper.oAuth2().USER_LOGGED_IN_USING_GOOGLE + "**").permitAll()
                    // /user-not-found
                    .mvcMatchers(propertiesKeeper.oAuth2().USER_NOT_FOUND + "**").permitAll()
                    // /user-is-disabled
                    .mvcMatchers(propertiesKeeper.oAuth2().USER_IS_DISABLED + "**").permitAll()
                    // /user-registered-using-google
                    .mvcMatchers(propertiesKeeper.oAuth2().USER_REGISTERED_USING_GOOGLE + "**").permitAll()
                    // /user-account-already-exists
                    .mvcMatchers(propertiesKeeper.oAuth2().USER_ACCOUNT_ALREADY_EXISTS + "**").permitAll()
                    // /failure-target-url
                    .mvcMatchers(propertiesKeeper.oAuth2().FAILURE_TARGET_URL + "**").permitAll()
                    // require authentication via JWT
                    .anyRequest().authenticated();
        }
    }

    /**
     * Configuring which requests should be ignored.
     */
    static class Web {
        static void configureIgnoredRequests(final WebSecurity webSecurity, final PropertiesKeeper propertiesKeeper) {
            webSecurity.ignoring()
                            // allow calls for request methods of "OPTIONS" type -> (CORS purpose) without checking JWT token
                            // (this helps to avoid duplicate calls before the specific ones)
                            .mvcMatchers(HttpMethod.OPTIONS, "/**")
                                .and()
                       .ignoring()
                            // /auth
                            .mvcMatchers(HttpMethod.POST, propertiesKeeper.endpoints().AUTH)
                                .and()
                        .ignoring()
                            // /auth/**
                            .mvcMatchers(HttpMethod.POST, propertiesKeeper.endpoints().AUTH + "/**")
                                .and()
                        .ignoring()
                            // /registration/**  (/register-user-account)
                            .mvcMatchers(HttpMethod.POST, propertiesKeeper.endpoints().REGISTRATION + "/**")
                                .and()
                        .ignoring()
                            // /registration/**
                            .mvcMatchers(HttpMethod.GET, propertiesKeeper.endpoints().REGISTRATION + "/**")
                                .and()
                        .ignoring()
                            // /refresh-token
                            .mvcMatchers(HttpMethod.POST, propertiesKeeper.endpoints().REFRESH_TOKEN)
                                .and()
                        .ignoring()
                            .mvcMatchers(HttpMethod.GET, StaticResources.ALL)
                                .and()
                        .ignoring()
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
