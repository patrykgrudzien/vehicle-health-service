package me.grudzien.patryk.exceptions.login;

import org.springframework.security.authentication.BadCredentialsException;

public class BadCredentialsAuthenticationException extends BadCredentialsException {

	private static final long serialVersionUID = 2699728387301596587L;

	public BadCredentialsAuthenticationException(final String message) {
		super(message);
	}
}
