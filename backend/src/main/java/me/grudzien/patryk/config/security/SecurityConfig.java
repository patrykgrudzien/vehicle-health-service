package me.grudzien.patryk.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.handlers.security.CustomAuthenticationEntryPoint;
import me.grudzien.patryk.service.security.MyUserDetailsService;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final MyUserDetailsService myUserDetailsService;
	private final CustomApplicationProperties customApplicationProperties;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	@Autowired
	public SecurityConfig(final MyUserDetailsService myUserDetailsService,
	                      final CustomApplicationProperties customApplicationProperties,
	                      final CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
		this.myUserDetailsService = myUserDetailsService;
		this.customApplicationProperties = customApplicationProperties;
		this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
	}

	@Override
	protected void configure(final HttpSecurity http) throws Exception {

		http
			// development purpose (no existing on production "Heroku")
			.cors().and()
			// disabling CSRF for now (testing purposes), later it'll be replaced by OAuth2 and JWT
			.csrf().disable()
		    .authorizeRequests()
		        // /registration/**
		        .antMatchers(customApplicationProperties.getEndpoints().getRegistration().getRoot() + "/**").permitAll()
				// /server/health-check
				.antMatchers(customApplicationProperties.getEndpoints().getServer().getRootHealthCheck()).authenticated()
				// home page
				.antMatchers("/").permitAll()
					.and()
			.formLogin().permitAll()
		            .and()
			.logout()
		        .invalidateHttpSession(Boolean.TRUE)
		        .clearAuthentication(Boolean.TRUE)
		        .logoutRequestMatcher(new AntPathRequestMatcher(customApplicationProperties.getEndpoints().getLogout().getRoot()))
		        .logoutSuccessUrl(customApplicationProperties.getEndpoints().getLogout().getRootSuccessUrl())
					.and()
			.exceptionHandling()
				.authenticationEntryPoint(customAuthenticationEntryPoint);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(myUserDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(authenticationProvider());
	}
}
