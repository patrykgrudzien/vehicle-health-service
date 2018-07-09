package me.grudzien.patryk.config.filters.registry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.config.i18n.LocaleDeterminerFilter;

/**
 * This configuration determines on which endpoint {@link me.grudzien.patryk.config.i18n.LocaleDeterminerFilter} is applied.
 */
@Configuration
public class FiltersRegistryConfig {

	private final CustomApplicationProperties customApplicationProperties;

	@Autowired
	public FiltersRegistryConfig(final CustomApplicationProperties customApplicationProperties) {
		this.customApplicationProperties = customApplicationProperties;
	}

	@Bean
	public FilterRegistrationBean<LocaleDeterminerFilter> registerLocaleDeterminerFilter() {

		final FilterRegistrationBean<LocaleDeterminerFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new LocaleDeterminerFilter());
		registrationBean.addUrlPatterns(
				// -> /auth
				customApplicationProperties.getEndpoints().getAuthentication().getRoot(),
				// -> /registration
				customApplicationProperties.getEndpoints().getRegistration().getRoot(),
				// -> /register-user-account
				customApplicationProperties.getEndpoints().getRegistration().getRegisterUserAccount(),
				// -> /registration/register-user-account
				customApplicationProperties.getEndpoints().getRegistration().getRoot() +
				customApplicationProperties.getEndpoints().getRegistration().getRegisterUserAccount()
		);
		return registrationBean;
	}
}
