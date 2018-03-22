package me.grudzien.patryk.handlers.security;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import me.grudzien.patryk.utils.log.LogMarkers;

@Log4j2
@Component
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final ObjectMapper objectMapper;

	@Autowired
	public CustomUsernamePasswordAuthenticationFilter(final ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)
			throws AuthenticationException {

		log.info(LogMarkers.FLOW_MARKER, "attemptAuthentication() inside >>>> CustomUsernamePasswordAuthenticationFilter");

		try {

			final LoginRequest loginRequest = objectMapper.readValue(request.getReader(), LoginRequest.class);
			log.info(LogMarkers.FLOW_MARKER, "User's email who attempts authentication: {}", loginRequest.getEmail());

			final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
					loginRequest.getEmail(), loginRequest.getPassword());

			setDetails(request, token);

			return this.getAuthenticationManager().authenticate(token);

		} catch (final IOException exception) {

			log.error(exception.getMessage());
			exception.printStackTrace();
		}
		return super.attemptAuthentication(request, response);
	}

	@Getter
	@Setter
	private static class LoginRequest{
		private String email;
		private String password;
	}
}
