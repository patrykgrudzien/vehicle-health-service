package me.grudzien.patryk.exceptions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "Builder")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionResponse {

	private String errorMessage;
	private List<String> errors;

	public static <T extends RuntimeException> ExceptionResponse buildGenericResponse(final T exception) {
		return ExceptionResponse.Builder()
		                        .errorMessage(exception.getMessage())
		                        .build();
	}
}

