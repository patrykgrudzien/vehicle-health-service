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

import me.grudzien.patryk.service.CustomUserDetailsService;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	// field injection to avoid circular dependency and BeanCurrentlyInCreationException
	@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Override
	protected void configure(final HttpSecurity http) throws Exception {

		http.cors().and()
			// disabling CSRF for now (testing purposes), later it'll be replaced by OAuth2 and JWT
			.csrf().disable()
		    .authorizeRequests()
		        .antMatchers("registration/**")
		        .permitAll()
		    .and()
		        .logout()
		        .invalidateHttpSession(Boolean.TRUE)
		        .clearAuthentication(Boolean.TRUE)
		        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		        .logoutSuccessUrl("/login?logout")
		        .permitAll();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(customUserDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(authenticationProvider());
	}
}
