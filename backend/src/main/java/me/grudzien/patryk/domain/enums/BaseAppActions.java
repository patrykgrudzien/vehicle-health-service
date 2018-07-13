package me.grudzien.patryk.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BaseAppActions {

	USER_ALREADY_ENABLED("Created user already enabled URL for (LOCAL) env."),
	VERIFICATION_TOKEN_CREATION("Created verification token URL for (LOCAL) env."),
	CONFIRM_REGISTRATION("Created registration confirmation URL for (LOCAL) env.");

	private String logInfoMessage;
}
