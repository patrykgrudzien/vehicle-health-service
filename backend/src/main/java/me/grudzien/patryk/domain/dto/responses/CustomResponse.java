package me.grudzien.patryk.domain.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude;

import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser.AccountStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class CustomResponse {

    @Getter
    @AllArgsConstructor
    public enum ResponseProperties {
        // value must be the same as "message" field in CustomResponse class!
        MESSAGE("message"),
        ACCOUNT_STATUS("accountStatus");

        private final String property;
    }

	private String message;
	private SecurityStatus securityStatus;
	private AccountStatus accountStatus;
	private String lastRequestedPath;
	private String lastRequestMethod;

	CustomResponse(final String message) {
		this.message = message;
	}

	@Getter
	@AllArgsConstructor
    @JsonFormat(shape = Shape.OBJECT)
	public enum SecurityStatus {

        JWT_TOKEN_EXPIRED("JWT_TOKEN_EXPIRED", "JWT Token Expired!"),
        UNAUTHENTICATED("UNAUTHENTICATED", "Unauthenticated! (access_token) has NOT been provided with the request!");

        private final String securityStatusCode;
        private final String securityStatusDescription;
	}

	@Getter
	@AllArgsConstructor
	public enum Headers {

		CONTENT_TYPE("Content-Type", "application/json;charset=UTF-8"),
		ACCESS_CONTROL_ALLOW_ORIGIN("Access-Control-Allow-Origin", "http://localhost:8080");

		private final String key;
		private final String value;
	}
}
