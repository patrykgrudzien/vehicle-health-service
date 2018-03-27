package me.grudzien.patryk.config.security;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.utils.log.LogMarkers;
import me.grudzien.patryk.utils.security.JwtTokenUtil;

@Log4j2
public class JwtAuthorizationTokenFilter extends OncePerRequestFilter {

	private final String tokenHeader;
	private final UserDetailsService userDetailsService;
	private final CustomApplicationProperties customApplicationProperties;

	public JwtAuthorizationTokenFilter(final UserDetailsService userDetailsService,
	                                   final CustomApplicationProperties customApplicationProperties) {

		this.userDetailsService = userDetailsService;
		this.customApplicationProperties = customApplicationProperties;

		log.info(LogMarkers.FLOW_MARKER, "Inside >>>> JwtAuthorizationTokenFilter()");
		this.tokenHeader = customApplicationProperties.getJwt().getHeader();
		log.info(LogMarkers.FLOW_MARKER, "Token Header >>>> " + tokenHeader);
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
	                                final FilterChain filterChain) throws ServletException, IOException {
		log.info(LogMarkers.FLOW_MARKER, "Processing authentication for '{}'", request.getRequestURL());

		final String requestHeader = request.getHeader(this.tokenHeader);

		String email = null;
		String authToken = null;
		if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
			authToken = requestHeader.substring(7);
			try {
				email = JwtTokenUtil.Retriever.getUserEmailFromToken(authToken);
			} catch (final IllegalArgumentException exception) {
				log.error(LogMarkers.FLOW_MARKER, "An error occurred during getting email from token", exception);
			} catch (final ExpiredJwtException exception) {
				log.warn(LogMarkers.FLOW_MARKER, "The token is expired and not valid anymore", exception);
			}
		}
		else {
			log.warn(LogMarkers.FLOW_MARKER, "Couldn't find bearer string, will ignore the header");
		}

		log.info(LogMarkers.FLOW_MARKER, "Checking authentication for user email {}.", email);
		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			log.debug(LogMarkers.FLOW_MARKER, "Security context was null, so authorizating user");

			// It is not compelling necessary to load the use details from the database. You could also store the information
			// in the token and read it from it. It's up to you ;)
			final UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

			// For simple validation it is completely sufficient to just check the token integrity. You don't have to call
			// the database compellingly. Again it's up to you ;)
			if (JwtTokenUtil.Validator.validateToken(authToken, userDetails)) {
				final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				                                                                                                   userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				log.info(LogMarkers.FLOW_MARKER, "Authorized user '{}', setting security context.", email);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(request, response);
	}
}
