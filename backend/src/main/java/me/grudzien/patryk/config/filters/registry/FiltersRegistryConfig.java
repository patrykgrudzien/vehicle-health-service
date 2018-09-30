package me.grudzien.patryk.config.filters.registry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;

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
	 * I cannot create constants inside:
	 * {@link me.grudzien.patryk.PropertiesKeeper.Endpoints}
	 * and use them here, because classes annotated by:
	 * {@link org.springframework.context.annotation.Configuration}
	 * are loaded at the first stage of context loading and bean(s) which I require
	 * to create constants are not loaded yet.
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
				propertiesKeeper.endpoints().REGISTER_USER_ACCOUNT,
				// //oauth2/authorization**
				// TODO: clicking google button on UI, "Language" header should bo somehow attached to allow filter build appropriate messages
				OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "**"
		);
		return registrationBean;
	}
}
