package me.grudzien.patryk.domain.dto.responses;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SuccessResponse extends CustomResponse {

	@Builder(builderMethodName = "Builder")
	public SuccessResponse(final String message) {
		super(message);
	}

	@Builder(builderMethodName = "FullBuilder")
	public SuccessResponse(final String message, final String code, final String lastRequestedPath, final String lastRequestMethod) {
		super(message, code, lastRequestedPath, lastRequestMethod);
	}

	public static SuccessResponse buildBodyMessage(final String successMessage) {
		return SuccessResponse.Builder()
		                      .message(successMessage)
		                      .build();
	}
}

