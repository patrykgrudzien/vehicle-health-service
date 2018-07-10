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

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.handlers.security.CustomAuthenticationEntryPoint;
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
	 * {@link org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure
	 * (org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder)}
	 */
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(final HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				// don't need CSRF because JWT token is invulnerable
				.csrf().disable()
				// show message to the user that some resource requires authentication
				.exceptionHandling()
					.authenticationEntryPoint(customAuthenticationEntryPoint)
                        .and()
                // don't create session
                .sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .and()
                // filters
		        .authorizeRequests()
					// allow calls for request methods of "OPTIONS" type -> (CORS purpose) without checking JWT token
					// (this helps to avoid duplicate calls before the specific ones)
					.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
					// /auth
					.antMatchers(HttpMethod.POST, customApplicationProperties.getEndpoints().getAuthentication().getRoot()).permitAll()
					// /auth/**
					.antMatchers(HttpMethod.POST, customApplicationProperties.getEndpoints().getAuthentication().getRoot() + "/**").permitAll()
			        // /registration/**  (/register-user-account)
			        .antMatchers(HttpMethod.POST, customApplicationProperties.getEndpoints().getRegistration().getRoot() + "/**").permitAll()
					// /registration/**
					.antMatchers(HttpMethod.GET, customApplicationProperties.getEndpoints().getRegistration().getRoot() + "/**").permitAll()
			        // require authentication via JWT
					.anyRequest().authenticated();

		// JWT filter
		final JwtAuthorizationTokenFilter authorizationTokenFilter = new JwtAuthorizationTokenFilter(userDetailsService(), customApplicationProperties);
		httpSecurity.addFilterBefore(authorizationTokenFilter, UsernamePasswordAuthenticationFilter.class);

		// ServletExceptionHandlerFilter (it is first and allows JwtAuthorizationTokenFilter to process).
		// Catches exceptions thrown by JwtAuthorizationTokenFilter.
		final ServletExceptionHandlerFilter servletExceptionHandlerFilter = new ServletExceptionHandlerFilter();
		httpSecurity.addFilterBefore(servletExceptionHandlerFilter, JwtAuthorizationTokenFilter.class);

		/**
		 * There is also another filter {@link me.grudzien.patryk.config.i18n.LocaleDeterminerFilter} which is marked as
		 * Spring @Component (in such case there is no need to register it here before one of any existing filter) ->
		 * It's incorrect behavior and not managed by Spring Security but it's done by purpose because some resources do not
		 * require Security check like: "/auth", "/registration" and no filter will be invoked but "Locale" needs to be determined
		 * to create appropriate message inside:
		 * {@link me.grudzien.patryk.utils.i18n.LocaleMessagesCreator#buildLocaleMessage(String)} or to take right email
		 * template inside:
		 * {@link me.grudzien.patryk.service.registration.EmailServiceImpl#sendMessageUsingTemplate(me.grudzien.patryk.domain.dto.registration.EmailDto)}
		 * that's why {@link me.grudzien.patryk.config.i18n.LocaleDeterminerFilter} will be invoked on each request.
		 */
	}

	@Override
	public void configure(final WebSecurity web) {
		// AuthenticationTokenFilter will ignore the below paths
		web.ignoring()
		        // allow calls for request methods of "OPTIONS" type -> (CORS purpose) without checking JWT token
		        // (this helps to avoid duplicate calls before the specific ones)
		        .antMatchers(HttpMethod.OPTIONS, "/**")
		            .and()
		   .ignoring()
		        // /auth
		        .antMatchers(HttpMethod.POST, customApplicationProperties.getEndpoints().getAuthentication().getRoot())
					.and()
		    .ignoring()
		        // /auth/**
		        .antMatchers(HttpMethod.POST, customApplicationProperties.getEndpoints().getAuthentication().getRoot() + "/**")
		            .and()
			.ignoring()
		        // /registration/**  (/register-user-account)
				.antMatchers(HttpMethod.POST, customApplicationProperties.getEndpoints().getRegistration().getRoot() + "/**")
					.and()
			.ignoring()
		        // /registration/**
				.antMatchers(HttpMethod.GET, customApplicationProperties.getEndpoints().getRegistration().getRoot() + "/**")
					.and()
			.ignoring()
                .antMatchers(HttpMethod.GET,
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
				.antMatchers(HttpMethod.GET,
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
}
