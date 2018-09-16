package me.grudzien.patryk.config.filters;

import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;

import me.grudzien.patryk.utils.i18n.LocaleMessagesHelper;

/**
 * This filter is registered in {@link me.grudzien.patryk.config.filters.registry.FiltersRegistryConfig}
 * and configured to be fired on specific URL's.
 * See:
 * {@link me.grudzien.patryk.config.filters.registry.FiltersRegistryConfig#registerLocaleDeterminerFilter()}
 */
@Log4j2
public class LocaleDeterminerFilter extends OncePerRequestFilter {

	private final LocaleMessagesHelper localeMessagesHelper = LocaleMessagesHelper.getINSTANCE();

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
			throws IOException, ServletException {

		log.info(FLOW_MARKER, "(FILTER) -----> {} ({}) on path -> {}", this.getClass().getSimpleName(), request.getMethod(), request.getRequestURI());

		/**
		 * Method {@link me.grudzien.patryk.utils.i18n.LocaleMessagesHelper#determineApplicationLocale(Object)} must be called in the first
		 * step because it sets locale according to header coming from UI.
		 *
		 * Filters' order is specified in:
		 * {@link me.grudzien.patryk.config.security.SecurityConfig#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)}
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
