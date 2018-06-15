package me.grudzien.patryk.config.i18n;

import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import me.grudzien.patryk.utils.i18n.LocaleMessagesHelper;

import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;

@Log4j2
/**
 * Marking this filter as Spring @Component is done by purpose ->
 * {@link LocaleDeterminerFilter#doFilterInternal(javax.servlet.http.HttpServletRequest,
 * javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)}
 * will be called on each request.
 */
@Component
public class LocaleDeterminerFilter extends OncePerRequestFilter {

	private final LocaleMessagesHelper localeMessagesHelper = LocaleMessagesHelper.getINSTANCE();

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
			throws IOException, ServletException {

		log.info(FLOW_MARKER, "Inside >>>> {}, request method >>>> {}", this.getClass().getSimpleName(), request.getMethod());

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
