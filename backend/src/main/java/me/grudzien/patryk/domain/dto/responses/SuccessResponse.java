package me.grudzien.patryk.domain.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "Builder")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponse extends CustomResponse {

	private String message;

	public static SuccessResponse buildGenericResponse(final String successMessage) {
		return SuccessResponse.Builder()
		                      .message(successMessage)
		                      .build();
	}
}

