package me.grudzien.patryk.exceptions.registration;

public class TokenNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -9128590544610805877L;

	public TokenNotFoundException(final String message) {
		super(message);
	}
}