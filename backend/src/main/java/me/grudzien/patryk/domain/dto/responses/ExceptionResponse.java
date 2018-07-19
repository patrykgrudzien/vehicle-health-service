package me.grudzien.patryk.domain.dto.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionResponse extends CustomResponse {

	private List<String> errors;

	@Builder(builderMethodName = "Builder")
	public ExceptionResponse(final String message, final String code, final String lastRequestedPath, final List<String> errors) {
		super(message, code, lastRequestedPath);
		this.errors = errors;
	}

	public static <T extends RuntimeException> ExceptionResponse buildBodyMessage(final T exception) {
		return ExceptionResponse.Builder()
		                        .message(exception.getMessage())
		                        .build();
	}

	public static <T extends RuntimeException> ExceptionResponse buildBodyMessage(final T exception, final CustomResponse.Codes code) {
		return ExceptionResponse.Builder()
		                        .message(exception.getMessage())
		                        .code(code.getCodeMessage())
		                        .build();
	}

	public static <T extends RuntimeException> ExceptionResponse buildBodyMessage(final T exception, final CustomResponse.Codes code,
	                                                                              final String lastRequestedPath) {
		return ExceptionResponse.Builder()
		                        .message(exception.getMessage())
		                        .code(code.getCodeMessage())
		                        .lastRequestedPath(lastRequestedPath)
		                        .build();
	}
}

