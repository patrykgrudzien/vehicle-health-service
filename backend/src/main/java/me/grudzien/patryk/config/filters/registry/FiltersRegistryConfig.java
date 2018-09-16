package me.grudzien.patryk.config.filters.registry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Preconditions;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.config.filters.LocaleDeterminerFilter;

/**
 * This configuration determines on which endpoint {@link me.grudzien.patryk.config.filters.LocaleDeterminerFilter} is applied.
 */
@Configuration
public class FiltersRegistryConfig {

	private final PropertiesKeeper propertiesKeeper;

	@Autowired
	public FiltersRegistryConfig(final PropertiesKeeper propertiesKeeper) {
		Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");
		this.propertiesKeeper = propertiesKeeper;
	}

	/**
	 * I cannot use here constants from:
	 * {@link me.grudzien.patryk.PropertiesKeeper.Endpoints}
	 * because classes annotated by:
	 * {@link org.springframework.context.annotation.Configuration}
	 * are loaded at the first stage of context loading.
	 */
	@Bean
	public FilterRegistrationBean<LocaleDeterminerFilter> registerLocaleDeterminerFilter() {

		final FilterRegistrationBean<LocaleDeterminerFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new LocaleDeterminerFilter());
		registrationBean.addUrlPatterns(
				// -> /auth
				propertiesKeeper.endpoints().AUTH,
				// -> /registration
				propertiesKeeper.endpoints().REGISTRATION,
				// -> /register-user-account
				propertiesKeeper.endpoints().REGISTER_USER_ACCOUNT,
				// -> /registration/register-user-account
				propertiesKeeper.endpoints().REGISTRATION +
				propertiesKeeper.endpoints().REGISTER_USER_ACCOUNT
		);
		return registrationBean;
	}
}
