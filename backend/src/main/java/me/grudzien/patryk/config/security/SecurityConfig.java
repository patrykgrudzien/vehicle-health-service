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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.google.common.base.Preconditions;

import static me.grudzien.patryk.Constants.Endpoints.AUTH;
import static me.grudzien.patryk.Constants.Endpoints.REFRESH_TOKEN;
import static me.grudzien.patryk.Constants.Endpoints.REGISTRATION;

import me.grudzien.patryk.Constants;
import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.config.filters.JwtAuthorizationTokenFilter;
import me.grudzien.patryk.config.filters.ServletExceptionHandlerFilter;
import me.grudzien.patryk.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.utils.log.LogMarkers;

@Log4j2
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
	private final CustomApplicationProperties customApplicationProperties;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	/**
	 * @Qualifier for {@link org.springframework.security.core.userdetails.UserDetailsService} is used here because there is also
	 * {@link org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration} available and Spring
	 * does not know that I want to use my custom implementation.
	 */
	@Autowired
	public SecurityConfig(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
	                      final CustomApplicationProperties customApplicationProperties,
	                      final CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {

		Preconditions.checkNotNull(userDetailsService, "userDetailsService cannot be null!");
		Preconditions.checkNotNull(customApplicationProperties, "customApplicationProperties cannot be null!");
		Preconditions.checkNotNull(customAuthenticationEntryPoint, "customAuthenticationEntryPoint cannot be null!");

		this.userDetailsService = userDetailsService;
		this.customApplicationProperties = customApplicationProperties;
		this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
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
		HttpSecurityConfigurer.sessionCreationPolicy(httpSecurity);

		// we are stateless so "/logout" endpoint not needed
		HttpSecurityConfigurer.logout(httpSecurity);

		// show message to the user that some resource requires authentication
		HttpSecurityConfigurer.exceptionHandling(httpSecurity, customAuthenticationEntryPoint);

		// JWT filter
		final JwtAuthorizationTokenFilter authorizationTokenFilter = new JwtAuthorizationTokenFilter(userDetailsService(), customApplicationProperties);
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

		// don't need CSRF because JWT token is invulnerable
		HttpSecurityConfigurer.csrf(httpSecurity);

		// CORS configuration
		HttpSecurityConfigurer.cors(httpSecurity);

		// oauth2 clients
		HttpSecurityConfigurer.oauth2Client(httpSecurity, customApplicationProperties);

		// mvcMatchers
		HttpSecurityConfigurer.authorizeRequests(httpSecurity, customApplicationProperties);
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
		        .mvcMatchers(HttpMethod.POST, customApplicationProperties.getEndpoints().getAuthentication().getRoot())
					.and()
		    .ignoring()
		        // /auth/**
		        .mvcMatchers(HttpMethod.POST, customApplicationProperties.getEndpoints().getAuthentication().getRoot() + "/**")
		            .and()
			.ignoring()
		        // /registration/**  (/register-user-account)
				.mvcMatchers(HttpMethod.POST, customApplicationProperties.getEndpoints().getRegistration().getRoot() + "/**")
					.and()
			.ignoring()
		        // /registration/**
				.mvcMatchers(HttpMethod.GET, customApplicationProperties.getEndpoints().getRegistration().getRoot() + "/**")
					.and()
		   .ignoring()
		        // /refresh-token
		        .mvcMatchers(HttpMethod.POST, customApplicationProperties.getEndpoints().getAuthentication().getRefreshToken())
		            .and()
			.ignoring()
                .mvcMatchers(HttpMethod.GET,
                             "/",
                             "/favicon.ico",
                             "/static/**",
                             "/static/*.html",
                             "/static/engine-1.jpg",
                             "/static/engine-2.jpg",
                             "/static/engine-3.jpg",
                             "/static/engine-4.jpg",
                             "/static/engine-5.jpg",
                             "/static/css/**",
                             "/static/js/**")
					.and()
			.ignoring()
				.mvcMatchers(HttpMethod.GET,
				             "/about-me",
				             "/registration-form",
				             "/registration-confirmed",
				             "/registration-confirmed/**",
				             "/server-health",
				             "/login",
				             "/main-board",
				             "/main-board/**",
				             "/authentication-required");
	}

	private static final class HttpSecurityConfigurer {

		static void sessionCreationPolicy(final HttpSecurity httpSecurity) throws Exception {
			httpSecurity.sessionManagement()
			            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		}

		static void logout(final HttpSecurity httpSecurity) throws Exception {
			httpSecurity.logout()
			            .disable();
		}

		static void csrf(final HttpSecurity httpSecurity) throws Exception {
			httpSecurity.csrf()
			            .disable();
		}

		static void cors(final HttpSecurity httpSecurity) throws Exception {
			httpSecurity.cors();
		}

		static void exceptionHandling(final HttpSecurity httpSecurity, final CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
			httpSecurity.exceptionHandling()
			            .authenticationEntryPoint(customAuthenticationEntryPoint);
		}

		static void authorizeRequests(final HttpSecurity httpSecurity, final CustomApplicationProperties customApplicationProperties) throws Exception {
			httpSecurity
					.authorizeRequests()
					// allow calls for request methods of "OPTIONS" type -> (CORS purpose) without checking JWT token
					// (this helps to avoid duplicate calls before the specific ones)
					.mvcMatchers(HttpMethod.OPTIONS, "/**").permitAll()
					// /auth
					.mvcMatchers(HttpMethod.POST, AUTH).permitAll()
					// /auth/**
					.mvcMatchers(HttpMethod.POST, AUTH + "/**").permitAll()
					// /registration/**  (/register-user-account)
					.mvcMatchers(HttpMethod.POST, REGISTRATION + "/**").permitAll()
					// /registration/**
					.mvcMatchers(HttpMethod.GET, REGISTRATION + "/**").permitAll()
					// /refresh-token
					.mvcMatchers(HttpMethod.POST, REFRESH_TOKEN).permitAll()
					// require authentication via JWT
					.anyRequest().authenticated();
		}

		static void oauth2Client(final HttpSecurity httpSecurity, final CustomApplicationProperties customApplicationProperties) throws Exception {
			httpSecurity.oauth2Login()
			            .loginPage(Constants.OAuth2.LOGIN_PAGE)
			            .authorizationEndpoint()
							.authorizationRequestRepository(new HttpCookieOAuth2AuthorizationRequestRepository(customApplicationProperties));
		}
	}
}
