package me.grudzien.patryk.oauth2.exceptions;

public class UnknownDelegateException extends RuntimeException {

	private static final long serialVersionUID = -4271021530164285521L;

	public UnknownDelegateException(final String message) {
		super(message);
	}
}
