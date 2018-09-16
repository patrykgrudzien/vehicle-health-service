package me.grudzien.patryk.config.security;

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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.google.common.base.Preconditions;

import static me.grudzien.patryk.PropertiesKeeper.FrontendRoutes;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.config.filters.JwtAuthorizationTokenFilter;
import me.grudzien.patryk.config.filters.ServletExceptionHandlerFilter;
import me.grudzien.patryk.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import me.grudzien.patryk.oauth2.OAuth2AuthenticationSuccessHandler;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.utils.log.LogMarkers;

@Log4j2
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
	private final PropertiesKeeper propertiesKeeper;

	/**
	 * @Qualifier for {@link org.springframework.security.core.userdetails.UserDetailsService} is used here because there is also
	 * {@link org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration} available and Spring
	 * does not know that I want to use my custom implementation.
	 */
	@Autowired
	public SecurityConfig(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
	                      final CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
	                      final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
	                      final PropertiesKeeper propertiesKeeper) {

		Preconditions.checkNotNull(userDetailsService, "userDetailsService cannot be null!");
		Preconditions.checkNotNull(customAuthenticationEntryPoint, "customAuthenticationEntryPoint cannot be null!");
		Preconditions.checkNotNull(oAuth2AuthenticationSuccessHandler, "oAuth2AuthenticationSuccessHandler cannot be null!");
		Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");

		this.userDetailsService = userDetailsService;
		this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
		this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
		this.propertiesKeeper = propertiesKeeper;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	protected void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		log.info(LogMarkers.FLOW_MARKER, "Authentication Provider configured globally.");
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

	@Override
	protected void configure(final HttpSecurity httpSecurity) throws Exception {
		// don't create session - set creation policy to STATELESS
		sessionCreationPolicy(httpSecurity);

		// we are stateless so "/logout" endpoint not needed
		logout(httpSecurity);

		// show message to the user that some resource requires authentication
		exceptionHandling(httpSecurity, customAuthenticationEntryPoint);

		/**
		 * {@link me.grudzien.patryk.config.filters.JwtAuthorizationTokenFilter}
		 * &&
		 * {@link me.grudzien.patryk.config.filters.ServletExceptionHandlerFilter}
		 */
		tokenAuthentication(httpSecurity, userDetailsService());

		// don't need CSRF because JWT token is invulnerable
		csrf(httpSecurity);

		// CORS configuration
		cors(httpSecurity);

		// oauth2 clients
		oauth2Client(httpSecurity, oAuth2AuthenticationSuccessHandler);

		// mvcMatchers
		authorizeRequests(httpSecurity);
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
                .mvcMatchers(HttpMethod.GET,
                             "/",
                             "/favicon.ico",
                             "/static/**",
                             "/static/*.html",
                             "/static/*.jpg",
                             "/static/css/**",
                             "/static/js/**")
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

	private void sessionCreationPolicy(final HttpSecurity httpSecurity) throws Exception {
		httpSecurity.sessionManagement()
		            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	private void logout(final HttpSecurity httpSecurity) throws Exception {
		httpSecurity.logout()
		            .disable();
	}

	private void csrf(final HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf()
		            .disable();
	}

	private void cors(final HttpSecurity httpSecurity) throws Exception {
		httpSecurity.cors();
	}

	private void exceptionHandling(final HttpSecurity httpSecurity, final CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
		httpSecurity.exceptionHandling()
		            .authenticationEntryPoint(customAuthenticationEntryPoint);
	}

	private void tokenAuthentication(final HttpSecurity httpSecurity, final UserDetailsService userDetailsService) {
		// JWT filter
		final JwtAuthorizationTokenFilter authorizationTokenFilter = new JwtAuthorizationTokenFilter(userDetailsService, propertiesKeeper);
		httpSecurity.addFilterBefore(authorizationTokenFilter, UsernamePasswordAuthenticationFilter.class);

		// ServletExceptionHandlerFilter (it is first and allows JwtAuthorizationTokenFilter to process).
		// Catches exceptions thrown by JwtAuthorizationTokenFilter.
		final ServletExceptionHandlerFilter servletExceptionHandlerFilter = new ServletExceptionHandlerFilter();
		httpSecurity.addFilterBefore(servletExceptionHandlerFilter, JwtAuthorizationTokenFilter.class);

		/**
		 * There is also another filter {@link me.grudzien.patryk.config.filters.LocaleDeterminerFilter} which is registered in
		 * {@link me.grudzien.patryk.config.filters.registry.FiltersRegistryConfig#registerLocaleDeterminerFilter()}
		 * to disable Spring Security on some endpoints like: "/auth", "/registration".
		 * This filter is required to determine "Locale" which is needed to create appropriate messages using:
		 * {@link me.grudzien.patryk.utils.i18n.LocaleMessagesCreator#buildLocaleMessage(String)} or to take right email template inside:
		 * {@link me.grudzien.patryk.service.registration.impl.EmailServiceImpl#sendMessageUsingTemplate(me.grudzien.patryk.domain.dto.registration.EmailDto)}.
		 */
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
				// require authentication via JWT
				.anyRequest().authenticated();
	}

	private void oauth2Client(final HttpSecurity httpSecurity, final AuthenticationSuccessHandler authenticationSuccessHandler) throws Exception {
		httpSecurity.oauth2Login()
		            .loginPage(propertiesKeeper.oAuth2().LOGIN_PAGE)
		            .authorizationEndpoint()
		            .authorizationRequestRepository(new HttpCookieOAuth2AuthorizationRequestRepository(propertiesKeeper))
		            .and()
		            .successHandler(authenticationSuccessHandler);
	}
}
