package me.grudzien.patryk.domain.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppFLow {

	ACCOUNT_ALREADY_ENABLED(
			"Created user already enabled URL for (LOCAL) env.",
			AppFLow.SUCCESSFUL_REDIRECTION_DEFAULT_LOG_INFO_MESSAGE,
			AppFLow.REDIRECTION_EXCEPTION_DEFAULT_LOG_INFO_MESSAGE
	),
	VERIFICATION_TOKEN_CREATION(
			"Created verification token URL for (LOCAL) env.",
			AppFLow.SUCCESSFUL_REDIRECTION_DEFAULT_LOG_INFO_MESSAGE,
			AppFLow.REDIRECTION_EXCEPTION_DEFAULT_LOG_INFO_MESSAGE
	),
	VERIFICATION_TOKEN_NOT_FOUND(
			"Created verification token not found URL for (LOCAL) env.",
			AppFLow.SUCCESSFUL_REDIRECTION_DEFAULT_LOG_INFO_MESSAGE,
			AppFLow.REDIRECTION_EXCEPTION_DEFAULT_LOG_INFO_MESSAGE
	),
	VERIFICATION_TOKEN_EXPIRED(
			"Created verification token expired URL for (LOCAL) env.",
			AppFLow.SUCCESSFUL_REDIRECTION_DEFAULT_LOG_INFO_MESSAGE,
			AppFLow.REDIRECTION_EXCEPTION_DEFAULT_LOG_INFO_MESSAGE
	),
	CONFIRM_REGISTRATION(
			"Created registration confirmation URL for (LOCAL) env.",
			AppFLow.SUCCESSFUL_REDIRECTION_DEFAULT_LOG_INFO_MESSAGE,
			AppFLow.REDIRECTION_EXCEPTION_DEFAULT_LOG_INFO_MESSAGE
	),
	USER_LOGGED_IN_USING_GOOGLE(
			"Created user logged in using google URL for (LOCAL) env.",
			AppFLow.SUCCESSFUL_REDIRECTION_DEFAULT_LOG_INFO_MESSAGE,
			AppFLow.REDIRECTION_EXCEPTION_DEFAULT_LOG_INFO_MESSAGE
	),
	REGISTER_OAUTH2_PRINCIPAL(
			"Created register oauth2 principal URL for (LOCAL) env.",
			AppFLow.SUCCESSFUL_REDIRECTION_DEFAULT_LOG_INFO_MESSAGE,
			AppFLow.REDIRECTION_EXCEPTION_DEFAULT_LOG_INFO_MESSAGE
	);

	private final String determineUrlLogInfoMessage;
	private final String successfulRedirectionLogInfoMessage;
	private final String redirectionExceptionLogErrorMessage;

	@Getter(AccessLevel.NONE)
	private static final String SUCCESSFUL_REDIRECTION_DEFAULT_LOG_INFO_MESSAGE = "User successfully redirected to ({}) url.";
	@Getter(AccessLevel.NONE)
	private static final String REDIRECTION_EXCEPTION_DEFAULT_LOG_INFO_MESSAGE = "Cannot redirect user to ({}) url.";
}
