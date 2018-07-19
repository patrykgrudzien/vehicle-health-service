package me.grudzien.patryk.domain.dto.responses;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AuthenticationEntryPointResponse extends CustomResponse {

	@Builder(builderMethodName = "Builder")
	public AuthenticationEntryPointResponse(final String message, final String code, final String lastRequestedPath, final String lastRequestMethod) {
		super(message, code, lastRequestedPath, lastRequestMethod);
	}

	public static AuthenticationEntryPointResponse buildBodyMessage(final String localeMessage, final CustomResponse.Codes code) {
		return AuthenticationEntryPointResponse.Builder()
		                                       .message(localeMessage)
		                                       .code(code.getCodeMessage())
		                                       .build();
	}
}
