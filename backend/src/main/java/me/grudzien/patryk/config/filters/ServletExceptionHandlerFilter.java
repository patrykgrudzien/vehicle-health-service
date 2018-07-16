package me.grudzien.patryk.config.filters;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.log4j.Log4j2;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static me.grudzien.patryk.domain.dto.responses.CustomResponse.Codes.JWT_TOKEN_EXPIRED;
import static me.grudzien.patryk.domain.dto.responses.ExceptionResponse.buildMessageWithExceptionCode;
import static me.grudzien.patryk.utils.log.LogMarkers.EXCEPTION_MARKER;
import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;
import static me.grudzien.patryk.utils.web.CustomResponseCreator.customizeHttpResponse;

/**
 * Filters CANNOT be managed by Spring explicitly !!!
 * It's NOT ALLOWED to mark them using (@Component) annotation !!!
 * In other case Spring Security does not work properly and does not ignore specified paths.
 * Another filter:
 * {@link JwtAuthorizationTokenFilter}
 */
@Log4j2
public class ServletExceptionHandlerFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
			throws ServletException, IOException {

		log.info(FLOW_MARKER, "(FILTER) -----> {} ({})", this.getClass().getSimpleName(), request.getMethod());
		log.info(FLOW_MARKER, "(FILTER Path) -----> {}", request.getRequestURI());

		try {
			filterChain.doFilter(request, response);
		} catch (final IllegalArgumentException exception) {
			log.error(EXCEPTION_MARKER, "An error occurred during getting email from token, message -> {}", exception.getMessage());
		} catch (final ExpiredJwtException exception) {
			log.error(EXCEPTION_MARKER, "The JWT token is expired and not valid anymore, message -> {}", exception.getMessage());
			customizeHttpResponse(response, UNAUTHORIZED, buildMessageWithExceptionCode(exception, JWT_TOKEN_EXPIRED));
		} catch (final UnsupportedJwtException exception) {
			log.error(EXCEPTION_MARKER, "UnsupportedJwtException message -> {}", exception.getMessage());
		} catch (final MalformedJwtException exception) {
			log.error(EXCEPTION_MARKER, "MalformedJwtException message -> {}", exception.getMessage());
		} catch (final SignatureException exception) {
			log.error(EXCEPTION_MARKER, "SignatureException message -> {}", exception.getMessage());
		}
	}
}
