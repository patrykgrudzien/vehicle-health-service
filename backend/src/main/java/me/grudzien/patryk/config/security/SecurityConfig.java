package me.grudzien.patryk.config.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import com.google.common.base.Preconditions;

import static me.grudzien.patryk.PropertiesKeeper.FrontendRoutes;
import static me.grudzien.patryk.PropertiesKeeper.StaticResources;
import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.config.filters.JwtTokenSpringAuthenticationManagerFilter;
import me.grudzien.patryk.config.filters.ServletExceptionHandlerFilter;
import me.grudzien.patryk.oauth2.authentication.JwtAuthenticationProvider;
import me.grudzien.patryk.oauth2.authentication.JwtTokenCustomAuthenticationManagerFilter;
import me.grudzien.patryk.oauth2.handlers.CustomOAuth2AuthenticationFailureHandler;
import me.grudzien.patryk.oauth2.handlers.CustomOAuth2AuthenticationSuccessHandler;
import me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository;
import me.grudzien.patryk.oauth2.service.CustomOAuth2UserService;
import me.grudzien.patryk.oauth2.service.CustomOidcUserService;
import me.grudzien.patryk.oauth2.utils.CacheHelper;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

@Log4j2
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler;
	private final CustomOAuth2AuthenticationFailureHandler customOAuth2AuthenticationFailureHandler;
	private final CustomOidcUserService customOidcUserService;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final PropertiesKeeper propertiesKeeper;
	private final CacheHelper cacheHelper;
	private final LocaleMessagesCreator localeMessageCreator;

	private final JwtAuthenticationProvider jwtAuthenticationProvider;

	/**
	 * @Qualifier for {@link org.springframework.security.core.userdetails.UserDetailsService} is used here because there is also
	 * {@link org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration} available and Spring
	 * does not know that I want to use my custom implementation.
	 */
	@Autowired
	public SecurityConfig(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
	                      final CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
	                      final CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler,
	                      final CustomOAuth2AuthenticationFailureHandler customOAuth2AuthenticationFailureHandler, final CustomOidcUserService customOidcUserService,
	                      final CustomOAuth2UserService customOAuth2UserService, final PropertiesKeeper propertiesKeeper, final CacheHelper cacheHelper,
	                      final LocaleMessagesCreator localeMessageCreator, final JwtAuthenticationProvider jwtAuthenticationProvider) {

		Preconditions.checkNotNull(userDetailsService, "userDetailsService cannot be null!");
		Preconditions.checkNotNull(customAuthenticationEntryPoint, "customAuthenticationEntryPoint cannot be null!");
		Preconditions.checkNotNull(customOAuth2AuthenticationSuccessHandler, "customOAuth2AuthenticationSuccessHandler cannot be null!");
		Preconditions.checkNotNull(customOAuth2AuthenticationFailureHandler, "customOAuth2AuthenticationFailureHandler cannot be null!");
		Preconditions.checkNotNull(customOidcUserService, "customOidcUserService cannot be null!");
		Preconditions.checkNotNull(customOAuth2UserService, "customOAuth2UserService cannot be null!");
		Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");
		Preconditions.checkNotNull(cacheHelper, "cacheHelper cannot be null!");
		Preconditions.checkNotNull(localeMessageCreator, "localeMessageCreator cannot be null!");

		this.userDetailsService = userDetailsService;
		this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
		this.customOAuth2AuthenticationSuccessHandler = customOAuth2AuthenticationSuccessHandler;
		this.customOAuth2AuthenticationFailureHandler = customOAuth2AuthenticationFailureHandler;
		this.customOidcUserService = customOidcUserService;
		this.customOAuth2UserService = customOAuth2UserService;
		this.propertiesKeeper = propertiesKeeper;
		this.cacheHelper = cacheHelper;
		this.localeMessageCreator = localeMessageCreator;
		this.jwtAuthenticationProvider = jwtAuthenticationProvider;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	protected void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService)
		    .passwordEncoder(this.passwordEncoder())
		    .and()
		    .authenticationProvider(jwtAuthenticationProvider);
		log.info(FLOW_MARKER, "Authentication Provider configured globally.");
	}

	/**
	 * This method is overriden to expose the {@link org.springframework.security.authentication.AuthenticationManager} bean from
	 * {@link WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder)}
	 */
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	private HttpSessionRequestCache httpSessionRequestCache() {
		final HttpSessionRequestCache httpSessionRequestCache = new HttpSessionRequestCache();
		httpSessionRequestCache.setCreateSessionAllowed(Boolean.FALSE);
		return httpSessionRequestCache;
	}

	@Override
	protected void configure(final HttpSecurity httpSecurity) throws Exception {
		// don't create session - set creation policy to STATELESS
		sessionCreationPolicy(httpSecurity);
		httpSessionRequestCache(httpSecurity, httpSessionRequestCache());

		// we are stateless so these things are not needed
		disableFormLogin(httpSecurity);
		disableHttpBasic(httpSecurity);
		disableLogout(httpSecurity);

		// show message to the user that some resource requires authentication
		exceptionHandling(httpSecurity, customAuthenticationEntryPoint);

		/**
		 * {@link me.grudzien.patryk.config.filters.JwtTokenSpringAuthenticationManagerFilter}
		 * &&
		 * {@link me.grudzien.patryk.oauth2.authentication.JwtTokenCustomAuthenticationManagerFilter}
		 * &&
		 * {@link me.grudzien.patryk.config.filters.ServletExceptionHandlerFilter}
		 */
		addTokenAuthenticationFilters(httpSecurity, localeMessageCreator, AuthenticationProvider.CUSTOM);

		// don't need CSRF because JWT token is invulnerable
		disableCSRF(httpSecurity);

		// CORS configuration
		addCORSFilter(httpSecurity);

		// oauth2 clients
		configureOAuth2Client(httpSecurity, cacheHelper);

		// mvcMatchers
		authorizeRequests(httpSecurity);
	}


	private void sessionCreationPolicy(final HttpSecurity httpSecurity) throws Exception {
		httpSecurity.sessionManagement()
		            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	private void httpSessionRequestCache(final HttpSecurity httpSecurity, final RequestCache requestCache) throws Exception {
		httpSecurity.requestCache()
		            .requestCache(requestCache);
	}

	private void disableLogout(final HttpSecurity httpSecurity) throws Exception {
		httpSecurity.logout()
		            .disable();
	}

	private void disableFormLogin(final HttpSecurity httpSecurity) throws Exception {
		httpSecurity.formLogin()
		            .disable();
	}

	private void disableHttpBasic(final HttpSecurity httpSecurity) throws Exception {
		httpSecurity.httpBasic()
		            .disable();
	}

	private void disableCSRF(final HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf()
		            .disable();
	}

	private void addCORSFilter(final HttpSecurity httpSecurity) throws Exception {
		httpSecurity.cors();
	}

	private void exceptionHandling(final HttpSecurity httpSecurity, final CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
		httpSecurity.exceptionHandling()
		            .authenticationEntryPoint(customAuthenticationEntryPoint);
	}

	@Getter
	@AllArgsConstructor
	public enum AuthenticationProvider {
		SPRING(Boolean.FALSE), CUSTOM(Boolean.FALSE);

		@Setter
		private boolean isActive;
	}

	private void addTokenAuthenticationFilters(final HttpSecurity httpSecurity, final LocaleMessagesCreator localeMessagesCreator,
	                                           final AuthenticationProvider authenticationProvider) throws Exception {

		final ServletExceptionHandlerFilter servletExceptionHandlerFilter = new ServletExceptionHandlerFilter();

		switch (authenticationProvider) {
			case SPRING:
				AuthenticationProvider.SPRING.setActive(Boolean.TRUE);
				// JWT filter with Spring based Authentication Manager
				final JwtTokenSpringAuthenticationManagerFilter authorizationTokenFilter = new JwtTokenSpringAuthenticationManagerFilter(super.userDetailsService(),
				                                                                                                                         propertiesKeeper,
				                                                                                                                         localeMessagesCreator);
				httpSecurity.addFilterBefore(authorizationTokenFilter, UsernamePasswordAuthenticationFilter.class);

				// ServletExceptionHandlerFilter (it is first and allows JwtTokenSpringAuthenticationManagerFilter to process).
				// Catches exceptions thrown by JwtTokenSpringAuthenticationManagerFilter.
				httpSecurity.addFilterBefore(servletExceptionHandlerFilter, JwtTokenSpringAuthenticationManagerFilter.class);

				/**
				 * There is also another filter {@link me.grudzien.patryk.config.filters.LocaleDeterminerFilter} which is registered in
				 * {@link me.grudzien.patryk.config.filters.registry.FiltersRegistryConfig#registerLocaleDeterminerFilter()}
				 * to disable Spring Security on some endpoints like: "/auth", "/registration".
				 * This filter is required to determine "Locale" which is needed to create appropriate messages using:
				 * {@link me.grudzien.patryk.utils.i18n.LocaleMessagesCreator#buildLocaleMessage(String)} or to take right email template inside:
				 * {@link me.grudzien.patryk.service.registration.impl.EmailServiceImpl#sendMessageUsingTemplate(me.grudzien.patryk.domain.dto.registration.EmailDto)}.
				 */
				break;
			case CUSTOM:
				AuthenticationProvider.CUSTOM.setActive(Boolean.TRUE);
				// JWT filter with custom Authentication Manager
				httpSecurity.addFilterBefore(new JwtTokenCustomAuthenticationManagerFilter(this.authenticationManagerBean(), propertiesKeeper),
				                             UsernamePasswordAuthenticationFilter.class);

				// ServletExceptionHandlerFilter (it is first and allows JwtTokenCustomAuthenticationManagerFilter to process).
				// Catches exceptions thrown by JwtTokenCustomAuthenticationManagerFilter.
				httpSecurity.addFilterBefore(servletExceptionHandlerFilter, JwtTokenCustomAuthenticationManagerFilter.class);
				break;
		}
	}

	private void configureOAuth2Client(final HttpSecurity httpSecurity, final CacheHelper cacheHelper) throws Exception {
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

	private void authorizeRequests(final HttpSecurity httpSecurity) throws Exception {
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
				// /user-registered-using-google
				.mvcMatchers(propertiesKeeper.oAuth2().USER_REGISTERED_USING_GOOGLE + "**").permitAll()
				// /user-account-already-exists
				.mvcMatchers(propertiesKeeper.oAuth2().USER_ACCOUNT_ALREADY_EXISTS + "**").permitAll()
				// /failure-target-url
				.mvcMatchers(propertiesKeeper.oAuth2().FAILURE_TARGET_URL + "**").permitAll()
				// require authentication via JWT
				.anyRequest().authenticated();
	}

	@Override
	public void configure(final WebSecurity web) {
		// AuthenticationTokenFilter will ignore the below paths
		web.ignoring()
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
