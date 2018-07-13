package me.grudzien.patryk.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppFLow {

	ACCOUNT_ALREADY_ENABLED(
			"Created user already enabled URL for (LOCAL) env.",
			"User successfully redirected to ({}) url.",
			"Cannot redirect user to ({}) url."
	),
	VERIFICATION_TOKEN_CREATION(
			"Created verification token URL for (LOCAL) env.",
			"User successfully redirected to ({}) url.",
			"Cannot redirect user to ({}) url."
	),
	VERIFICATION_TOKEN_NOT_FOUND(
			"Created verification token not found URL for (LOCAL) env.",
			"User successfully redirected to ({}) url.",
			"Cannot redirect user to ({}) url."
	),
	VERIFICATION_TOKEN_EXPIRED(
			"Created verification token expired URL for (LOCAL) env.",
			"User successfully redirected to ({}) url.",
			"Cannot redirect user to ({}) url."
	),
	CONFIRM_REGISTRATION(
			"Created registration confirmation URL for (LOCAL) env.",
			"User successfully redirected to ({}) url.",
			"Cannot redirect user to ({}) url."
	);

	private String determineUrlLogInfoMessage;
	private String successfulRedirectionLogInfoMessage;
	private String redirectionExceptionLogErrorMessage;
}
