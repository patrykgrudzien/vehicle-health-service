package me.grudzien.patryk.exceptions.exception;

import lombok.Getter;

@Getter
public class UserEmailNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 4207195928139509940L;

	private final String email;

	public UserEmailNotFoundException(final String email, final String message) {
		super(message);
		this.email = email;
	}
}
