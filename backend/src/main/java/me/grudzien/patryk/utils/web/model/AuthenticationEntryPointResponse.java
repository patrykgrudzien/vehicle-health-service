package me.grudzien.patryk.utils.web.model;

import lombok.Builder;
import lombok.NoArgsConstructor;

import me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser.AccountStatus;

@NoArgsConstructor
public class AuthenticationEntryPointResponse extends CustomResponse {

	@Builder(builderMethodName = "Builder")
	public AuthenticationEntryPointResponse(final String message, final SecurityStatus securityStatus, final AccountStatus accountStatus,
                                            final String lastRequestedPath, final String lastRequestMethod) {
		super(message, securityStatus, accountStatus, lastRequestedPath, lastRequestMethod);
	}

	public static AuthenticationEntryPointResponse buildBodyMessage(final String localeMessage, final SecurityStatus securityStatus) {
		return AuthenticationEntryPointResponse.Builder()
		                                       .message(localeMessage)
		                                       .securityStatus(securityStatus)
		                                       .build();
	}
}
