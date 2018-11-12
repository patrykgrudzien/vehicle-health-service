package me.grudzien.patryk.exception.registration;

import lombok.Getter;

import java.util.List;

@Getter
public class CustomUserValidationException extends RuntimeException {

	private static final long serialVersionUID = 643102337064671517L;

	private final List<String> validationErrors;

	public CustomUserValidationException(final String message, final List<String> validationErrors) {
		super(message);
		this.validationErrors = validationErrors;
	}
}
