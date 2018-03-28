package me.grudzien.patryk.exceptions.login;

public class UserDisabledAuthenticationException extends RuntimeException {

	private static final long serialVersionUID = -6726748741327656684L;

	public UserDisabledAuthenticationException(final String message) {
		super(message);
	}
}
