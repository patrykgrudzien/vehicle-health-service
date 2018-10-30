package me.grudzien.patryk.oauth2.exceptions;

public class JwtTokenNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -350353448738602590L;

	public JwtTokenNotFoundException(final String message) {
		super(message);
	}
}
