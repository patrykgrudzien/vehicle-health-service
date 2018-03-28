package me.grudzien.patryk.exceptions.login;

public class BadCredentialsAuthenticationException extends RuntimeException {

	private static final long serialVersionUID = 2699728387301596587L;

	public BadCredentialsAuthenticationException(final String message) {
		super(message);
	}
}
