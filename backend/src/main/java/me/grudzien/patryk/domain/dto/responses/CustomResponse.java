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

	public static final String CONTENT_TYPE_KEY = "Content-Type";
	public static final String CONTENT_TYPE_VALUE = "application/json;charset=UTF-8";
	public static final String CORS_KEY = "Access-Control-Allow-Origin";
	public static final String CORS_VALUE = "http://localhost:8080";

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
