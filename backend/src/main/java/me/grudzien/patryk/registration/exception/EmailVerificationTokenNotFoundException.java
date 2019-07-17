package me.grudzien.patryk.registration.exception;

public class EmailVerificationTokenNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -6454562086472852598L;

	public EmailVerificationTokenNotFoundException(final String message) {
		super(message);
	}
}
