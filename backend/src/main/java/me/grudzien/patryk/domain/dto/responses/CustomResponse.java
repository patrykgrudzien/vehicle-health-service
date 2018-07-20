package me.grudzien.patryk.domain.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class CustomResponse {

	private String message;
	private String code;
	private String lastRequestedPath;
	private String lastRequestMethod;

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

	@Getter
	@AllArgsConstructor
	public enum Headers {

		CONTENT_TYPE("Content-Type", "application/json;charset=UTF-8"),
		ACCESS_CONTROL_ALLOW_ORIGIN("Access-Control-Allow-Origin", "http://localhost:8080");

		private String key;
		private String value;
	}
}
