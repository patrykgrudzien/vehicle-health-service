package me.grudzien.patryk.exceptions.registration;

public class UserAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = -2119137559395579575L;

	public UserAlreadyExistsException(final String message) {
		super(message);
	}
}
