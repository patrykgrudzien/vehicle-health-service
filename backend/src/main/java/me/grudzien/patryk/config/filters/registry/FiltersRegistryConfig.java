package me.grudzien.patryk.config.filters.registry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.config.i18n.LocaleDeterminerFilter;

@Configuration
public class FiltersRegistryConfig {

	private final CustomApplicationProperties customApplicationProperties;

	@Autowired
	public FiltersRegistryConfig(final CustomApplicationProperties customApplicationProperties) {
		this.customApplicationProperties = customApplicationProperties;
	}

	@Bean
	public FilterRegistrationBean<LocaleDeterminerFilter> localeDeterminerFilter() {

		final FilterRegistrationBean<LocaleDeterminerFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new LocaleDeterminerFilter());
		// TODO: provide all required paths
		registrationBean.addUrlPatterns(customApplicationProperties.getEndpoints().getAuthentication().getRoot());
		return registrationBean;
	}
}
