package me.grudzien.patryk.config.filters;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Qualifier;
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
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.utils.jwt.JwtTokenUtil;

import static me.grudzien.patryk.utils.log.LogMarkers.EXCEPTION_MARKER;
import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;

/**
 * Filters CANNOT be managed by Spring explicitly !!!
 * It's NOT ALLOWED to mark them using (@Component) annotation !!!
 * In other case Spring Security does not work properly and does not ignore specified paths.
 * Another filter:
 * {@link me.grudzien.patryk.config.filters.ServletExceptionHandlerFilter}
 */
@Log4j2
public class JwtAuthorizationTokenFilter extends OncePerRequestFilter {

	private final String tokenHeader;
	private final UserDetailsService userDetailsService;

	public JwtAuthorizationTokenFilter(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
	                                   final CustomApplicationProperties customApplicationProperties) {
		this.userDetailsService = userDetailsService;
		this.tokenHeader = customApplicationProperties.getJwt().getHeader();
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
	                                final FilterChain filterChain) throws ServletException, IOException {

		log.info(FLOW_MARKER, "(FILTER) -----> {} ({})", this.getClass().getSimpleName(), request.getMethod());
		log.info(FLOW_MARKER, "Processing authentication for -----> {}", request.getRequestURI());

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
			/*
			 * It is not compelling necessary to load the use details from the database. You could also store the information
			 * in the token and read it from it.
			 */
			final UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
			/*
			 * For simple validation it is completely sufficient to just check the token integrity. You don't have to call
			 * the database compellingly.
			 */
			if (JwtTokenUtil.Validator.validateToken(accessToken, userDetails)) {
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
				log.info(FLOW_MARKER, "Authorized user '{}', setting security context.", email);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(request, response);
	}
}
