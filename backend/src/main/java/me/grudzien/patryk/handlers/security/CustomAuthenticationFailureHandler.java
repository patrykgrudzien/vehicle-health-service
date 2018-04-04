package me.grudzien.patryk.handlers.security;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.utils.log.LogMarkers;

@Log4j2
@Component
// TODO: CLASS TO BE REMOVED
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final CustomApplicationProperties customApplicationProperties;

	@Autowired
	public CustomAuthenticationFailureHandler(final CustomApplicationProperties customApplicationProperties) {
		this.customApplicationProperties = customApplicationProperties;
	}

	/*
	 * Now (MyUserDetailsService.java) uses the "enabled" flag of the user - and so it'll only allow enabled user to authenticate.
	 * This handler is gonna customize the exception messages coming from (MyUserDetailsService.java).
	 */
	@Override
	public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
	                                    final AuthenticationException exception) throws IOException, ServletException {
		log.info(LogMarkers.FLOW_MARKER, "User was trying to login but it doesn't exist or is NOT enabled by verification token");

		//  /login?error=true
		setDefaultFailureUrl(customApplicationProperties.getEndpoints().getAuthentication().getFailureUrl());
		// process with authentication failure
		super.onAuthenticationFailure(request, response, exception);
		// send custom message to UI
		request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, "User is disabled.");
	}
}
