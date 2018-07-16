package me.grudzien.patryk.domain.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "Builder")
public class AuthenticationEntryPointResponse extends CustomResponse {

	private String code;
	private String message;

	public static AuthenticationEntryPointResponse buildLocaleMessageWithExceptionCode(final String localeMessage,
	                                                                                   final CustomResponse.Codes code) {
		return AuthenticationEntryPointResponse.Builder()
		                                       .message(localeMessage)
		                                       .code(code.getCodeMessage())
		                                       .build();
	}
}
