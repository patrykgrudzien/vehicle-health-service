package me.grudzien.patryk.config.security;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import com.google.common.base.Preconditions;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.oauth2.authentication.CustomAuthenticationProvider;
import me.grudzien.patryk.oauth2.handler.CustomOAuth2AuthenticationFailureHandler;
import me.grudzien.patryk.oauth2.handler.CustomOAuth2AuthenticationSuccessHandler;
import me.grudzien.patryk.oauth2.service.CustomOAuth2UserService;
import me.grudzien.patryk.oauth2.service.CustomOidcUserService;
import me.grudzien.patryk.oauth2.util.CacheHelper;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.util.i18n.LocaleMessagesCreator;

import static me.grudzien.patryk.util.log.LogMarkers.FLOW_MARKER;

/**
 * <h2>Creating and Customizing Filter Chains</h2>
 * <p>
 *     The default fallback filter chain in a Spring Boot app (the one with the {@linkplain /**} request matcher) has a predefined order of
 *     {@link org.springframework.boot.autoconfigure.security.SecurityProperties#BASIC_AUTH_ORDER}. You can switch it off completely by setting
 *     {@linkplain security.basic.enabled} = false, or you can use it as a fallback and just define other rules with a lower order. To do that just add a
 *     {@linkplain @Bean} of type {@link org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter} and decorate the class
 *     with {@linkplain @Order}.
 * </p>
 */
@SuppressWarnings("JavadocReference")   // disabling errors caused by "security.basic.enabled" mentioned above
@Log4j2
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
// This will cause Spring Security to add a new filter chain and order it before the fallback.
@Order(SecurityProperties.BASIC_AUTH_ORDER - 10)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler;
	private final CustomOAuth2AuthenticationFailureHandler customOAuth2AuthenticationFailureHandler;
	private final CustomOidcUserService customOidcUserService;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final PropertiesKeeper propertiesKeeper;
	private final CacheHelper cacheHelper;
	private final LocaleMessagesCreator localeMessageCreator;

	private final CustomAuthenticationProvider customAuthenticationProvider;

	/**
	 * {@linkplain @Qualifier} for {@link org.springframework.security.core.userdetails.UserDetailsService} is used here because there is also
	 * {@link org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration} available and Spring
	 * does not know that I want to use my custom implementation.
	 */
	@Autowired
	public SecurityConfig(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
	                      final CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
	                      final CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler,
	                      final CustomOAuth2AuthenticationFailureHandler customOAuth2AuthenticationFailureHandler, final CustomOidcUserService customOidcUserService,
	                      final CustomOAuth2UserService customOAuth2UserService, final PropertiesKeeper propertiesKeeper, final CacheHelper cacheHelper,
	                      final LocaleMessagesCreator localeMessageCreator, final CustomAuthenticationProvider customAuthenticationProvider) {

		Preconditions.checkNotNull(userDetailsService, "userDetailsService cannot be null!");
		Preconditions.checkNotNull(customAuthenticationEntryPoint, "customAuthenticationEntryPoint cannot be null!");
		Preconditions.checkNotNull(customOAuth2AuthenticationSuccessHandler, "customOAuth2AuthenticationSuccessHandler cannot be null!");
		Preconditions.checkNotNull(customOAuth2AuthenticationFailureHandler, "customOAuth2AuthenticationFailureHandler cannot be null!");
		Preconditions.checkNotNull(customOidcUserService, "customOidcUserService cannot be null!");
		Preconditions.checkNotNull(customOAuth2UserService, "customOAuth2UserService cannot be null!");
		Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");
		Preconditions.checkNotNull(cacheHelper, "cacheHelper cannot be null!");
		Preconditions.checkNotNull(localeMessageCreator, "localeMessageCreator cannot be null!");

		this.userDetailsService = userDetailsService;
		this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
		this.customOAuth2AuthenticationSuccessHandler = customOAuth2AuthenticationSuccessHandler;
		this.customOAuth2AuthenticationFailureHandler = customOAuth2AuthenticationFailureHandler;
		this.customOidcUserService = customOidcUserService;
		this.customOAuth2UserService = customOAuth2UserService;
		this.propertiesKeeper = propertiesKeeper;
		this.cacheHelper = cacheHelper;
		this.localeMessageCreator = localeMessageCreator;
		this.customAuthenticationProvider = customAuthenticationProvider;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	protected void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService)
		    .passwordEncoder(this.passwordEncoder())
		    .and()
		    .authenticationProvider(customAuthenticationProvider);
		log.info(FLOW_MARKER, "Authentication Provider configured globally.");
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

	private HttpSessionRequestCache httpSessionRequestCache() {
		final HttpSessionRequestCache httpSessionRequestCache = new HttpSessionRequestCache();
		httpSessionRequestCache.setCreateSessionAllowed(Boolean.FALSE);
		return httpSessionRequestCache;
	}

	@Override
	protected void configure(final HttpSecurity httpSecurity) throws Exception {
		// don't create session - set creation policy to STATELESS
		SecurityConfigContext.sessionCreationPolicy(httpSecurity);
		SecurityConfigContext.httpSessionRequestCache(httpSecurity, httpSessionRequestCache());

		// we are stateless so these things are not needed
        SecurityConfigContext.disableFormLogin(httpSecurity);
        SecurityConfigContext.disableHttpBasic(httpSecurity);
        SecurityConfigContext.disableLogout(httpSecurity);

		// show message to the user that some resource requires authentication
		SecurityConfigContext.exceptionHandling(httpSecurity, customAuthenticationEntryPoint);

        // don't need CSRF because JWT token is invulnerable
        SecurityConfigContext.disableCSRF(httpSecurity);

        // CORS configuration
        SecurityConfigContext.addCORSFilter(httpSecurity);

		/**
		 * {@link me.grudzien.patryk.config.filters.GenericJwtTokenFilter}
		 * &&
		 * {@link me.grudzien.patryk.config.filters.ServletExceptionHandlerFilter}
		 */
		SecurityConfigContext.Filters.addTokenAuthenticationFilters(httpSecurity, super.userDetailsService(), propertiesKeeper, localeMessageCreator);

		// oauth2 clients
        SecurityConfigContext.OAuth2.configureOAuth2Client(httpSecurity, propertiesKeeper, cacheHelper, customOAuth2AuthenticationSuccessHandler,
                                                           customOAuth2AuthenticationFailureHandler, customOidcUserService, customOAuth2UserService);
		// mvcMatchers
        SecurityConfigContext.Requests.authorizeRequests(httpSecurity, propertiesKeeper);
	}

    /**
     * Configuring which requests should be ignored.
     */
	@Override
	public void configure(final WebSecurity webSecurity) {
	    SecurityConfigContext.Web.configureIgnoredRequests(webSecurity, propertiesKeeper);
	}
}
