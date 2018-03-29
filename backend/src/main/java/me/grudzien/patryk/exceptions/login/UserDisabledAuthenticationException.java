package me.grudzien.patryk.exceptions.login;

import org.springframework.security.authentication.DisabledException;

public class UserDisabledAuthenticationException extends DisabledException {

	private static final long serialVersionUID = -6726748741327656684L;

	public UserDisabledAuthenticationException(final String message) {
		super(message);
	}
}
