package me.grudzien.patryk.utils.web.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import lombok.Builder;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;

import me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser.AccountStatus;

@NoArgsConstructor
@JsonInclude(NON_NULL)
public class SuccessResponse extends CustomResponse {

	public SuccessResponse(final String message) {
		super(message);
	}

	@Builder(builderMethodName = "FullBuilder")
	public SuccessResponse(final String message, final SecurityStatus securityStatus, final AccountStatus accountStatus, final String lastRequestedPath,
                           final String lastRequestMethod) {
		super(message, securityStatus, accountStatus, lastRequestedPath, lastRequestMethod);
	}

	public static SuccessResponse buildBodyMessage(final AccountStatus accountStatus) {
		return SuccessResponse.FullBuilder()
                              .accountStatus(accountStatus)
		                      .build();
	}
}

