package me.grudzien.patryk.config.filters.registry;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import me.grudzien.patryk.config.filters.LocaleDeterminerFilter;

import static me.grudzien.patryk.Constants.Endpoints;

/**
 * This configuration determines on which endpoint {@link me.grudzien.patryk.config.filters.LocaleDeterminerFilter} is applied.
 */
@Configuration
public class FiltersRegistryConfig {

	@Bean
	public FilterRegistrationBean<LocaleDeterminerFilter> registerLocaleDeterminerFilter() {

		final FilterRegistrationBean<LocaleDeterminerFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new LocaleDeterminerFilter());
		registrationBean.addUrlPatterns(
				// -> /auth
				Endpoints.AUTH,
				// -> /registration
				Endpoints.REGISTRATION,
				// -> /register-user-account
				Endpoints.REGISTER_USER_ACCOUNT,
				// -> /registration/register-user-account
				Endpoints.REGISTRATION + Endpoints.REGISTER_USER_ACCOUNT
		);
		return registrationBean;
	}
}
