package me.grudzien.patryk.config.filters;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.domain.dto.responses.CustomResponse.SecurityStatus;
import me.grudzien.patryk.domain.dto.responses.ExceptionResponse;
import me.grudzien.patryk.util.web.HttpResponseCustomizer;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.run;
import static io.vavr.Predicates.instanceOf;

import static me.grudzien.patryk.util.log.LogMarkers.EXCEPTION_MARKER;
import static me.grudzien.patryk.util.log.LogMarkers.FLOW_MARKER;

/**
 * Filters CANNOT be managed by Spring explicitly !!!
 * It's NOT ALLOWED to mark them using (@Component) annotation !!!
 * In other case Spring Security does not work properly and does not ignore specified paths.
 * Another filter:
 * {@link me.grudzien.patryk.config.filters.GenericJwtTokenFilter}
 *
 * This filter is used in case of unsuccessful JWT operations which are performed in:
 * {@link me.grudzien.patryk.util.jwt.JwtTokenUtil.Retriever}
 * e.g. when token is expired there is no option to
 * {@link me.grudzien.patryk.util.jwt.JwtTokenUtil.Retriever#getAllClaimsFromToken(String)}.
 * In such case one of the exceptions listed below is thrown.
 */
@Log4j2
public class ServletExceptionHandlerFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) {

		log.info(FLOW_MARKER, "(FILTER) -----> {} ({}) on path -> {}", this.getClass().getSimpleName(), request.getMethod(), request.getRequestURI());

		Try.run(() -> filterChain.doFilter(request, response))
		   .onFailure(throwable -> Match(throwable).of(
		   		Case($(instanceOf(ExpiredJwtException.class)), ExpiredJwtException -> run(() -> {
				    log.error(EXCEPTION_MARKER, "The JWT token is expired and not valid anymore, message -> {}", ExpiredJwtException.getMessage());
                    HttpResponseCustomizer.customizeHttpResponse(response, HttpStatus.UNAUTHORIZED,
                                                                 ExceptionResponse.buildBodyMessage(ExpiredJwtException, SecurityStatus.JWT_TOKEN_EXPIRED,
                                                                                                    request.getRequestURI(), request.getMethod()));
			    })),
			    Case($(instanceOf(IllegalArgumentException.class)), IllegalArgumentException -> run(() ->
					log.error(EXCEPTION_MARKER, "An error occurred during getting email from token, message -> {}", IllegalArgumentException.getMessage()))),
			    Case($(instanceOf(UnsupportedJwtException.class)), UnsupportedJwtException -> run(() ->
					log.error(EXCEPTION_MARKER, "UnsupportedJwtException message -> {}", UnsupportedJwtException.getMessage()))),
			    Case($(instanceOf(MalformedJwtException.class)), MalformedJwtException -> run(() ->
					log.error(EXCEPTION_MARKER, "MalformedJwtException message -> {}", MalformedJwtException.getMessage()))),
			    Case($(instanceOf(SignatureException.class)), SignatureException -> run(() ->
					log.error(EXCEPTION_MARKER, "SignatureException message -> {}", SignatureException.getMessage()))),
			    // if any other exception
			    Case($(), () -> new RuntimeException("No specific exception caught inside ServletExceptionHandlerFilter.doFilterInternal()..."))
		   ));
	}
}
