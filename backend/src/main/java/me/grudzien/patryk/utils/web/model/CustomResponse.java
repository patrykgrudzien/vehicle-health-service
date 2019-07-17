package me.grudzien.patryk.utils.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser.AccountStatus;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.OBJECT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
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
    @JsonFormat(shape = OBJECT)
	public enum SecurityStatus {

        JWT_TOKEN_EXPIRED("JWT_TOKEN_EXPIRED", "JWT Token Expired!"),
        UNAUTHENTICATED("UNAUTHENTICATED", "Unauthenticated! (access_token) has NOT been provided with the request!"),
        ILLEGAL_ARGUMENT("ILLEGAL_ARGUMENT", "An error occurred during getting email from token!"),
        NO_CRYPTOGRAPHICALLY_SIGNED_TOKEN("NO_CRYPTOGRAPHICALLY_SIGNED_TOKEN", "Application requires JWT token with cryptographically signed Claims!"),
        JWT_TOKEN_NOT_CORRECTLY_CONSTRUCTED("JWT_TOKEN_NOT_CORRECTLY_CONSTRUCTED", "JWT token has NOT been correctly constructed!"),
        JWT_TOKEN_INCORRECT_SIGNATURE("JWT_TOKEN_INCORRECT_SIGNATURE", "Either calculating a signature or verifying an existing signature of a JWT failed!");

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
