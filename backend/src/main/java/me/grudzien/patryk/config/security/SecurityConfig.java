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

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(final HttpSecurity httpSecurity) throws Exception {
		log.info(LogMarkers.FLOW_MARKER, "Inside Spring Security >>>> HttpSecurity configuration.");

		httpSecurity
				// development purpose (no existing on production "Heroku")
				.cors().and()
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
			        // /registration/**
			        .antMatchers(customApplicationProperties.getEndpoints().getRegistration().getRoot() + "/**").permitAll()
			        // /auth/**
			        .antMatchers(customApplicationProperties.getEndpoints().getAuthentication().getRoot() + "/**").permitAll()
			        // require authentication via JWT
					.anyRequest().authenticated();

		// JWT filter
		final JwtAuthorizationTokenFilter authorizationTokenFilter = new JwtAuthorizationTokenFilter(userDetailsService(), customApplicationProperties);
		// JWT filter
		httpSecurity.addFilterBefore(authorizationTokenFilter, UsernamePasswordAuthenticationFilter.class);
	}

	@Override
	public void configure(final WebSecurity web) {
		log.info(LogMarkers.FLOW_MARKER, "Inside Spring Security >>>> WebSecurity ignoring() configuration.");

		// AuthenticationTokenFilter will ignore the below paths
		web.ignoring()
		        .antMatchers(HttpMethod.POST, customApplicationProperties.getEndpoints().getAuthentication().getRoot())
					.and()
			.ignoring()
				.antMatchers(HttpMethod.POST, customApplicationProperties.getEndpoints().getRegistration().getRoot() + "/**")
					.and()
			.ignoring()
				.antMatchers(HttpMethod.GET, customApplicationProperties.getEndpoints().getRegistration().getRoot() + "/**")
					.and()
			.ignoring()
                .antMatchers(HttpMethod.GET,
                             "/",
                             "/*.html",
                             "/favicon.ico",
                             "/css/**",
                             "/js/**");
	}
}
