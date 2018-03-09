package me.grudzien.patryk.handlers.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Handler for customization the exception messages coming from "MyUserDetailsService".
 */
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
	                                    final AuthenticationException exception) throws IOException, ServletException {
		// TODO: to be implemented
		super.onAuthenticationFailure(request, response, exception);
	}
}
