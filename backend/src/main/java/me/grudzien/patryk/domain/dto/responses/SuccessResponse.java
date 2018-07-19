package me.grudzien.patryk.domain.dto.responses;

import lombok.Builder;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;

@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponse extends CustomResponse {

	@Builder(builderMethodName = "Builder")
	public SuccessResponse(final String message) {
		super(message);
	}

	@Builder(builderMethodName = "FullBuilder")
	public SuccessResponse(final String message, final String code, final String lastRequestedPath) {
		super(message, code, lastRequestedPath);
	}

	public static SuccessResponse buildBodyMessage(final String successMessage) {
		return SuccessResponse.Builder()
		                      .message(successMessage)
		                      .build();
	}
}

