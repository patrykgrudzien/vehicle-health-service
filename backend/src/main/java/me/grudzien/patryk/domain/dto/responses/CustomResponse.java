package me.grudzien.patryk.domain.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class CustomResponse {

	private String message;
	private String code;
	private String lastRequestedPath;

	public static final String CONTENT_TYPE_KEY = "Content-Type";
	public static final String CONTENT_TYPE_VALUE = "application/json;charset=UTF-8";

	CustomResponse(final String message) {
		this.message = message;
	}

	@Getter
	@AllArgsConstructor
	public enum Codes {

		JWT_TOKEN_EXPIRED("JWT TOKEN EXPIRED"),
		SECURED_RESOURCE_CODE("Unauthenticated");

		private String codeMessage;
	}
}
