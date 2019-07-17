package me.grudzien.patryk.configuration.filters;

import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.base.Preconditions;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.i18n.LocaleMessagesHelper;

/**
 * <hr><br>
 * The Spring Security {@link org.springframework.security.web.FilterChainProxy} dispatches requests to the first chain that matches.
 * A vanilla Spring Boot application with no custom security configuration has a several (call it n) filter chains, where usually n=6.
 * The first (n-1) chains are there just to ignore static resource patterns, like {@linkplain /css/**} and {@linkplain /images/**}, and
 * the error view {@linkplain /error} (the paths can be controlled by the user with {@linkplain security.ignored} from the
 * {@linkplain org.springframework.boot.autoconfigure.security.SecurityProperties} configuration bean).
 * The last chain matches to catch all path {@linkplain /**} and is more active, containing logic for authentication, authorization,
 * exception handling, session handling, header writing, etc. There are a total of 11 filters in this chain by default, but normally
 * it is not necessary for users to concern themselves with which filters are used and when.
 *
 * <br><br>
 *
 * <h2>Note:</h2>
 * <i>The fact that all filters internal to <b>Spring Security</b> are unknown to the container is important, especially in a Spring Boot
 * application, where all {@linkplain @Beans} of type {@linkplain Filter} are registered automatically with the container by default.
 * So if you want to add a custom filter to the security chain, you need to either <b>NOT</b> make it a {@linkplain @Bean} or wrap it in
 * a {@link org.springframework.boot.web.servlet.FilterRegistrationBean} that explicitly disables the container registration.</u>
 *
 * <br><br><hr><br>
 *
 * Another filters:
 * {@link GenericJwtTokenFilter},
 * {@link ServletExceptionHandlerFilter}
 */
@SuppressWarnings("JavadocReference")
@Log4j2
public class LocaleDeterminerFilter extends OncePerRequestFilter {

	private final LocaleMessagesHelper localeMessagesHelper;

	public LocaleDeterminerFilter(final LocaleMessagesHelper localeMessagesHelper) {
		Preconditions.checkNotNull(localeMessagesHelper, "localeMessagesHelper cannot be null!");
		this.localeMessagesHelper = localeMessagesHelper;
	}

	@SuppressWarnings("DanglingJavadoc")
    @Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
			throws IOException, ServletException {

		log.info("(FILTER) -----> {} ({}) on path -> {}", this.getClass().getSimpleName(), request.getMethod(), request.getRequestURI());

		/**
		 * Method {@link me.grudzien.patryk.utils.i18n.LocaleMessagesHelper#determineApplicationLocale(Object)} must be called in the first
		 * step because it sets locale according to header coming from UI.
		 *
		 * Filters' order is specified in:
		 * {@link me.grudzien.patryk.configuration.security.SecurityConfigContext.Filters#addTokenAuthenticationFilters(HttpSecurity, UserDetailsService,
         * PropertiesKeeper, LocaleMessagesCreator)}
		 *
		 * Later {@link me.grudzien.patryk.utils.i18n.LocaleMessagesHelper#getLocale()} is used in
		 * {@link me.grudzien.patryk.utils.i18n.LocaleMessagesCreator#buildLocaleMessage(String)} related methods to create i18n messages
		 * without passing WebRequest or HttpServletRequest object later in the code flow.
		 */
		if (!request.getMethod().equals(HttpMethod.OPTIONS.name())) {
			localeMessagesHelper.determineApplicationLocale(request);
		}
		filterChain.doFilter(request, response);
	}
}
