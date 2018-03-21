package me.grudzien.patryk.exceptions;

public class RedirectionException extends RuntimeException {

	private static final long serialVersionUID = 5157631299549733108L;

	public RedirectionException(final String message) {
		super(message);
	}
}
