package me.grudzien.patryk.domain.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

public abstract class CustomResponse {

	public static final String CONTENT_TYPE_KEY = "Content-Type";
	public static final String CONTENT_TYPE_VALUE = "application/json;charset=UTF-8";

	@Getter
	@AllArgsConstructor
	public enum Codes {

		JWT_TOKEN_EXPIRED("JWT TOKEN EXPIRED"),
		SECURED_RESOURCE_CODE("Unauthenticated");

		private String codeMessage;
	}
}
