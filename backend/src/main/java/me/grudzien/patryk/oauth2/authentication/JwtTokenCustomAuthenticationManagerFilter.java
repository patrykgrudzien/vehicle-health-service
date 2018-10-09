package me.grudzien.patryk.oauth2.authentication;

import lombok.extern.log4j.Log4j2;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static me.grudzien.patryk.utils.log.LogMarkers.EXCEPTION_MARKER;
import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.utils.jwt.JwtTokenUtil;

@Log4j2
public class JwtTokenCustomAuthenticationManagerFilter extends OncePerRequestFilter {

	private final String tokenHeader;
	private final AuthenticationManager authenticationManager;

	public JwtTokenCustomAuthenticationManagerFilter(final AuthenticationManager authenticationManager, final PropertiesKeeper propertiesKeeper) {
		this.authenticationManager = authenticationManager;
		this.tokenHeader = propertiesKeeper.jwt().TOKEN_HEADER;
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
			throws ServletException, IOException {

		log.info(FLOW_MARKER, "(FILTER) -----> {} ({}) on path -> {}", this.getClass().getSimpleName(), request.getMethod(), request.getRequestURI());

		final String requestHeader = request.getHeader(this.tokenHeader);

		String email = null;
		String accessToken = null;
		if (requestHeader != null && requestHeader.startsWith(JwtTokenUtil.BEARER)) {
			accessToken = requestHeader.substring(JwtTokenUtil.JWT_TOKEN_BEGIN_INDEX);
			email = JwtTokenUtil.Retriever.getUserEmailFromToken(accessToken);
		} else {
			log.warn(EXCEPTION_MARKER, "Couldn't find bearer string, will ignore the header");
		}

		log.info(FLOW_MARKER, "Checking authentication for user email {}.", email);

		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			log.debug(FLOW_MARKER, "Security context was null, starting authenticating the user...");

			try {
				final JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(accessToken);
				final Authentication authentication = authenticationManager.authenticate(jwtAuthenticationToken);
				SecurityContextHolder.getContext().setAuthentication(authentication);

			} catch (final Exception exception) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		}
		filterChain.doFilter(request, response);
	}
}
