package me.grudzien.patryk.domain.dto.responses;

import lombok.Builder;
import lombok.NoArgsConstructor;

import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser.AccountStatus;

@NoArgsConstructor
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

