package me.grudzien.patryk.registration.exception;

public class EmailVerificationTokenExpiredException extends RuntimeException {

	private static final long serialVersionUID = -5458840867208550428L;

	public EmailVerificationTokenExpiredException(final String message) {
		super(message);
	}
}
