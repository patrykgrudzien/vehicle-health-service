package me.grudzien.patryk.exception.registration;

public class TokenExpiredException extends RuntimeException {

	private static final long serialVersionUID = -2978374911347052504L;

	public TokenExpiredException(final String message) {
		super(message);
	}
}
