package me.grudzien.patryk.oauth2.exception;

public class UnknownOAuth2FlowException extends RuntimeException {

	private static final long serialVersionUID = -4271021530164285521L;

	public UnknownOAuth2FlowException(final String message) {
		super(message);
	}
}
