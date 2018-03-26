package me.grudzien.patryk.exceptions.login;

public class AuthenticationException extends RuntimeException {

	private static final long serialVersionUID = 2699728387301596587L;

	public AuthenticationException(final String message) {
		super(message);
	}
}
