package me.grudzien.patryk.config.security;

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

import static me.grudzien.patryk.utils.log.LogMarkers.EXCEPTION_MARKER;

@Log4j2
public class JwtAuthorizationTokenFilter extends OncePerRequestFilter {

	private final String tokenHeader;
	private final UserDetailsService userDetailsService;

	public JwtAuthorizationTokenFilter(final UserDetailsService userDetailsService,
	                                   final CustomApplicationProperties customApplicationProperties) {
		this.userDetailsService = userDetailsService;
		this.tokenHeader = customApplicationProperties.getJwt().getHeader();
		log.info(LogMarkers.FLOW_MARKER, "Inside >>>> JwtAuthorizationTokenFilter()");
		log.info(LogMarkers.FLOW_MARKER, "Token Header >>>> {}", tokenHeader);
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
	                                final FilterChain filterChain) throws ServletException, IOException {
		log.info(LogMarkers.FLOW_MARKER, "Processing authentication for '{}'", request.getRequestURL());

		final String requestHeader = request.getHeader(this.tokenHeader);

		String email = null;
		String authToken = null;
		if (requestHeader != null && requestHeader.startsWith(JwtTokenUtil.BEARER)) {
			authToken = requestHeader.substring(JwtTokenUtil.JWT_TOKEN_BEGIN_INDEX);
			email = JwtTokenUtil.Retriever.getUserEmailFromToken(authToken);
		} else {
			log.warn(EXCEPTION_MARKER, "Couldn't find bearer string, will ignore the header");
		}

		log.info(LogMarkers.FLOW_MARKER, "Checking authentication for user email {}.", email);
		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			log.debug(LogMarkers.FLOW_MARKER, "Security context was null, so authorizating user");

			/*
			 * It is not compelling necessary to load the use details from the database. You could also store the information
			 * in the token and read it from it.
			 */
			final UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
			/*
			 * For simple validation it is completely sufficient to just check the token integrity. You don't have to call
			 * the database compellingly.
			 */
			if (JwtTokenUtil.Validator.validateToken(authToken, userDetails)) {
				/*
				 * UsernamePasswordAuthenticationToken- an {@link org.springframework.security.core.Authentication} implementation that is
				 * designed for simple presentation of a username and password.
				 *
				 * Authentication - represents the token for an authentication request or for an authentication principal once the
				 * request has been processed by "AuthenticationManager.authenticate()" method.
				 * Once the request has been authenticated, the "Authentication" will usually be stored in a thread-local
				 * (SecurityContext) managed by the (SecurityContextHolder) by the authentication mechanism which is being used.
				 *
				 * Credentials - they prove that principal is correct. This is usually a password, but could be anything relevant to the
				 * AuthenticationManager. Callers are expected to populate the credentials.
				 */
				final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				                                                                                                   userDetails.getAuthorities());
				/*
				 * Details - store additional details about the authentication request. These might be an IP address, certificate serial
				 * number etc.
				 *
				 * WebAuthenticationDetailsSource - implementation of (AuthenticationDetailsSource) which builds the details object from
				 * an "HttpServletRequest" object.
				 */
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				log.info(LogMarkers.FLOW_MARKER, "Authorized user '{}', setting security context.", email);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(request, response);
	}
}
