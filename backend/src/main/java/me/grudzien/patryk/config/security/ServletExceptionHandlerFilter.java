package me.grudzien.patryk.config.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static me.grudzien.patryk.utils.log.LogMarkers.EXCEPTION_MARKER;
import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;

import me.grudzien.patryk.domain.dto.responses.ExceptionResponse;

@Log4j2
@Component
public class ServletExceptionHandlerFilter extends OncePerRequestFilter {

	private final ObjectMapper objectMapper;

	@Autowired
	public ServletExceptionHandlerFilter(final ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
			throws ServletException, IOException {

		log.info(FLOW_MARKER, "Inside >>>> ServletExceptionHandlerFilter");

		try {
			filterChain.doFilter(request, response);
		} catch (final IllegalArgumentException exception) {
			log.error(EXCEPTION_MARKER, "An error occurred during getting email from token, message -> {}", exception.getMessage());
		} catch (final ExpiredJwtException exception) {
			log.error(EXCEPTION_MARKER, "The JWT token is expired and not valid anymore, message -> {}", exception.getMessage());

			final ExceptionResponse exceptionResponse = ExceptionResponse.Builder()
			                                                             .message(exception.getMessage())
			                                                             .code("JWT TOKEN EXPIRED")
			                                                             .build();
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			// write the custom response body
			objectMapper.writeValue(response.getOutputStream(), exceptionResponse);
			// commit the response
			response.flushBuffer();

		} catch (final UnsupportedJwtException exception) {
			log.error(EXCEPTION_MARKER, "UnsupportedJwtException message -> {}", exception.getMessage());
		} catch (final MalformedJwtException exception) {
			log.error(EXCEPTION_MARKER, "MalformedJwtException message -> {}", exception.getMessage());
		} catch (final SignatureException exception) {
			log.error(EXCEPTION_MARKER, "SignatureException message -> {}", exception.getMessage());
		}
	}
}
