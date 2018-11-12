package me.grudzien.patryk.exception;

public class RedirectionException extends RuntimeException {

	private static final long serialVersionUID = 5157631299549733108L;

	public RedirectionException(final String message) {
		super(message);
	}
}
